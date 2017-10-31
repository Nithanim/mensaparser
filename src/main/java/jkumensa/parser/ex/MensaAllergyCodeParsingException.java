package jkumensa.parser.ex;

public class MensaAllergyCodeParsingException extends MensaParsingException {
    public MensaAllergyCodeParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MensaAllergyCodeParsingException(Throwable cause) {
        super(cause);
    }
}
