package ai.knowlej.Planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;

import ai.knowlej.DataStructures.ActionDomain;
import ai.knowlej.DataStructures.ActionSchema;
import ai.knowlej.Exceptions.*;
import ai.knowlej.PropositionalLogic.Logic.Proposition;
import ai.knowlej.PropositionalLogic.Models.Model;

public class ClassicalPlanner {
    private String[] initialState; // initial propositional arrangement/parameters of task/environment
    private String[] state; // current propositional arrangement/parameters of task/environment
    private ActionDomain actions;
    private HashMap<Instant, Proposition> sentenceTimestamps; // propositions bound by timestamp
    private String[] groundTruths; // axioms that are always true
    private Model[] fluents; // Propositions that change over time

    public ClassicalPlanner() {
        super();
        this.initialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public ClassicalPlanner(ActionSchema[] actions) {
        this.actions = new ActionDomain(actions);
        this.initialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public ClassicalPlanner(ActionDomain domain) {
        this.actions = domain;
        this.initialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public ClassicalPlanner(String[] groundTruths, ActionSchema[] actions) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.actions = new ActionDomain(actions);
        setGroundTruths(groundTruths);
        this.fluents = null;
    }

    public ClassicalPlanner(String[] groundTruths, ActionDomain domain) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.actions = domain;
        setGroundTruths(groundTruths);
        this.fluents = null;
    }

    public ClassicalPlanner(Model[] fluents, String[] groundTruths, ActionSchema[] actions) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.actions = new ActionDomain(actions);
        setGroundTruths(groundTruths);
        this.fluents = fluents;
    }

    public ClassicalPlanner(Model[] fluents, String[] groundTruths, ActionDomain domain) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.actions = domain;
        setGroundTruths(groundTruths);
        this.fluents = fluents;
    }

    private void setGroundTruths(String[] groundTruths) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        for (String statement : groundTruths) {
            try {
                Proposition propTest = new Proposition(statement);
            } catch (InvalidExpressionException e){
                throw new InvalidExpressionException("Invalid statement included in ground truths");
            }
        }
        this.groundTruths = groundTruths;
    }

    public ArrayList<ActionSchema> plan() {
        ArrayList<ActionSchema> plan = new ArrayList<ActionSchema>();


        return plan;
    }
}
