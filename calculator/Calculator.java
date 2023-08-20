package calculator;

import java.util.ArrayList;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.LogicalPropositions;

public class Calculator {
    
    public static void main(String[] args) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        ArrayList<LogicalPropositions> props = new ArrayList<LogicalPropositions>();
        props.add(new LogicalPropositions("A&B"));
        props.add(new LogicalPropositions("A|B"));
        props.add(new LogicalPropositions("A->(B&~C)"));
    }

    public LogicalPropositions convertBoolAlgebraToPropositions(String expression) {
    }

    public 
}
