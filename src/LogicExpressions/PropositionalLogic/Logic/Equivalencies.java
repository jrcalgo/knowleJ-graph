package src.LogicExpressions.PropositionalLogic.Logic;

public class Equivalencies {
    public boolean isTautology(char[] rowOrColumn) {
        for (String s : rowOrColumn) {
            if (s.equals("F"))
                return false;
        }
        return true;
    }

    public boolean isTautology(Boolean[] rowOrColumn) {
        for (Boolean b : rowOrColumn) {
            if (b == false)
                return false;
        }
        return true;
    }

    public boolean isContradiction(char[] rowOrColumn) {
        for (String s : rowOrColumn) {
            if (s.equals("T"))
                return false;
        }
        return true;
    }

    public boolean isContradiction(Boolean[] rowOrColumn) {
        for (Boolean b : rowOrColumn) {
            if (b == true)
                return false;
        }
        return true;
    }

    public boolean isContingency(char[] rowOrColumn) {
        return !(isTautology(rowOrColumn) || isContradiction(rowOrColumn));
    }

    public boolean isContingency(Boolean[] rowOrColumn) {
        return !(isTautology(rowOrColumn) || isContradiction(rowOrColumn));
    }
}
