package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import pp.muza.monopoly.model.Fortune;

/**
 * This class represents an action that can be executed by a player.
 *
 * @author dmitro.muza
 */
public enum Action {
    /**
     * Player arrives to a land and should use this action card.
     */
    ARRIVAL(ImmutableList.of(Arrival.class, Takeover.class)),
    /**
     * Any property-related action in which the player can buy a property.
     */
    BUY(ImmutableList.of(Buy.class)),
    /**
     * This is a specific card that stores the chance pile of the game.
     * <p>It should be returned to the game when the card is used.
     * A Card with this type must implement {@link Fortune} interface.
     * </p>
     */
    CHANCE(ImmutableList.of(FortuneCard.class)),
    /**
     * Contract, any property-related activity in which the player can sale a property.
     */
    CONTRACT(ImmutableList.of(Contract.class)),
    /**
     * End turn.
     */
    END_TURN(ImmutableList.of(EndTurn.class)),
    /**
     * Get a gift.
     */
    GIFT(ImmutableList.of(SpawnGiftCard.class, TakeFortuneCard.class)),
    /**
     * Go to jail.
     */
    GO_TO_JAIL(ImmutableList.of(GoToJail.class)),
    /**
     * Get income.
     */
    INCOME(ImmutableList.of(Income.class, GoReward.class, ReceiveMoney.class, RentRevenue.class)),
    /**
     * Move forward by the number of steps.
     */
    MOVE(ImmutableList.of(Move.class, OptionMove.class)),
    /**
     * Move to the land with the given id.
     */
    MOVE_TO(ImmutableList.of(MoveTo.class, OptionMoveTo.class, MoveAndTakeover.class)),
    /**
     * New turn, the player starts a new turn with this card.
     */
    NEW_TURN(ImmutableList.of(NewTurn.class)),
    /**
     * Birthday party.
     */
    PARTY(ImmutableList.of(BirthdayParty.class)),
    /**
     * Player pays the given number of coins to other players or the bank.
     */
    DEBT(ImmutableList.of(PayRent.class, Gift.class, JailFine.class, Tax.class)),
    /**
     * Roll dice get random number.
     */
    ROLL_DICE(ImmutableList.of(RollDice.class));

    @Getter
    private final List<Class<? extends BaseActionCard>> classList;

    Action(List<Class<? extends BaseActionCard>> list) {
        this.classList = ImmutableList.copyOf(list);
    }
}
