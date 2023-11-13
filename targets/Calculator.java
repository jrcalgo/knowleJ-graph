package targets;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.Propositions;

public class Calculator {
    
    public static void main(String[] args) throws IOException, InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        //Propositions e = new Propositions("(P&Q| ~V )->Z<>(Y&~R<>P>-<Q)");
        Propositions e = new Propositions("~(P&~(~Q|(~V->~Y)))");
        System.out.println(e.getExpression());
        System.out.println(e.getConvertedExpression());
        System.out.println(e.getPropositions());
        System.out.println(e.getPropositions(0,3));
        System.out.println(e.getPropositions(3, 5));
        System.out.println(e.getPropositions(1,1));
        String[] s = e.getStringTableRow(0);
        for (String i : s) {
            System.out.print(i + " ");
        }
        e.printTruthTable();

    }
}
