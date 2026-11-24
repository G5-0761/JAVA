package exception;

// Thrown when the user enters a currency we don't have a rate for
public class InvalidCurrencyException extends Exception {
    public InvalidCurrencyException(String message) {
        super(message);
    }
}