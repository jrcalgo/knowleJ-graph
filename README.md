# KnowleJ: Advanced Propositional Logic Engine

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8+-purple.svg)](https://kotlinlang.org/)
[![Python](https://img.shields.io/badge/Python-3.8+-blue.svg)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**KnowleJ** is a sophisticated, multi-language propositional logic SAT engine that combines symbolic reasoning with modern software engineering practices. Built with Java, Kotlin, and Python, it provides a comprehensive toolkit for automated theorem proving, logical inference, and knowledge representation.

## **What Makes KnowleJ Special**

- **Research-Grade Logic Engine**: Implements complete propositional logic with inference and equivalency laws
- **Graph-Based Reasoning**: Advanced deduction graphs for proof search and chaining
- **ML-Ready Architecture**: Designed for hybrid symbolic-ML approaches (in development)
- **Neo4j Integration**: Scalable graph database support for knowledge persistence (in development)
- **gRPC API**: Modern service-oriented architecture for distributed systems (in development)

## **Architecture Overview**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Java Core     │    │   Kotlin API    │    │  Python ML      │
│                 │    │                 │    │                 │
│ • Proposition   │◄──►│ • gRPC Server   │◄──►│ • Learning      │
│ • Argument      │    │ • Neo4j DB      │    │ • Automation    │
│ • Truth Tables  │    │ • Node Models   │    │ • Utilities     │
│ • Deduction     │    │                 │    │                 │
│   Graphs        │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## **Current Status**

### **Fully Functional**
- **Java Logic Engine**: Complete propositional logic implementation
- **Truth Table Generation**: Comprehensive boolean logic evaluation
- **Inference Laws**: Modus ponens, syllogisms, resolution, etc.
- **Equivalency Laws**: DeMorgan's, distributive, associative, etc.
- **Deduction Graphs**: A* search, bidirectional reasoning
- **Model System**: Deterministic and stochastic logic models
- **Exception Handling**: Robust error management

### **In Development**
- **ML Pipeline**: Python learning algorithms (proof link prediction, graph masking)
- **Neo4j Integration**: Database operations (infrastructure ready)
- **gRPC Services**: API layer (proto definitions complete)

## **Quick Start**

### Prerequisites
- Java 17+
- Maven 3.6+
- Python 3.8+ (for ML components)
- Neo4j (optional, for database features)

### Installation
```bash
git clone https://github.com/yourusername/knowlej.git
cd knowlej/knowlej-core
mvn clean install
```

## **Core Usage Examples**

### 1. **Working with Propositions**

```java
import ai.knowlej.PropositionalLogic.Logic.Proposition;
import ai.knowlej.Exceptions.*;

// Create a proposition from a logical expression
Proposition p = new Proposition("(p ∧ q) → r");

// Get the expression
System.out.println(p.getExpression()); // "(p ∧ q) → r"

// Get operands
System.out.println(p.getOperandCount()); // 3 (p, q, r)

// Generate and print truth table
p.printTruthTable();
```

**Output:**
```
p  q  r  (p ∧ q) → r
T  T  T     T
T  T  F     F
T  F  T     T
T  F  F     T
F  T  T     T
F  T  F     T
F  F  T     T
F  F  F     T
```

### 2. **Building Arguments with Knowledge Bases**

```java
import ai.knowlej.PropositionalLogic.Logic.Argument;
import ai.knowlej.PropositionalLogic.Models.LogicModels.DeterministicModel;

// Create knowledge base models
DeterministicModel[] kb = {
    new DeterministicModel("premise1", "p → q"),
    new DeterministicModel("premise2", "q → r")
};

// Create argument with learning enabled
Argument<DeterministicModel> argument = new Argument<>(kb, true);

// Check if a query follows from the knowledge base
String query = "p → r";
String result = argument.checkAllTTModels(query);
System.out.println("Query '" + query + "' is: " + result);
```

### 3. **Advanced Deduction with Graphs**

```java
import ai.knowlej.DataStructures.Graph.DirectedDeductionGraph;
import ai.knowlej.PropositionalLogic.Logic.Proposition;

// Create knowledge base
HashSet<String> knowledgeBase = new HashSet<>();
knowledgeBase.add("p → q");
knowledgeBase.add("q → r");

// Create query
Proposition query = new Proposition("p → r");

// Build deduction graph
DirectedDeductionGraph graph = new DirectedDeductionGraph(knowledgeBase, query);

// Perform bidirectional A* search
Set<String> forwardHistory = new HashSet<>();
Set<String> backwardHistory = new HashSet<>();
ArrayList<DeductionGraphNode> proof = graph.multithreadedBidirectionalAStar(forwardHistory, backwardHistory);

// Print proof path
for (DeductionGraphNode node : proof) {
    System.out.println("→ " + node.getExpression());
}
```

### 4. **Working with Stochastic Logic Models**

```java
import ai.knowlej.PropositionalLogic.Models.LogicModels.StochasticModel;
import ai.knowlej.PropositionalLogic.Models.LogicModels.ModelAbstract;
import ai.knowlej.PropositionalLogic.Logic.Argument;
import java.util.HashMap;

// Create several stochastic models with symbolic and probabilistic assignments
StochasticModel sm1 = new StochasticModel("Model1", "A & B | C");
StochasticModel sm2 = new StochasticModel("Model2", "A & B | C", new HashMap<Character, String>() {{
    put('A', "Cat");
    put('B', "Dog");
    put('C', "Bird");
}});
StochasticModel sm3 = new StochasticModel("Model3", "A & B | C", new HashMap<Character, String>() {{
    put('A', "Cat");
    put('B', "Dog");
    put('C', "Bird");
}}, 0.55, new HashMap<Character, Double>() {{
    put('A', 0.5);
    put('B', 0.4);
    put('C', 0.75);
}});
// ... more models as needed

// Assemble models into an array
StochasticModel[] models = new StochasticModel[] { sm1, sm2, sm3 /*, ... */ };

// Use with the Argument class for advanced deduction
Argument<ModelAbstract> argument = new Argument<>(models);
argument.deduce("A & C | B -> D & A");
```

This demonstrates the flexibility of the stochastic model system, supporting both symbolic and probabilistic assignments, and shows how to use them in advanced logical deduction scenarios.

## **Supported Logical Operations**

### **Logical Operators**
- `∧` (AND), `∨` (OR), `¬` (NOT)
- `→` (IMPLIES), `↔` (IFF), `⊕` (XOR)

### **Inference Laws**
- **Modus Ponens**: `p → q, p ⊢ q`
- **Modus Tollens**: `p → q, ¬q ⊢ ¬p`
- **Hypothetical Syllogism**: `p → q, q → r ⊢ p → r`
- **Disjunctive Syllogism**: `p ∨ q, ¬p ⊢ q`
- **Addition**: `p ⊢ p ∨ q`
- **Simplification**: `p ∧ q ⊢ p`
- **Conjunction**: `p, q ⊢ p ∧ q`
- **Resolution**: `p ∨ q, ¬p ∨ r ⊢ q ∨ r`

### **Equivalency Laws**
- **DeMorgan's**: `¬(p ∧ q) ↔ ¬p ∨ ¬q`
- **Distributive**: `p ∧ (q ∨ r) ↔ (p ∧ q) ∨ (p ∧ r)`
- **Associative**: `(p ∧ q) ∧ r ↔ p ∧ (q ∧ r)`
- **Commutative**: `p ∧ q ↔ q ∧ p`
- **Double Negation**: `¬¬p ↔ p`
- **Identity**: `p ∧ T ↔ p`
- **Domination**: `p ∨ T ↔ T`
- **Complement**: `p ∨ ¬p ↔ T`

## **Advanced Features**

### **Truth Table Generation**
- Automatic operand extraction and validation
- Complete boolean evaluation for all combinations
- CSV export capabilities
- Custom column range printing

### **Deduction Graphs**
- **A* Search**: Optimal pathfinding in logical space
- **Bidirectional Search**: Forward and backward reasoning
- **Multithreaded**: Parallel computation for large graphs
- **Adjacency Matrix**: Efficient graph representation

### **Model System**
- **Deterministic Models**: Classical boolean logic
- **Stochastic Models**: Probabilistic reasoning (in development)
- **Symbolic Representation**: Human-readable expressions
- **Validity Classification**: Tautology, contradiction, contingency

## **API Integration**

### **gRPC Services** (Ready for Implementation)
```protobuf
service ComputationGraphService {
    rpc BuildGraph(GraphRequest) returns (GraphResponse);
    rpc SearchProof(SearchRequest) returns (ProofResponse);
}

service Neo4JGraphService {
    rpc StoreKnowledge(KnowledgeRequest) returns (StoreResponse);
    rpc QueryGraph(QueryRequest) returns (QueryResponse);
}
```

### **Neo4j Integration** (Infrastructure Ready)
- **Node Types**: Abstract knowledge nodes, logic nodes, domain groups
- **Graph Operations**: Build, read, alter domain and subdomain graphs
- **Persistence**: Scalable knowledge base storage

## **Testing**

```bash
# Run Java tests
mvn test

# Run specific test classes
mvn test -Dtest=ArgumentTest
mvn test -Dtest=PropositionTest
```

## **Contributing**

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### **Development Areas**
- **ML Pipeline Completion**: Python learning algorithms
- **Neo4j Integration**: Database operations
- **Performance Optimization**: Large-scale reasoning
- **Documentation**: Examples and tutorials

## **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## **Acknowledgments**

- **Microsoft ONNX Runtime** for neural network integration
- **Neo4j** for graph database technology
- **gRPC** for modern API design
- **Academic Community** for foundational logic research


