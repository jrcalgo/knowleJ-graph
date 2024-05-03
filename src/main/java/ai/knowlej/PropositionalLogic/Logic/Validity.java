package knowlej.PropositionalLogic.Logic;

public class Validity {

    public boolean isTautology(boolean[] rowOrColumn) {
        for (boolean b : rowOrColumn) {
            if (b == false)
                return false;
        }
        return true;
    }

    public boolean isContradiction(boolean[] rowOrColumn) {
        for (boolean b : rowOrColumn) {
            if (b == true)
                return false;
        }
        return true;
    }

    public boolean isContingency(boolean[] rowOrColumn) {
        return !(isTautology(rowOrColumn) || isContradiction(rowOrColumn));
    }
}
