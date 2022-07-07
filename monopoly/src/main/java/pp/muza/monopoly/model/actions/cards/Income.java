package pp.muza.monopoly.model.actions.cards;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;

/**
 * The player receives money from this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Income extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Income.class);

    private final BigDecimal amount;

    Income(BigDecimal amount) {
        super("Income", Action.INCOME, Type.OBLIGATION, HIGH_PRIORITY);
        this.amount = amount;
    }

    public static ActionCard of(BigDecimal amount) {
        return new Income(amount);
    }



    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> res;
        try {
            turn.addMoney(amount);
            res = ImmutableList.of(new EndTurn());
        } catch (BankException e) {
            LOG.info("Player cannot receive money: {}", e.getMessage());
            res = ImmutableList.of();
        }
        return res;
    }
}
