package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The player has to pay money to other player.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayGift extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(PayGift.class);

    private final Player recipient;
    private final BigDecimal amount;

    PayGift(Player recipient, BigDecimal amount) {
        super("Pay Gift", Action.PAY, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.recipient = recipient;
        this.amount = amount;
    }

    public static ActionCard of(Player recipient, BigDecimal amount) {
        return new PayGift(recipient, amount);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.payRent(recipient, amount);
            result = ImmutableList.of();
        } catch (BankException e) {
            LOG.info("Player cannot pay money: {}", e.getMessage());
            result = new ArrayList<>();
            result.add(this);
            result.addAll(PayRent.createContractsForPlayerPossession(turn));
        }
        return result;
    }


}
