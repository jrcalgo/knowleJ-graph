==========================================================================
THINGS MUST BE IMPLEMENTED SOMEWHERE FOR PROPOSITIONS:
==========================================================================
* Conditional statement conversions: Converse, Contrapositive, Inverse
* Equivalences: Tautology, Contradiction, Neutral
* Logic Operators: implication (>>), if and only if (<>), not (~), exclusive-or OR xor (><), reduction (<<)
* Laws of Propositional Logic: Idempotent, Associtative, Communitative, Distributive, Identity, Domination, Double negation, Complement, De Morgan's, Absorption, Conditional Identities

==========================================================================
THINGS MUST BE IMPLEMENTED SOMEWHERE FOR ARGUMENTS:
==========================================================================
* Predicates: "Basically 
* Predicate Quantifiers: Existential quantifier, Universal quantifier, Nested quantifiers
* Argument: "Using predicates and/or quantifiers, or propositions to form an argument"
* Argument Operator: therefore (!!!)
* Rules of Inference: Modus Ponens, Modus Tollens, Addition, Simplification, Conjunction, Hypothetical Syllogism, Disjunctive Syllogism, Resolution
* Rules of Inference for Quantifiers: Universal Instantiation, Universal Generalization, Existential Instantiation, Existential Generalization
=====================================


==========================================================================
THINGS MUST BE IMPLEMENTED SOMEWHERE FOR BOOLEAN ALGEBRA:
==========================================================================
* Conversions: "Must convert boolean algebra syntax into propositional logic syntax, evaluate expression, and return value into terms of Boolean Algebra
* Functions: 

==========================================================================
1. In-take expression in LogicalPropositions Object
2. Store converted expression that contains simpler syntax
3. Check converted expression for syntax errors
4. If valid, parse all expression proposition partitions into a tree
5. Store 1 instance of each operand in an array, and append tree partitions at end in-order; store these values in an aggregate ArrayList
    5. Be able to print truth table for propositions and their operands
    5. Be able to evaluate and apply applicable propositional equivalency laws to expression/partitions
6. Use LogicalPredicate Object to set operand values through boolean values or boolean variables or boolean function calls
    6. Be able to print truth table based off boolean arguments and possible uninitialized operands
    6. Be able to evaluate and apply quantifiers to predicate
7. 


?. Be able to define and initialize an either an argument (set(s) of predicates or propositions/set(s) of hypotheses) for the purpose of using containerized variables and/or method calls and their corresponding outputted boolean values as proof of some true, end-result argument statement; chaining executed branches inside a single object to produce some end-result, such as variable value(s) or additional function call(s); encapsulating boolean branches of executed code and/or variables into an object to then output a boolean variable value or to execute additional code that returns boolean; cramming program and subprogram branches/logic into a reuseable object variable.

The construction of these arguments must follow propositional and predicate-quantifier inference laws where each element must form a TAUTOLOGY, including the conclusion. This means that, in context of this program, all hyptheses and conclusion(s) must output a True boolean value. Hypotheses statements with no direct fit with inference laws will need to be proven or simplified into appropriate hypotheses using propositional and predicate laws: If the simplification is False and all other possible equivalency proofs are False as well, end argument and throw exception; If the conversion is True, execute any code in said statement, continue to next hypothesis/final conclusion, check its validity in the argument, and execute given code.

==========================================================================
Things to do for Propositions.java:
* Fill operand truth table with all possible T/F value combinations
* Encode each partition's operands with its corresponding T/F value
* Evaluate each encoded partition in the truth table by using evaluator object/methods and operand T/F values
* Store each partition's evaluated value in corresponding truth table slot
* Create methods for inverse, converse, and contrapositive conversions of conditional statement partitions