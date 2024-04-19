package knowleJ.Planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;

import knowleJ.Exceptions.*;
import knowleJ.PropositionalLogic.Logic.Proposition;
import knowleJ.PropositionalLogic.Logic.Argument;
import knowleJ.PropositionalLogic.Models.Model;

public class Planner extends ActionDomain {
    String[] initialState; // initial propositional arrangement/parameters of task/environment
    String[] state; // current propositional arrangement/parameters of task/environment
    HashMap<Instant, Proposition> sentenceTimestamps; // propositions bound by timestamp
    String[] groundTruths; // axioms that are always true
    Model[] fluents; // Propositions that change over time

    public Planner() {
        super();
        this.intialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public Planner(ActionSchema[] actions) {
        super(actions);
        this.initialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public Planner(ActionDomain domain) {
        super(domain.getActions());
        this.initialState = null;
        this.state = null;
        this.groundTruths = null;
        this.fluents = null;
    }

    public Planner(String[] groundTruths, ActionSchema[] actions) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super(actions);
        setGroundTruths(groundTruths);
        this.fluents = null;
    }

    public Planner(String[] groundTruths, ActionDomain domain) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super(domain.getActions());
        setGroundTruths(groundTruths);
        this.fluents = null;
    }

    public Planner(Model[] fluents, String[] groundTruths, ActionSchema[] actions) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super(actions);
        setGroundTruths(groundTruths);
        this.fluents = fluents;
    }

    public Planner(Model[] fluents, String[] groundTruths, ActionDomain domain) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super(domain.getActions());
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
