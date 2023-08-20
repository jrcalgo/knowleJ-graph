package src.LogicExpressions.PropositionalLogic.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.io.IOException;

public class LogicalCharacters {

    /**
     * Default operand list for propositional logic
     */
    private final static ArrayList<Character> DEFAULT_OPERAND_LIST = new ArrayList<Character>() {
        {
            add('A');
            add('B');
            add('C');
            add('D');
            add('E');
            add('F');
            add('G');
            add('H');
            add('I');
            add('J');
            add('K');
            add('L');
            add('M');
            add('N');
            add('O');
            add('P');
            add('Q');
            add('R');
            add('S');
            add('T');
            add('U');
            add('V');
            add('W');
            add('X');
            add('Y');
            add('Z');
        }
    };

    private static ArrayList<Character> CUSTOM_OPERAND_LIST = new ArrayList<Character>();

    /**
     * custom/default loaded operand list for propositional logic
     */
    private static ArrayList<Character> OPERAND_LIST = new ArrayList<Character>() {
        {
            addAll(DEFAULT_OPERAND_LIST);
        }   
    };

    /** Operator map index reference integers */
    private static final int OPERATOR_CONVERSION_INDEX = 0;
    private final int OPERATOR_NAME_INDEX = 1;

    /**
     * Operator map for all hand-typed operators and their corresponding conversion
     * values and names
     */
    private final static Map<String, ArrayList<String>> OPERATOR_MAPS = new HashMap<String, ArrayList<String>>() {
        {
            put("&", new ArrayList<String>() {
                {
                    add("&");
                    add("and");
                }
            });
            put("|", new ArrayList<String>() {
                {
                    add("|");
                    add("or");
                }
            });
            put("->", new ArrayList<String>() {
                {
                    add(">");
                    add("implies");
                }
            });
            put("<>", new ArrayList<String>() {
                {
                    add("i");
                    add("iff");
                }
            });
            put("~", new ArrayList<String>() {
                {
                    add("~");
                    add("not");
                }
            });
            put(">-<", new ArrayList<String>() {
                {
                    add("x");
                    add("xor");
                }
            });
            put("<-", new ArrayList<String>() {
                {
                    add("<");
                    add("reduces");
                }
            });
            put("(", new ArrayList<String>() {
                {
                    add("(");
                    add("left-parenthesis");
                }
            });
            put(")", new ArrayList<String>() {
                {
                    add(")");
                    add("right-parenthesis");
                }
            });
            put("\s", new ArrayList<String>() {
                {
                    add("");
                    add("space");
                }
            });
            put("T", new ArrayList<String>() {
                {
                    add("T");
                    add("true");
                }
            });
            put("F", new ArrayList<String>() {
                {
                    add("F");
                    add("false");
                }
            });
        }
    };

    private final static ArrayList<String> INVALID_OPERATOR_ORDER = new ArrayList<String>() {
        {
        add("~&");
        add("~|");
        add("~>");
        add("~<");
        add("~x");
        add("~)");
        add("~i");

        add("&&");
        add("&&&");
        add("&|");
        add("&>");
        add("&<");
        add("&x");
        add("&)");
        add("&i");

        add("||");
        add("|||");
        add("|&");
        add("|>");
        add("|<");
        add("|x");
        add("|)");
        add("|i");

        add(">>");
        add(">>>");
        add(">|");
        add(">&");
        add(">x");
        add(">)");
        add(">i");

        add("xx");
        add("xxx");
        add("x&");
        add("x|");
        add("x>");
        add("x<");
        add("x)");
        add("xi");

        add("<<");
        add("<<<");
        add("<|");
        add("<&");
        add("<x");
        add("<)");
        add("<i");
        add("<>");

        add("ii");
        add("iii");
        add("i&");
        add("i|");
        add("i>");
        add("i<");
        add("i)");
        add("ix");

        add("()");
        add("(|");
        add("(&");
        add("(>");
        add("(<");
        add("(x");
        add("(i");
        }
    };

    /**
     * default constructor calls super()/Object constructor
     */
    public LogicalCharacters() {
        super();
    }

    // ~~~~~~~~OPERAND METHODS~~~~~~~~
    public void customOperands(ArrayList<Character> ops) throws IOException {
        final int MAX_OPERANDS = 30;
        if (ops.size() > MAX_OPERANDS) {
            throw new IOException("Custom operands cannot exceed " + MAX_OPERANDS + " characters");
        }
        for (char c : ops) {
            if (OPERATOR_MAPS.containsKey(Character.toString(c))) {
                throw new IOException("Custom operands cannot contain operators");
            }
        }
        CUSTOM_OPERAND_LIST.clear();
        CUSTOM_OPERAND_LIST.addAll(ops);
        OPERAND_LIST.addAll(CUSTOM_OPERAND_LIST);
    }

