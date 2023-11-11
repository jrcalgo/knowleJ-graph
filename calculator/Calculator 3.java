package calculator;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.LogicalPropositions;

public class Calculator {
    
    public static void main(String[] args) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        LogicalPropositions prop1 = new LogicalPropositions("A&B");
        LogicalPropositions prop2 = new LogicalPropositions("A|B");
        LogicalPropositions prop3 = new LogicalPropositions("A->B");
    }
}
