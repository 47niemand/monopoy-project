package pp.muza.monopoly.model.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import pp.muza.monopoly.model.game.IndexedEntry;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Property;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SpawnGiftCard extends AbstractActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(SpawnGiftCard.class.getName());

    SpawnGiftCard() {
        super("Spawn BuyOrTrade cards", Action.GIFT, Type.OBLIGATION, HIGH_PRIORITY);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("Spawning BuyOrTrade cards");
        List<IndexedEntry<Property>> properties = turn.getFreeProperties();
        if (properties.isEmpty()) {
            // if there are no free properties, the player has to choose one of the properties he now owns
            properties = turn.getAllProperties().stream().filter(x -> turn.getPropertyOwner(x.getIndex()) != turn.getPlayer()).collect(Collectors.toList());
        }
        return properties.stream().map(x -> new BuyOrTrade(x.getIndex(), x.getValue())).collect(Collectors.toList());
    }
}