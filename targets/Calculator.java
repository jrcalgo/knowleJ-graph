package targets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
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
        // Proposition e = new Proposition("P|~Q|(~P&~R)");
        // System.out.println(e.getExpression());
        // System.out.println(e.getConvertedExpression());
        // System.out.println(e.getPropositions());
        // e.printTruthTable();
        // String[] s = e.getStringTableRow(0);
        // for (String i : s) {
        //     System.out.print(i + " ");
        // }
        // e.printTruthTable();

        Random r = new Random();
        int randomNum1 = r.nextInt(100);
        int randomNum2 = r.nextInt(100);
        int randomNum3 = r.nextInt(100);
        int randomNum4 = r.nextInt(100);
        Character b1 = exampleBoolean1(randomNum1);
        Character b2 = exampleBoolean2(randomNum2);
        Model cleaning = new Model("Cleaning", "(P&Q) | (R&Y)", new HashMap<Character, Character>() {{put('P', b1); put ('Q', b2); put('R', exampleBoolean2(randomNum3)); put('Y', exampleBoolean1(randomNum4));}},
                         new HashMap<Character, String>() {{put('P', "I will clean my room"); put('Q', "I will clean the kitchen"); put('R', "I will clean the bathroom"); put('Y', "I will clean the living room");}});
        System.out.println(cleaning.getExpression());
        System.out.println(cleaning.getPredicateModel());
        System.out.println(cleaning.getModelName());
        System.out.println(cleaning.getSymbolicModel());
        System.out.println(cleaning.getOperands());

    }

    public static Character exampleBoolean1(int i) {
        if (i % 3 == 0) {
            return 'T';
        } else {
            return 'F';
        }
    }

    public static Character exampleBoolean2(int i) {
        if (i >= 0 && i <= 50) {
            return 'T';
        } else {
            return 'F';
        }
    }
}
