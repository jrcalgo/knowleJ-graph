import java.util.Stack;
import java.util.List;

public class Proposition<T extends Comparable<T>> {
    private Stack<T> propositionBuffer;
    private Stack<T> operatorBuffer;

    private String expression;

    private char[] propositionalStatements;

    private final boolean TRUE = true;
    private final boolean FALSE = false;

    public Proposition() {
        propositionBuffer = null;
        expression = null;
        propositionalStatements = null;
    }

    public Proposition(String expression) throws InvalidLogicOperatorException {
        propositionBuffer = null;
        this.expression = expression;
        propositionalStatements = null;
    }

    public Proposition(char[] proposition) {

    }

    private void throwILOEIfNecessary() throws InvalidLogicOperatorException {
        if (expression)
    }

    public void setStatement(){}

    public String truthTable() {

    }

    public 

    @Override
    public String toString() {
        return expression + "," + ;
    }
}