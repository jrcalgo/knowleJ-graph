package targets;

import java.util.ArrayList;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.LogicalPropositions;

public class Calculator {
    
    public static void main(String[] args) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        LogicalPropositions e = new LogicalPropositions("(P&Q| ~F )->Z<>(Y&~R<>P>-<Q)");
        System.out.println(e.getExpression());
        System.out.println(e.getConvertedExpression());
        System.out.println(e.getPropositions());
        System.out.println(e.getPropositions(0,3));
        System.out.println(e.getPropositions(3, 5));
        System.out.println(e.getPropositions(1,1));
        e.printTruthTable();

    }
}
