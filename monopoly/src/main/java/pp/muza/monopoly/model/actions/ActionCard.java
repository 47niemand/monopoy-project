package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.Turn;

/**
 * ActionThe action card is a card that can be used by the player.
 * The action card has a name, a type of action (like buying a property, paying
 * rent, etc.), a type of card (like chance, obligation, etc.), and a priority.
 * onExecute is a method that is executed when the card is using. It should be
 * overridden by the subclasses. The method execute should be called by the
 * {@link Turn}.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class ActionCard  {

    public static final int HIGH_PRIORITY = 0;
    public static final int NEW_TURN_PRIORITY = 100;
    public static final int DEFAULT_PRIORITY = 1000;
    public static final int LOW_PRIORITY = 10000;

    private static final Logger LOG = LoggerFactory.getLogger(ActionCard.class);

    private final String name;
    private final Action action;
    private final Type type;

    @EqualsAndHashCode.Exclude
    private final int priority;

    protected ActionCard(String name, Action action, Type type, int priority) {
        this.name = name;
        this.action = action;
        this.priority = priority;
        this.type = type;
    }

    protected abstract List<ActionCard> onExecute(Turn turn);

    public enum Type {
        OPTIONAL(false), // optional card, player can choose to use it
        OBLIGATION(true), // obligation card, player must use it
        CHANCE(true),  // chance card, player must use it, this type of card can be used only once
        KEEPABLE(false), // keepable card, player can keep it and use it later
        CONTRACT(false); // Using the contract card to sell a property is optional and up to the player.

        private final boolean mandatory;

        Type(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public boolean isMandatory() {
            return mandatory;
        }

    }

    public enum Action {
        NEW_TURN, // New turn
        ROLL_DICE, // roll dice get random number
        MOVE, // move to next land
        ARRIVAL, // player arrives to a land
        BUY, // buy property
        PAY, // pay to player (rent, gift, etc.)
        TAX, // pay tax or fine to bank
        CONTRACT, // contract
        GO_TO_JAIL, // go to jail
        CHANCE, // get chance card, this is a special card that stores the chance pile of the
                // game. it should be returned to the game when the card is used.
        INCOME, // get income
        END_TURN, // end turn
        GIFT // get gift
        ;

        Action() {
        }
    }
}
