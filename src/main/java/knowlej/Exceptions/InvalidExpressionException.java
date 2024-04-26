package knowlej.Exceptions;

public class InvalidExpressionException extends Exception {
    
    public InvalidExpressionException() {
        super("Invalid mathematical expression");
    }

    public InvalidExpressionException(String e) {
        super("Invalid mathematical expression: " + e);
    }
}
