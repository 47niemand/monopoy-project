package pp.muza.monopoly.errors;

public enum TurnError {
    // TODO: add java doc
    PLAYER_IS_NOT_IN_JAIL_CANNOT_PAY_TAX("Player is not in jail, cannot pay tax"),
    THE_CARD_IS_NOT_ACTIVE("The card is not active."),
    THE_CARD_IS_NOT_IN_THE_PLAYER_S_HAND("The card is not in the player's hand."),
    THE_CARD_IS_NULL("The card is null."),
    THE_TURN_IS_FINISHED("The turn is finished.");

    private final String message;

    TurnError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
