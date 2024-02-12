package targets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicType.PropositionalLogic.Logic.*;
import src.LogicType.PropositionalLogic.Models.DeterministicModel;
import src.LogicType.PropositionalLogic.Models.Model;

public class demo {

    public static void main(String[] args)
            throws IOException, InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        // Proposition e = new Proposition("(P&Q| ~V )->Z<>(Y&~R<>P>-<Q)");
        // Proposition e = new Proposition("~(P&~(~Q|(~V->~Y)))");
        // Proposition e = new Proposition("(Q&P)->~(P&Q)");
        // Proposition e = new
        // Proposition("~((P&Q)|(~(V|Y)&U))&((R<>W)&(Z>-<L))|(P&G)|(~B&(X&N))");
        // //Proposition e = new Proposition("P&Q&R");
        // Proposition e = new Proposition("P|~Q|(~P&~R)");
        // Proposition e = new Proposition("P->W&T");
        // System.out.println(e.getExpression());
        // System.out.println(e.getConvertedExpression());
        // System.out.println(e.getSentences());
        // e.printTruthTable();
        // String[] s = e.getStringTableRow(0);
        // for (String i : s) {
        // System.out.print(i + " ");
        // }
        // e.printTruthTable();

        Random r = new Random();
        int randomNum1 = r.nextInt(100);
        int randomNum2 = r.nextInt(100);
        int randomNum3 = r.nextInt(100);
        int randomNum4 = r.nextInt(100);
        Character b1 = exampleBoolean1(randomNum1);
        Character b2 = exampleBoolean2(randomNum2);
        // DeterministicModel cleaning = new DeterministicModel("Cleaning", "(P&Q) | (R&Y)",
        //         new HashMap<Character, Character>() {
        //             {
        //                 put('P', b1);
        //                 put('Q', b2);
        //                 put('R', exampleBoolean2(randomNum3));
        //                 put('Y', exampleBoolean1(randomNum4));
        //             }
        //         },
        //         new HashMap<Character, String>() {
        //             {
        //                 put('P', "I will clean my room");
        //                 put('Q', "I will clean the kitchen");
        //                 put('R', "I will clean the bathroom");
        //                 put('Y', "I will clean the living room");
        //             }
        //         });
        // System.out.println(cleaning.getExpression());
        // System.out.println(cleaning.getPredicateModel());
        // System.out.println(cleaning.getModelName());
        // System.out.println(cleaning.getSymbolicRepresentation());
        // System.out.println(cleaning.getOperands());

        DeterministicModel forwardOrBackward = new DeterministicModel("ForwardOrBackward",  new HashMap<Character, String>() {
                {
                    put('P', "Navigatable Ground Path");
                    put('Q', "Obstacle(s) in the way");
                    put('R', "Path fits robot dimensions");
                }
            },
            new HashMap<Character, Character>() {
                {
                    put('P', 'T');
                    put('Q', 'T');
                    put('R', 'T');
                }
            },"P&R&~Q");

    DeterministicModel turn = new DeterministicModel("Turn", new HashMap<Character, String>() {
                {
                    put('P', "Navigatable Ground Path");
                    put('Q', "Obstacle(s) in the way");
                    put('R', "Path fits robot dimensions");
                    put('Y', "Unsafe path");
                    put('X', "Unable to turn");
                }
            },
            new HashMap<Character, Character>() {
                {
                    put('P', 'T');
                    put('Q', 'T');
                    put('R', 'T');
                    put('Y', 'T');
                    put('X', 'T');
                }
            }, "(P&R)&(~Q&~Y&~X)" );

    DeterministicModel stop = new DeterministicModel("Stop",new HashMap<Character,String>() {
            {
                put('P',"Navigatable Ground Path");
                put('Q',"Obstacle(s) present");
                put('R',"Path fits robot dimensions");
                put('Y',"Unsafe path");
            }
        },
        new HashMap<Character,Character>() {
            {
                put('P','T');
                put('Q','T');
                put('R','T');
                put('Y','T');
            }
        },"(Q->Y)|(Y->Q)|(~R->Q)&(~R->Y)");

    Model[] carMovement = new Model[] {
            forwardOrBackward,
            turn,
            stop
    };

    DeterministicModel schoolOrWork = new DeterministicModel("schwork", new HashMap<Character, String>() {
        {
            put('P', "Programming");
            put('C', "Collaboration");
            put('W', "At work/school");
            put('D', "Driving");
            put('L', "Lunchbreak");
        }
    },
    new HashMap<Character, Character>() {
        {
            put('P', 'T');
            put('C', 'T');
            put('W', 'T');
            put('D', 'T');
            put('L', 'T');
        }
    }, "((P|C)&W)->(~D&~L)");

    DeterministicModel sleeping = new DeterministicModel("sleeping", new HashMap<Character, String>() {
        {
            put('S', "Sleeping");
            put('W', "At work/school");
            put('D', "Driving");
            put('N', "Doing nothing");
        }
    },
    new HashMap<Character, Character>() {
        {
            put('S', 'T');
            put('W', 'T');
            put('D', 'T');
            put('N', 'T');
        }
    }, "S->N&~(D|W)");

    Model[] determinedActivities = new Model[] {
            schoolOrWork,
            sleeping
    };

    DeterministicModel health = new DeterministicModel("health", new HashMap<Character, String>() {
        {
            put('P', "fitness");
            put('H', "healthy eating");
            put('S', "8 hours of sleep");
            put('M', "stress management");
        }
    },
    new HashMap<Character, Character>() {
        {
            put('P', 'T');
            put('H', 'T');
            put('S', 'T');
            put('M', 'T');
        }
    }, "P&H&S&M");

    DeterministicModel social = new DeterministicModel("social", new HashMap<Character, String>() {
        {
            put('I', "interpersonal relationships outside of family");
            put('O', "public interactions");
        }
    },
    new HashMap<Character, Character>() {
        {
            put('I', 'T');
            put('O', 'T');
        }
    }, "I|O");

    Model[] success = new Model[] {
        health,
        social,
    };

    Argument<Model> a = new Argument<>(success);
    a.printTruthTable();
    a.printAllTrueKBModels();
    String query = "P&H&S&M&I&O";
    System.out.println("Query " + query + " is " + a.checkAllTTModels(query));
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
