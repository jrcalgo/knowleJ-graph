public class InvalidLogicOperatorException extends Exception {
    
    public InvalidLogicOperatorException() {
        super("Invalid logical operators have been passed.");
    }

    public InvalidLogicOperatorException(String expression) {
        super("Invalid logical operators have been passed into " + expression + ", please use correct logical operators.");
    }
}
