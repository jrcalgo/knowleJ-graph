package targets;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.Proposition;

public class Calculator {
    
    public static void main(String[] args) throws IOException, InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        //Proposition e = new Proposition("(P&Q| ~V )->Z<>(Y&~R<>P>-<Q)");
        //Proposition e = new Proposition("~(P&~(~Q|(~V->~Y)))");
        //Proposition e = new Proposition("(Q&P)->(P&Q)");
        Proposition e = new Proposition("~((P&Q)|(~(V|Y)&U))&((R<>W)&(Z>-<L))|(P&G)|(~B&(X&N))");
        System.out.println(e.getExpression());
        System.out.println(e.getConvertedExpression());
        System.out.println(e.getPropositions());
        e.printTruthTable();
        String[] s = e.getStringTableRow(0);
        for (String i : s) {
            System.out.print(i + " ");
        }
        //e.printTruthTable();

    }
}