    public void resetOperands() {
        CUSTOM_OPERAND_LIST.clear();
        OPERAND_LIST.clear();
        OPERAND_LIST.addAll(DEFAULT_OPERAND_LIST);
    }

    /**
     * 
     * @param c
     * @return
     */
    public boolean isOperand(char c) {
        return OPERAND_LIST.contains(c);
    }

    /**
     * 
     * @param s
     * @return
     */
    public boolean isOperand(String s) {
        return OPERAND_LIST.contains(s.charAt(0));
    }

    /**
     * 
     * @param s
     * @param c
     * @return
     */
    public boolean containsOperand(String s, char c) {
        return s.contains(Character.toString(c));
    }

    /**
     * 
     */
    public boolean containsOperand(String s, String operand) {
        return s.contains(operand);
    }

    /**
     * 
     * @param s
     * @return
     */
    public boolean containsAnyOperands(String s) {
        for (char c : OPERAND_LIST) {
            if (s.contains(Character.toString(c))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public String getValidOperands() {
        return OPERAND_LIST.toString();
    }

    // ~~~~~~~~OPERATOR METHODS~~~~~~~~
    /**
     * Used to check if a specific, partitioned string/sequence of characters IS an
     * operator/key
     * 
     * @param s
     * @return
     */
    public boolean isOperator(String s) {
        return OPERATOR_MAPS.containsKey(s);
    }

    /**
     * Used to check if a specific, partitioned string/sequence of characters HAS a
     * given operator/key
     * 
     * @param s
     * @param key
     * @return
     */
    public boolean containsOperator(String s, String key) {
        return s.contains((CharSequence) OPERATOR_MAPS.get(key));
    }

    /**
     * Used to check if a specific, partitioned string/sequence of characters HAS
     * ANY operators/keys
     * 
     * @param s
     * @return
     */
    public boolean containsAnyOperators(String s) {
        for (String key : OPERATOR_MAPS.keySet()) {
            if (s.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to get the operator/key from a given value
     * 
     * @param value
     * @return
     */
    public String getOperatorKeyFromValue(String value) {
        for (String key : OPERATOR_MAPS.keySet()) {
            if (OPERATOR_MAPS.get(key).contains(value)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Used to get the converted value from a given operator/key
     * 
     * @param operator
     * @return
     */
    public static String getConversionValueFromOperatorKey(String operator) {
        return OPERATOR_MAPS.get(operator).get(OPERATOR_CONVERSION_INDEX);
    }

    /**
     * Used to get the converted value from a given name value
     * 
     * @param name
     * @return
     */
    public String getConversionValueFromNameValue(String name) {
        for (String key : OPERATOR_MAPS.keySet()) {
            if (OPERATOR_MAPS.get(key).contains(name)) {
                return OPERATOR_MAPS.get(key).get(OPERATOR_CONVERSION_INDEX);
            }
        }
        return null;
    }

    /**
     * Used to get the name of the operator/key from a given operator/key
     * 
     * @param operator
     * @return
     */
    public String getNameValueFromOperatorKey(String operator) {
        return OPERATOR_MAPS.get(operator).get(OPERATOR_NAME_INDEX);
    }

    /**
     * Used to get the name valueof the operator/key from a given conversion value
     * 
     * @param conversion
     * @return
     */
    public String getNameValueFromConversionValue(String conversion) {
        for (String key : OPERATOR_MAPS.keySet()) {
            if (OPERATOR_MAPS.get(key).contains(conversion)) {
                return OPERATOR_MAPS.get(key).get(OPERATOR_NAME_INDEX);
            }
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public String getStringOperatorKeys() {
        return OPERATOR_MAPS.keySet().toString();
    }

    /**
     * 
     * @return
     */
    public String getStringOperatorConversion() {
        ArrayList<String> conversions = new ArrayList<String>();
        for (String key : OPERATOR_MAPS.keySet()) {
            conversions.add(OPERATOR_MAPS.get(key).get(OPERATOR_CONVERSION_INDEX));
        }
        return conversions.toString();
    }

    /**
     * 
     * @return
     */
    public String getStringOperatorName() {
        ArrayList<String> names = new ArrayList<String>();
        for (String key : OPERATOR_MAPS.keySet()) {
            names.add(OPERATOR_MAPS.get(key).get(OPERATOR_NAME_INDEX));
        }
        return names.toString();
    }

    public ArrayList<Character> getValidOperators() {
        return OPERAND_LIST;
    }

    // ~~~~~~~~LOGICAL METHODS~~~~~~~~
    
    public int getInvalidOrderSize() {
        return INVALID_OPERATOR_ORDER.size();
    }

    public String getInvalidOrderSet(int i) {
        return INVALID_OPERATOR_ORDER.get(i);
    }

}
