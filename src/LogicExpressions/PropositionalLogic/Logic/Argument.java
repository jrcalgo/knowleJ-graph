package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Laws.InferenceLaws;

public class Argument extends InferenceLaws {
    private Model[] models;
    private char[] charEvaluations;
    private boolean[] booleanEvaluations;
    private String[][] truthTable;
    private Boolean[][] valueTable;

    public Argument(Model[] models) {
        this.models = models;
        setTruthTable();
    }
    
    public void checkModels() {
        
    }

    public boolean evaluateArgument() {

    }

    public setTruthTable() {

    }

    public getTruthTable() {

    }

    public getValueTable() {

    }

    public Model[] getModels() {
        return this.models;
    }

    public String[] getArgumentExpressions() {
        String[] expressions = new String[this.models.length];
        for (int i = 0; i < this.models.length; i++) {
            expressions[i] = this.models[i].getExpression();
        }
        return expressions;
    }
}
