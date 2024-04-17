package knowleJ.Planning;

import java.util.ArrayList;

import knowleJ.Exceptions.*;
import knowleJ.PropositionalLogic.Logic.Argument;
import knowleJ.PropositionalLogic.Models.Model;

public class Planner extends ActionDomain {
    Argument<Model> knowledgeBase;

    public Planner() {
        super();
        this.knowledgeBase = null;
    }

    public Planner(Argument<Model> knowledgeBase, ActionSchema[] actions) {
        super(actions);
        this.knowledgeBase = knowledgeBase;
    }

    public Planner(Argument<Model> knowledgeBase, ActionDomain domain) {
        super(domain.getActions());
        this.knowledgeBase = knowledgeBase;
    }

    public ArrayList<ActionSchema> plan() {
        ArrayList<ActionSchema> plan = new ArrayList<ActionSchema>();


        return plan;
    }
}
