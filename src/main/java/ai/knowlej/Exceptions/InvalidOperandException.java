package knowlej.Exceptions;

public class InvalidOperandException extends Exception {
    
    public InvalidOperandException() {
        super("Invalid operand(s) detected, pass valid operands.");
    }

    public InvalidOperandException(String expression) {
        super("Invalid operand(s): " + expression);
    }
}
