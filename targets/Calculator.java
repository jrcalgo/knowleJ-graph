package targets;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.*;

public class Calculator {

    public static void main(String[] args) throws IOException, InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        //Proposition e = new Proposition("(P&Q| ~V )->Z<>(Y&~R<>P>-<Q)");
        //Proposition e = new Proposition("~(P&~(~Q|(~V->~Y)))");
        //Proposition e = new Proposition("(Q&P)->~(P&Q)");
        // //Proposition e = new Proposition("~((P&Q)|(~(V|Y)&U))&((R<>W)&(Z>-<L))|(P&G)|(~B&(X&N))");
        // //Proposition e = new Proposition("P&Q&R");
        // Proposition e = new Proposition("P|Q|(P&~R)");
        // System.out.println(e.getExpression());
        // System.out.println(e.getConvertedExpression());
        // System.out.println(e.getPropositions());
        // e.printTruthTable();
        // String[] s = e.getStringTableRow(0);
        // for (String i : s) {
        //     System.out.print(i + " ");
        // }
        //e.printTruthTable();

        //Model model = new Model("Model", "(P&Q) | (R&Y)", new HashMap<Character, Character>() {{put('P', 'T'); put('R', 'F'); put('Y', 'T');}});
        Model cleaning = new Model("Cleaning", "(P&Q) | (R&Y)", new HashMap<Character, Character>() {{put('P', 'T'); put ('Q', 'T'); put('R', 'F'); put('Y', 'T');}},
                         new HashMap<Character, String>() {{put('P', "I will clean my room"); put('Q', "I will clean the kitchen"); put('R', "I will clean the bathroom"); put('Y', "I will clean the living room");}});
        System.out.println(cleaning.getExpression());
        System.out.println(cleaning.getPredicateModel());
        System.out.println(cleaning.getSymbolicModel());
        System.out.println(cleaning.expression.getExpression());

    }
}
