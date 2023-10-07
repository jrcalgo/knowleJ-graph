package src.Interfaces;

public interface Equivalencies {
    public boolean isTautology(String[] s);
    public boolean isTautology(Boolean[] b);
    public boolean isContradiction(String[] s);
    public boolean isContradiction(Boolean[] b);
    public boolean isContingency(String[] s);
    public boolean isContingency(Boolean[] b);
}
