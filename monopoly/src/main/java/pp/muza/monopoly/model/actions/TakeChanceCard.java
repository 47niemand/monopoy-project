package pp.muza.monopoly.model.actions;

import com.google.common.collect.ImmutableList;
import pp.muza.monopoly.model.game.Turn;

import java.util.List;

public final class TakeChanceCard extends AbstractActionCard {

    TakeChanceCard() {
        super("Take Chance Card", Action.CHANCE, Type.CHANCE, DEFAULT_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        return ImmutableList.of(turn.popChanceCard());
    }
}