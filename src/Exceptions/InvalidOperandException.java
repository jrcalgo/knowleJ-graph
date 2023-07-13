package src.Exceptions;

public class InvalidOperandException extends Exception {
    
    public InvalidOperandException() {
        super("Invalid operand(s) detected, pass valid operands.");
    }

    public InvalidOperandException(String expression) {
        this();
    }
}
