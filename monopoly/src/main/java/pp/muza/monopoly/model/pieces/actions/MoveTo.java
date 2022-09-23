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
import pp.muza.monopoly.model.Turn;

/**
 * A player moves to a new position on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MoveTo extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(MoveTo.class);

    protected final int landId;

    protected MoveTo(String name, ActionType type, int priority, int landId) {
        super(name, Action.MOVE_TO, type, priority);
        this.landId = landId;
    }

    MoveTo(int landId) {
        this("Move To", ActionType.OBLIGATION, DEFAULT_PRIORITY, landId);
    }

    public static ActionCard of(int landId) {
        return new MoveTo(landId);
    }

    /**
     * A method called when the player arrives at a new location on the board.
     * <p>can be overridden by subclasses to perform additional actions.</p>
     *
     * @param turn     the current turn
     * @return the action cards to execute after the player arrives at the new location.
     */
    protected List<ActionCard> onArrival(Turn turn) {
        return ImmutableList.of(new Arrival(landId));
    }

    @Override
    protected final List<ActionCard> onExecute(Turn turn) {
        LOG.info("{} moved to {} ({})", turn.getPlayer().getName(), landId, turn.getLand(landId).getName());
        List<Land> path = null;
        try {
            path = turn.moveTo(landId);
        } catch (pp.muza.monopoly.errors.TurnException e) {
            throw new RuntimeException(e);
        }
        if (path.size() == 0) {
            LOG.warn("Staying on the same land");
        }
        return ImmutableList.<ActionCard>builder()
                .addAll(CardUtils.onPath(turn, path))
                .addAll(onArrival(turn))
                .build();
    }

}
