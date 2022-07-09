package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;

import static pp.muza.monopoly.model.actions.CardUtils.onArrival;

/**
 * The player specifies the distance to take to move to a new location on the board.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Move extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Move.class);

    private final int distance;

    Move(int distance) {
        super("Move", Action.MOVE, Type.OBLIGATION, DEFAULT_PRIORITY);
        assert distance > 0;
        this.distance = distance;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        int position = turn.nextPosition(distance);
        LOG.info("{}: moving by {} steps to {} ({})", turn.getPlayer().getName(), distance, position, turn.getLand(position).getName());
        List<ActionCard> res;
        List<Land> path = turn.moveTo(position);
        assert path.size() == distance;
        res = onArrival(turn, path, position);
        return res;

    }
}
