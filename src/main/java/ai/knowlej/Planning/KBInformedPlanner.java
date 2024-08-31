package ai.knowlej.Planning;

import ai.knowlej.DataStructures.ActionDomain;
import ai.knowlej.DataStructures.StateDomain;

import java.util.ArrayList;

public class KBInformedPlanner {
    private ActionDomain actions;
    private StateDomain currentState;
    private StateDomain goalState;

    private ArrayList<String[]> actionHistory;
    private ArrayList<String[]> stateHistory;

    public KBInformedPlanner(StateDomain goalState) {
        // actions and currentState are pulled from neo4j database KBs
        this.goalState = goalState;
    }

    public void setGoalState(StateDomain goalState) {
        this.goalState = goalState;
    }

    public StateDomain getGoalStateDomain() {
        return this.goalState;
    }

    public StateDomain getCurrentStateDomain() {
        return this.currentState;
    }

    public ActionDomain getActionDomain() {
        return this.actions;
    }
}
