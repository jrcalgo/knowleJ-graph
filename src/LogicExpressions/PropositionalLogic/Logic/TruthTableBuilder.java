package src.LogicExpressions.PropositionalLogic.Logic;

import java.util.ArrayList;

/**
 * Used for building a basic truth table that includes only operands.
 */
public class TruthTableBuilder {
    private char[] operands;
    private int operandCount;
    private int boolRowsCount;
    private int boolColsCount;
    private int boolCount;
    private Boolean[][] valueTable;
    private String[][] truthTable;

    public TruthTableBuilder(ArrayList<String> operands, int boolRowsCount, int boolColsCount) {
        this.operands = String.join("", operands).toCharArray();
        this.boolRowsCount = boolRowsCount;
        this.boolColsCount = Math.max(boolColsCount, this.operands.length);
        this.operandCount = operands.size();
        this.boolCount = boolRowsCount * boolColsCount;
        buildTruthTable();
    }

    public TruthTableBuilder(char[] operands, int boolRowsCount, int boolColsCount) {
        this.operands = operands;
        this.boolRowsCount = boolRowsCount;
        this.boolColsCount = Math.max(boolColsCount, this.operands.length);
        this.operandCount = operands.length;
        this.boolCount = boolRowsCount * boolColsCount;
        buildTruthTable();
    }

    private void buildTruthTable() {
        valueTable = new Boolean[boolRowsCount][boolColsCount];
        truthTable = new String[boolRowsCount + 1][boolColsCount]; // +1 for index 0 column titles

        for (int i = 0; i < operandCount; i++)
            truthTable[0][i] = operands[i] + ""; // titles each column with corresponding
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

    public void close() {
        this.operands = null;
        this.operandCount = 0;
        this.boolRowsCount = 0;
        this.boolColsCount = 0;
        this.boolCount = 0;
        this.valueTable = null;
        this.truthTable = null;
    }
}
