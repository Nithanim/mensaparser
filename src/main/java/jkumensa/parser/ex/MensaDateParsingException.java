package jkumensa.parser.ex;

public class MensaDateParsingException extends MensaParsingException {
    public MensaDateParsingException(String message) {
        super(message);
    }

    public MensaDateParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MensaDateParsingException(Throwable cause) {
        super(cause);
    }
}
