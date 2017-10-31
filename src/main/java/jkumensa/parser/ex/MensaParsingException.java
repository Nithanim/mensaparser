package jkumensa.parser.ex;

public abstract class MensaParsingException extends RuntimeException {
    public MensaParsingException(String message) {
        super(message);
    }

    public MensaParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MensaParsingException(Throwable cause) {
        super(cause);
    }

}
