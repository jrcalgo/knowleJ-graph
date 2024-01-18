package src.LogicExpressions.PropositionalLogic.Logic;

import java.util.ArrayList;

/**
 * Used for building a truth table that includes only operands.
 */
public class TruthTableBuilder {
    private ArrayList<String> operands;
    private int operandCount;
    private int boolRowsCount;
    private int boolColsCount;
    private int boolCount;
    private Boolean[][] valueTable;
    private String[][] truthTable;

    public TruthTableBuilder(ArrayList<String> operands, int boolRowsCount, int boolColsCount) {
        this.operands = operands;
        this.boolRowsCount = boolRowsCount;
        this.boolColsCount = boolColsCount;
        this.operandCount = operands.size();
        this.boolCount = boolRowsCount * boolColsCount;
        buildTruthTable();
    }

    private void buildTruthTable() {
        valueTable = new Boolean[boolRowsCount][boolColsCount];
        truthTable = new String[boolRowsCount + 1][boolColsCount]; // +1 for index 0 column titles

        for (int i = 0; i < operands.size(); i++)
            truthTable[0][i] = operands.get(i) + ""; // titles each column with corresponding
                                                         // proposition/compound proposition
        combineOperandValues();
    }

    private void combineOperandValues() {
        String[] operandValues = new String[operandCount];
        for (int i = 0; i < operandCount; i++)
            operandValues[i] = "T";

        for (int i = 0; i < boolRowsCount; i++) {
            for (int j = 0; j < operandCount; j++) {
                if (operandValues[j].equals("T")) {
                    operandValues[j] = "F";
                    break;
                } else {
                    operandValues[j] = "T";
                }
            }
            for (int j = 0; j < operandCount; j++) {
                valueTable[i][j] = operandValues[j].equals("T") ? true : false;
                truthTable[i + 1][j] = operandValues[j];
            }
        }
    }

    public String[][] getTruthTable() {
        return this.truthTable;
    }

    public Boolean[][] getValueTable() {
        return this.valueTable;
    }

    public int getBoolCount() {
        return this.boolCount;
    }
}
