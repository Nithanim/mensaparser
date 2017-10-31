package jkumensa.parser.ex;

public class MensaFoodCharacteristicParsingException extends MensaParsingException {
    public MensaFoodCharacteristicParsingException(String message) {
        super(message);
    }

    public MensaFoodCharacteristicParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
