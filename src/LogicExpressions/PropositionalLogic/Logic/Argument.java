package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Laws.InferenceLaws;

public class Argument extends InferenceLaws {
    private Model[] models;
    private Model query;
    private char[] modelCharEvaluations;
    private boolean[] modelBooleanEvaluations;
    private char argumentCharEvaluation;
    private boolean argumentBooleanEvaluation;
    private String[] truthTable;
    private Boolean[] tableValues;

    public Argument(Model[] models, Model query) {
        this.models = models;
        this.query = query;
        setTruthTable();
    }

    public Argument(Model[] models, String query) {
        this.models = models;
        this.query = new Model("Query", query);
        setTruthTable();
    }
    
    public void checkModels() {
        
    }

    public void setTruthTable() {
        this.truthTable = new String[this.models.length + 1];
        for (int i = 0; i < this.models.length; i++) {
            this.truthTable[i] = this.models[i].getExpression() + " : " + this.models[i].getPredicateEvaluation();
        }
        this.truthTable[this.truthTable.length] = query.getExpression() + " : " + evaluateArgument();
    }

    public boolean evaluateArgument() {
        this.modelCharEvaluations = new char[this.models.length];
        this.modelBooleanEvaluations = new boolean[this.models.length];
        for (int i = 0; i < this.models.length; i++) {
            this.modelCharEvaluations[i] = this.models[i].getPredicateEvaluation() ? 'T' : 'F';
            this.modelBooleanEvaluations[i] = this.models[i].getPredicateEvaluation();
        }
        
        Boolean modelEvaluation = this.modelBooleanEvaluations[0];
        for (int i = 1; i < this.models.length; i++)
                modelEvaluation = modelEvaluation && this.modelBooleanEvaluations[i];

        if (modelEvaluation == true && this.query.get)
    }

    public String[] getTruthTable() {
        return this.truthTable;
    }

    public Boolean[] getTableValues() {
        return this.tableValues;
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
