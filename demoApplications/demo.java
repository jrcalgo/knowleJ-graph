package demoApplications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import lib.src.main.java.knowlej.PropositionalLogic.Logic.*;
import lib.src.main.java.knowlej.PropositionalLogic.Models.StochasticModel;
import lib.src.main.java.knowlej.PropositionalLogic.Models.DeterministicModel;
import lib.src.main.java.knowlej.PropositionalLogic.Models.Model;

public class demo {
    public static void main(String[] args) throws Exception {
        StochasticModel smTest = new StochasticModel("Test1", "A&B|C");
        System.out.println(smTest.getModelName() + ": " + smTest.getExpression().toString());

        StochasticModel smTest2 = new StochasticModel("Test2", "A&B|C", new HashMap<Character, String>() {{
            put('A', "Cat");
            put('B', "Dog");
            put('C', "Bird");
        }});
        // System.out.println(smTest2.getModelName() + ": " + smTest2.getExpression().toString());
        // System.out.println(smTest2.getSymbolicRepresentation());

        StochasticModel smTest3 = new StochasticModel("Test3", "A&B|C", new HashMap<Character, String>() {{
            put('A', "Cat");
            put('B', "Dog");
            put('C', "Bird");
        }}, 0.55, new HashMap<Character, Double>() {{
            put('A', 0.5);
            put('B', 0.4);
            put('C', 0.75);
        }});
        // System.out.println(smTest3.getModelName() + ": " + smTest3.getExpression().toString());
        // System.out.println(smTest3.getSymbolicRepresentation());
        // System.out.println(smTest3.getPredicateProbabilityModel());
        // System.out.println(smTest3.getPredicateBooleanModel());
        // System.out.println(smTest3.getValidityEvaluation());
        // System.out.println(smTest3.getPredicateEvaluation());

        StochasticModel smTest4 = new StochasticModel("Test4", "A&B|C", new HashMap<Character, String>() {{
            put('A', "Cat");
            put('B', "Dog");
            put('C', "Bird");
        }}, null, new HashMap<Character, Double>() {{
            put('A', 0.5);
            put('B', 0.4);
            put('C', 0.75);
        }}, new HashMap<Character, Double>() {{
            put('A', .5);
            put('B', .3);
            put('C', .74);
        }});
        // System.out.println(smTest4.getModelName() + ": " + smTest4.getExpression().toString());
        // System.out.println(smTest3.getSymbolicRepresentation());
        // System.out.println(smTest3.getPredicateProbabilityModel());
        // System.out.println(smTest3.getPredicateBooleanModel());
        // System.out.println(smTest3.getValidityEvaluation());
        // System.out.println(smTest3.getPredicateEvaluation());

        StochasticModel smTest5 = new StochasticModel("Test5", "A&B|C->D", new HashMap<Character, String>() {{
            put('A', "Cat");
            put('B', "Dog");
            put('C', "Bird");
            put('D', "Frog");
        }}, 0.45, new HashMap<Character, Double>() {{
            put('A', 0.5);
            put('B', 0.4);
            put('C', 0.75);
            put('D', 0.5);
        }}, new HashMap<Character, Double>() {{
            put('A', .5);
            put('B', .3);
            put('C', .74);
        }});
        // System.out.println(smTest5.getModelName() + ": " + smTest5.getExpression().toString());
        // System.out.println(smTest5.getOperands());
        // System.out.println(smTest5.getSymbolicRepresentation());
        // System.out.println(smTest5.getPredicateProbabilityModel());
        // System.out.println(smTest5.getPredicateBooleanModel());
        // System.out.println(smTest5.getValidityEvaluation());
        // System.out.println(smTest5.getPredicateEvaluation());

        StochasticModel[] stochasticModels = new StochasticModel[5];
        stochasticModels[0] = smTest;
        stochasticModels[1] = smTest2;
        stochasticModels[2] = smTest3;
        stochasticModels[3] = smTest4;
        stochasticModels[4] = smTest5;
        
        Argument<StochasticModel> argument1 = new Argument<>(stochasticModels);
        System.out.println(argument1.deduce("A&~B")); 
    }

}
