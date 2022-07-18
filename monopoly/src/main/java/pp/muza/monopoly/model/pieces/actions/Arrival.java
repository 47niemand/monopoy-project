package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;


/**
 * This is a special card which spawns action cards for the player, when player arrives at a land.
 * - when player arrives at the property, depending on the property owner, he/she can buy it, or he/she should pay rent.
 * - when player arrives at the goto jail, he/she should move to jail.
 * - when player arrives at the chance, he/she should draw a card.
 * - etc.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Arrival extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Arrival.class);

    private final int position;

    Arrival(int position) {
        super("Arrival", Action.ARRIVAL, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.position = position;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        Land land = turn.getLand(position);
        List<ActionCard> result;
        switch (land.getType()) {
            case PROPERTY:
                Player owner = turn.getPropertyOwner(position);
                if (owner == null) {
                    LOG.info("Property {} is not owned by anyone, player can buy it", land.getName());
                    result = ImmutableList.of(new Buy(position));
                } else if (owner != turn.getPlayer()) {
                    LOG.info("Property {} is owned by {}, player should pay rent", land.getName(), owner.getName());
                    result = ImmutableList.of(new PayRent(owner, position));
                } else {
                    LOG.info("Property {} is owned player, nothing to do", land.getName());
                    result = ImmutableList.of();
                }
                break;
            case GOTO_JAIL:
                result = ImmutableList.of(new GoToJail());
                break;
            case CHANCE:
                result = ImmutableList.of(turn.popFortuneCard());
                break;
            default:
                result = ImmutableList.of();
                break;
        }
        return result;
    }

}
