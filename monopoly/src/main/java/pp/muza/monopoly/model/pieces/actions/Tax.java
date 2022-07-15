package pp.muza.monopoly.model.pieces.actions;

import static pp.muza.monopoly.model.pieces.actions.CardUtils.createContractsForPlayerPossession;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.errors.BankException;

/**
 * A player has to pay money to the bank.
 * <p>
 * if the player is in jail, successfully pay the bill will allow to end the
 * turn.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Tax extends BaseActionCard {

    private final BigDecimal amount;

    Tax(BigDecimal amount) {
        super("Tax", Action.TAX, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.amount = amount;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.payTax(amount);
            result = ImmutableList.of();
        } catch (BankException e) {
            return ImmutableList.<ActionCard>builder().add(this).addAll(createContractsForPlayerPossession(turn))
                    .build();
        }
        return result;
    }
}