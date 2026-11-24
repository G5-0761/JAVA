package exception;

// Our own exception: thrown when amount or quantity is zero or negative
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}