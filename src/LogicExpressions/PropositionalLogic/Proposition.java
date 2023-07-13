package src.LogicExpressions.PropositionalLogic;
import java.util.Stack;
import java.util.Queue;
import java.nio.charset.Charset;
import java.util.List;
import src.Exceptions.*;
import src.LogicExpressions.PropositionalLogic.Operators.LogicalOperators;

public class Proposition<T> extends LogicalOperators {
    private Queue<Character> expressionBuffer;

    private String expression;
    private char[] propositionalElements;
    private String[] propositionalStatements;

    private final boolean TRUE = true;
    private final boolean FALSE = false;

    public Proposition() {
        this.expression = "(P&Q)|(R->S)";
        this.propositionalElements = this.expression.toCharArray();
        this.propositionalStatements = parseStatements(this.propositionalElements);

    }

    public Proposition(String expression) throws InvalidLogicOperatorException, InvalidOperandException {
        throwILOEIfNecessary(expression);
        throwIOpEIfNecessary(expression);

        this.expression = expression;
        this.propositionalElements = this.expression.toCharArray();
        this.propositionalStatements = parseStatements(this.propositionalElements);
    }

    private void throwILOEIfNecessary(String e) throws InvalidLogicOperatorException {
        if (e.contains("+") || e.contains("*") || e.contains("/") || e.contains("%"))
            throw new InvalidLogicOperatorException(expression);
    }

    private void throwIOpEIfNecessary(String e) {
        if ()
    }

    private String[] parseStatements(char[] e) {
        int count = 0;
        for (int i = e.length; i >= 0; i--) {
            if (e[i] != '(') {
                expressionBuffer.add(e[i]);
                count++;
            } else if (e[i] == '(') {
                expressionBuffer.add(e[i]);
                for (int j = 1; j < count; j++) {

                }
                count = 0;
            }

        }       
    }

    private void sortStatements(String[] s) {
        
    }






    public void setExpression(String expression) {
        this.expression = expression;

    }

    public String getExpression() {
        return this.expression;
    }
    
    
    public boolean evaluate() {

    }

    public String truthTable() {
        boolean[][] table;

    }
    

    

    @Override
    public String toString() {
        return expression + "," + ;
    }
}
