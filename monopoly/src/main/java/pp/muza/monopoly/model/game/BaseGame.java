package pp.muza.monopoly.model.game;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.data.PlayerInfo;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.*;
import pp.muza.monopoly.model.pieces.actions.NewTurn;
import pp.muza.monopoly.model.turn.TurnImpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class BaseGame {

    static final Logger LOG = LoggerFactory.getLogger(BaseGame.class);

    static final int DEFAULT_MAX_TURNS = 150;
    final Bank bank;
    final LinkedList<Fortune> fortuneCards;
    final List<Player> players = new ArrayList<>();
    final Map<Player, PlayerData> playerData = new HashMap<>();
    final Map<Integer, Player> propertyOwners = new HashMap<>();
    final Board board;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns = DEFAULT_MAX_TURNS;

    protected BaseGame(Bank bank, List<Fortune> fortuneCards, Board board) {
        this.bank = bank;
        this.fortuneCards = new LinkedList<>(fortuneCards);
        this.board = board;
    }

    List<Fortune> getFortuneCards() {
        return ImmutableList.copyOf(fortuneCards);
    }

    void setPlayerPosition(Player player, int position) {
        playerData.get(player).setPosition(position);
    }

    List<ActionCard> getPlayerCards(Player player) {
        return ImmutableList.copyOf(playerData.get(player).getActionCards());
    }

    void playTurn(Turn turn) {
        Player player = turn.getPlayer();
        List<String> list = playerData.get(player).getActionCards().stream().map(ActionCard::getName)
                .collect(Collectors.toList());
        LOG.info("Player's {} action cards: {}", player.getName(), list);
        Strategy strategy = playerData.get(player).getStrategy();
        strategy.playTurn(turn);
        if (!turn.isFinished()) {
            LOG.info("Player {} is not finished the turn", player);
            try {
                turn.endTurn();
            } catch (TurnException e) {
                LOG.error("Error in turn", e);
                throw new RuntimeException(e);
            }
        }
    }

    Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    int getNextPlayerId() {
        int temp = currentPlayerIndex;
        do {
            temp++;
            if (temp >= players.size()) {
                temp = 0;
            }
        } while (playerData.get(players.get(temp)).getStatus().isFinal() && temp != currentPlayerIndex);
        return temp;
    }

    boolean nextPlayer() {
        int temp = getNextPlayerId();
        boolean result = temp != currentPlayerIndex;
        currentPlayerIndex = temp;
        if (result) {
            LOG.info("Next player: {}", players.get(currentPlayerIndex).getName());
        } else {
            LOG.info("No next player");
        }
        return result;
    }

    PlayerInfo getPlayerInfo(Player player) {
        PlayerData playerData = this.playerData.get(player);
        List<Integer> playerProperties = propertyOwners.entrySet().stream()
                .filter(entry -> entry.getValue() == player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<IndexedEntry<Property>> belongings = playerProperties.stream().map(x -> new IndexedEntry<>(x, (Property) board.getLand(x)))
                .collect(Collectors.toList());
        return new PlayerInfo(player, playerData.getPosition(), playerData.getStatus(), bank.getBalance(player),
                ImmutableList.copyOf(playerData.getActionCards()), belongings);
    }

    List<Land> getLands() {
        return board.getLands();
    }

    List<Land> getLands(List<Integer> path) {
        return board.getLands(path);
    }

    List<Integer> getPathTo(int startPos, int endPos) {
        return board.getPathTo(startPos, endPos);
    }

    void deposit(Player player, BigDecimal amount) throws BankException {
        bank.deposit(player, amount);
    }

    void withdraw(Player player, BigDecimal amount) throws BankException {
        bank.withdraw(player, amount);
    }

    void setPropertyOwner(int landId, Player player) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now owned by {}", landId, property.getName(), player.getName());
        Player oldOwner = propertyOwners.put(landId, player);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    void getBackChanceCard(ActionCard card) {
        if (card.getAction() != ActionCard.Action.CHANCE) {
            throw new IllegalArgumentException("Not a chance card");
        }
        assert card instanceof Fortune;
        Fortune fortune = (Fortune) card;
        LOG.info("Fortune card {} returned", card.getName());
        assert !fortuneCards.contains(fortune);
        fortuneCards.addLast(fortune);
    }

    void propertyOwnerRemove(int landId) {
        Property property = (Property) board.getLand(landId);
        LOG.info("Property {} ({}) is now free", landId, property.getName());
        Player oldOwner = propertyOwners.remove(landId);
        if (oldOwner != null) {
            LOG.info("{} lost property {} ({})", oldOwner.getName(), landId, property.getName());
        }
    }

    void bringFortuneCardToTop(Fortune.Chance card) {
        Fortune fortune = removeFortuneCard(card);
        fortuneCards.addFirst(fortune);
    }

    Fortune removeFortuneCard(Fortune.Chance chance) {
        Fortune result;
        // find fortune by given chance
        OptionalInt index = IntStream.range(0, fortuneCards.size())
                .filter(i -> fortuneCards.get(i).getChance() == chance)
                .findFirst();
        if (index.isPresent()) {
            LOG.info("Fortune card {} removed from pile", chance.name());
            result = fortuneCards.remove(index.getAsInt());
        } else {
            LOG.error("Fortune card {} not found", chance.name());
            result = null;
        }
        return result;
    }

    void getBackAllChanceCards(Player player) {
        playerData.get(player).actionCards.removeIf(x -> {
                    boolean found = false;
                    if (x.getAction() == ActionCard.Action.CHANCE) {
                        // return chance card to pile
                        getBackChanceCard(x);
                        found = true;
                    }
                    return found;
                }
        );
    }

    void getBackAllPlayerCards(Player player) {
        playerData.get(player).actionCards.removeIf(x -> {
                    boolean found = x.getType() != ActionCard.Type.KEEPABLE;
                    if (found) {
                        LOG.info("{} lost action card {}", player.getName(), x.getName());
                        if (x.getAction() == ActionCard.Action.CHANCE) {
                            getBackChanceCard(x);
                        }
                    }
                    return found;
                }
        );
    }

    void setPlayerStatus(Player player, PlayerStatus status) {
        playerData.get(player).setStatus(status);
    }

    Board getBoard() {
        return board;
    }

    int getTurnNumber() {
        return turnNumber;
    }

    int getMaxTurns() {
        return maxTurns;
    }

    void endGame() {
        LOG.info("Game ended");
        for (Player player : players) {
            getBackAllChanceCards(player);
        }
    }

    public void gameLoop() {
        do {
            turnNumber++;
            Player player = getCurrentPlayer();
            GameImpl.LOG.info("PlayTurn {} - Player {}", turnNumber, player.getName());
            Turn turn = turn(player);
            playerData.get(player).getActionCards().add(NewTurn.of());
            playTurn(turn);

            if (turnNumber >= maxTurns) {
                GameImpl.LOG.info("Game loop ended after {} turns", turnNumber);
                break;
            }
        } while (nextPlayer());
        // get player with maximum money
        Player winner = players.stream()
                .filter(x -> !playerData.get(x).getStatus().isFinal())
                .max(Comparator.comparing(bank::getBalance))
                .orElseThrow(() -> new RuntimeException("No winner"));
        GameImpl.LOG.info("Winner: " + winner.getName());
        // print results
        players.forEach(x -> GameImpl.LOG.info("{} - {}", x.getName(), getPlayerInfo(x)));
    }


    abstract Turn turn(Player player);

    @Data
    protected static final class PlayerData {
        private final Player player;
        private final List<ActionCard> actionCards;
        private PlayerStatus status;
        private int position;
        private Strategy strategy;

        PlayerData(Player player, PlayerStatus status, int position, Strategy strategy) {
            this.player = player;
            this.status = status;
            this.position = position;
            this.actionCards = new ArrayList<>();
            this.strategy = strategy;
        }

        PlayerData(Player player, PlayerStatus status, int position, Strategy strategy, List<ActionCard> actionCards) {
            this(player, status, position, strategy);
            this.actionCards.addAll(actionCards);
        }

        void setPosition(int position) {
            if (this.position != position) {
                LOG.info("{}: changing position from {} to {}", this.player.getName(), this.position, position);
                this.position = position;
            } else {
                LOG.info("{} at position {}", this.player.getName(), this.position);
            }
        }

        void setStatus(PlayerStatus status) {
            assert status != null;
            if (this.status == null) {
                LOG.debug("{} set status to {}", player.getName(), status);
            } else {
                LOG.info("{}: changing status from {} to {}", this.player.getName(), this.status, status);
            }
            this.status = status;
        }
    }

}
