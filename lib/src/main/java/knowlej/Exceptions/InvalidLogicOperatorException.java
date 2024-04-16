package lib.src.main.java.knowlej.Exceptions;

public class InvalidLogicOperatorException extends Exception {
    
    public InvalidLogicOperatorException() {
        super("Invalid logical operator(s)");
    }

    public InvalidLogicOperatorException(String expression) {
        super("Invalid logical operator(s): " + expression);
    }
}
