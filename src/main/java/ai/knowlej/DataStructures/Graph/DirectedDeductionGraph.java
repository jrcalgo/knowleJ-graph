package ai.knowlej.DataStructures.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ai.knowlej.PropositionalLogic.Logic.Proposition;

public class DirectedDeductionGraph {
    private final ArrayList<DeductionGraphNode> premiseNodes; // initial expression(s) / root(s)
    private final DeductionGraphNode queryNode;     // query node
    private ArrayList<DeductionGraphNode> nodes;    // all graph nodes
    private ArrayList<DeductionGraphNode> forwardNodes;  // nodes from premise(s) to query
    private ArrayList<DeductionGraphNode> backwardNodes; // nodes from query to premise(s)

    private int nodeCount = -1;
    private int premiseCount = 0;
    private int forwardCount = 0;
    private int backwardCount = 0;

    private final HashSet<String> knowledgeBase;
    private final Proposition query;

    // Adjacency matrix: adjacencyMatrix[i][j] = 1 means an edge from node i -> node j, 0 means no edge
    private final ArrayList<ArrayList<Byte>> adjacencyMatrix;

    public DirectedDeductionGraph(HashSet<String> knowledgeBase, Proposition query) {
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // Initialize node lists
        nodes = new ArrayList<>();
        premiseNodes = new ArrayList<>();

        // Fill nodes with knowledge base
        for (int i = 0; i < knowledgeBase.size(); i++) {
            DeductionGraphNode premiseNode
                    = new DeductionGraphNode(knowledgeBase.toArray(new String[0])[i]);
            this.premiseNodes.add(premiseNode);
            this.nodes.add(premiseNode);
            this.nodeCount++;
            this.premiseCount++;
        }

        // Points knowledge base expressions to each other
        if (knowledgeBase.size() > 1) {
            for (int i = 0; i < knowledgeBase.size(); i++) {
                for (int j = knowledgeBase.size() - 1; j > 0; j--) {
                    if (i != j) {
                        // We'll update adjacency matrix inside mutuallyPoint(...)
                        // after adjacencyMatrix is fully initialized
                    } else {
                        break;
                    }
                }
            }
        }

        // Add a detached query node to the graph
        this.queryNode = new DeductionGraphNode(query.getExpression());
        this.nodes.add(this.queryNode);
        this.nodeCount++;

        // ---- Initialize adjacencyMatrix ----
        // We now know how many nodes we have: 'this.nodes.size()'
        this.adjacencyMatrix = new ArrayList<>();
        for (int i = 0; i < this.nodes.size(); i++) {
            ArrayList<Byte> row = new ArrayList<>();
            for (int j = 0; j < this.nodes.size(); j++) {
                row.add((byte) 0);
            }
            this.adjacencyMatrix.add(row);
        }

        // Now that adjacencyMatrix is allocated, let's finalize the knowledgeBase mutual pointers
        if (knowledgeBase.size() > 1) {
            for (int i = 0; i < knowledgeBase.size(); i++) {
                for (int j = knowledgeBase.size() - 1; j > 0; j--) {
                    if (i != j) {
                        this.mutuallyPoint(this.premiseNodes.get(i), this.premiseNodes.get(j));
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Ensures adjacencyMatrix stays in sync with 'this.nodes'.
     * If we add nodes after the constructor, we need to expand the matrix.
     */
    private void expandAdjacencyMatrix() {
        int newSize = this.nodes.size();
        int oldSize = this.adjacencyMatrix.size();

        // If the list was smaller, we add rows/columns to match the newSize
        if (newSize > oldSize) {
            // 1) Expand existing rows
            for (ArrayList<Byte> row : this.adjacencyMatrix) {
                while (row.size() < newSize) {
                    row.add((byte) 0);
                }
            }
            // 2) Add new rows
            for (int i = oldSize; i < newSize; i++) {
                ArrayList<Byte> newRow = new ArrayList<>();
                for (int j = 0; j < newSize; j++) {
                    newRow.add((byte) 0);
                }
                this.adjacencyMatrix.add(newRow);
            }
        }
    }

    @SuppressWarnings("null")
    public void point(DeductionGraphNode outVertex, DeductionGraphNode inVertex) {
        if (outVertex == null || inVertex == null) {
            throw new IllegalArgumentException("Both out and in nodes must be non-null");
        } else if (!this.nodes.contains(outVertex) || !this.nodes.contains(inVertex)) {
            throw new IllegalArgumentException("Both out and in nodes must be in the graph");
        }
        outVertex.addOutNode(inVertex);

        // Update adjacency matrix
        int outIndex = this.nodes.indexOf(outVertex);
        int inIndex  = this.nodes.indexOf(inVertex);
        this.adjacencyMatrix.get(outIndex).set(inIndex, (byte)1);
    }

    private void mutuallyPoint(DeductionGraphNode outVertex, DeductionGraphNode inVertex) {
        // point outVertex -> inVertex
        this.point(outVertex, inVertex);
        // point inVertex -> outVertex
        this.point(inVertex, outVertex);
    }

    public DeductionGraphNode add(String expression) throws Exception {
        // Add new node
        DeductionGraphNode newNode = new DeductionGraphNode(expression);
        this.nodes.add(newNode);
        this.nodeCount++;

        // Expand adjacency matrix to accommodate new node
        this.expandAdjacencyMatrix();

        return this.getNode(expression);
    }

    public void delete(String expression) {
        // You may want to remove row/col from adjacencyMatrix or zero them out
        // For now, let's just remove from nodes (and leave adjacency as-is).
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                this.nodes.remove(node);
                this.nodeCount--;
                break;
            }
        }
        // If you want to remove from adjacencyMatrix fully, you can do so here
        // but then watch your indexes for future calls. Often best to rebuild
        // or keep a "mark as deleted" pattern.
    }

    public void delete(DeductionGraphNode vertex) {
        if (this.nodes.contains(vertex)) {
            this.nodes.remove(vertex);
            this.nodeCount--;
        }
        // same caution about adjacencyMatrix as above
    }

    public boolean isEmpty() {
        return this.nodeCount == -1;
    }

    public int size() {
        return this.nodeCount;
    }

    public int forwardCount() {
        return this.forwardCount;
    }

    public int backwardCount() {
        return this.backwardCount;
    }

    public boolean contains(String expression) throws Exception {
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                return true;
            }
        }
        return false;
    }


    public DeductionGraphNode getNode(String expression) throws Exception {
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                return node;
            }
        }
        throw new Exception("Node not found: " + expression);
    }

    public int findNodeIndex(String expression) throws Exception {
        // Optional: validate expression via new Proposition(...)
        for (int i = 0; i < this.nodes.size(); i++) {
            if (this.nodes.get(i).getExpression().equals(expression)) {
                return i;
            }
        }
        return -1;
    }

    public HashSet<String> getKnowledgeBase() {
        return this.knowledgeBase;
    }

    public String getQuery() {
        return this.query.getExpression();
    }

    public ArrayList<DeductionGraphNode> getNodes() {
        return this.nodes;
    }

    public ArrayList<DeductionGraphNode> getLeafNodes() throws Exception {
        ArrayList<DeductionGraphNode> leafs = new ArrayList<>();
        for (DeductionGraphNode node : this.nodes) {
            if (node.getOutNodes().isEmpty() && !this.isQueryNode(node)) {
                leafs.add(node);
            }
        }
        return leafs;
    }

    public ArrayList<DeductionGraphNode> getForwardNodes() {
        return this.forwardNodes;
    }

    public ArrayList<DeductionGraphNode> getBackwardNodes() {
        return this.backwardNodes;
    }

    public ArrayList<DeductionGraphNode> getPremiseNodes() {
        return this.premiseNodes;
    }

    public DeductionGraphNode getQueryNode() {
        return this.queryNode;
    }

    public void addForwardNode(DeductionGraphNode node) {
        if (this.forwardNodes == null) {
            this.forwardNodes = new ArrayList<>();
            this.forwardNodes.addAll(this.premiseNodes);
        }
        this.forwardNodes.add(node);
        this.forwardCount++;
    }

    public void addBackwardNode(DeductionGraphNode node) {
        if (this.backwardNodes == null) {
            this.backwardNodes = new ArrayList<>();
            this.backwardNodes.add(this.queryNode);
        }
        this.backwardNodes.add(node);
        this.backwardCount++;
    }

    public boolean isNode(DeductionGraphNode vertex) {
        return this.nodes.contains(vertex);
    }

    public boolean isLeafNode(DeductionGraphNode vertex) throws Exception {
        return vertex.getOutNodes().isEmpty() && !this.isQueryNode(vertex);
    }

    public boolean isPremiseNode(DeductionGraphNode vertex) {
        return this.premiseNodes.contains(vertex);
    }

    public boolean isQueryNode(DeductionGraphNode vertex) {
        return vertex.getExpression().equals(this.query.getExpression());
    }

    public boolean isPointing(DeductionGraphNode outVertex, DeductionGraphNode inVertex) throws Exception {
        return outVertex.getOutNodes().contains(inVertex);
    }

    /**
     * Get the adjacency matrix in its current state.
     * 1 = edge present, 0 = no edge
     */
    public ArrayList<ArrayList<Byte>> getAdjacencyMatrix() {
        return this.adjacencyMatrix;
    }

    public void printAdjacencyMatrix() {
        for (ArrayList<Byte> row : this.adjacencyMatrix) {
            for (Byte cell : row) {
                System.out.print(cell + " ");
            }
        }
    }

    private record astarNodeRecord(DeductionGraphNode node, Integer f, int g, int h) {
        astarNodeRecord {
            if (node == null) {
                throw new IllegalArgumentException("Node cannot be null");
            }
            if (f < 0 || g < 0 || h < 0) {
                throw new IllegalArgumentException("f, g, and h must be non-negative");
            }
        }
    }

    public ArrayList<DeductionGraphNode> multithreadedBidirectionalAStar(Set<String> forwardKnowledgeHistory, Set<String> backwardKnowledgeHistory) throws Exception {
        final ConcurrentHashMap<String, astarNodeRecord> forwardClosedSet = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, astarNodeRecord> backwardClosedSet = new ConcurrentHashMap<>();

        final PriorityBlockingQueue<astarNodeRecord> forwardOpenSet = new PriorityBlockingQueue<>();
        final PriorityBlockingQueue<astarNodeRecord> backwardOpenSet = new PriorityBlockingQueue<>();

        final AtomicBoolean pathFound = new AtomicBoolean(false);
        final AtomicReference<DeductionGraphNode> meetingNode = new AtomicReference<>();

        final Object lock = new Object();

        for (DeductionGraphNode premise : this.premiseNodes) {
            int h = heuristic(premise, this.queryNode);
            astarNodeRecord nodeRecord = new astarNodeRecord(premise, null, 0, h);
            forwardOpenSet.add(nodeRecord);
            forwardClosedSet.put(premise.getExpression(), nodeRecord);
        }

        int hBackward = heuristic(this.queryNode, this.premiseNodes.get(0));
        astarNodeRecord nodeRecord = new astarNodeRecord(this.queryNode, null, 0, hBackward);
        backwardOpenSet.add(nodeRecord);
        backwardClosedSet.put(this.queryNode.getExpression(), nodeRecord);

        Runnable forwardTask = () -> {
            try {
                while (!forwardOpenSet.isEmpty() && !pathFound.get()) {
                    astarNodeRecord currentForward = forwardOpenSet.take();

                    for (DeductionGraphNode neighbor : currentForward.node.getOutNodes()) {
                        if (pathFound.get()) break;

                        String expression = neighbor.getExpression();
                        if (forwardClosedSet.containsKey(expression)) continue;

                        int tentativeG = currentForward.g + 1;
                        int h = heuristic(neighbor, this.queryNode);
                        int f = tentativeG + h;

                        astarNodeRecord neighborRecord = new astarNodeRecord(neighbor, f, tentativeG, h);
                        forwardOpenSet.add(neighborRecord);
                        forwardClosedSet.put(expression, neighborRecord);

                        if (backwardClosedSet.containsKey(expression)) {
                            synchronized (lock) {
                                pathFound.set(true);
                                meetingNode.set(neighbor);
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable backwardTask = () -> {
            try {
                while (!backwardOpenSet.isEmpty() && !pathFound.get()) {
                    astarNodeRecord currentBackward = backwardOpenSet.take();

                    for (DeductionGraphNode neighbor : currentBackward.node.getOutNodes()) {
                        if (pathFound.get()) break;

                        String expression = neighbor.getExpression();
                        if (backwardClosedSet.containsKey(expression)) continue;

                        int tentativeG = currentBackward.g + 1;
                        int h = heuristic(neighbor, this.queryNode);
                        int f = tentativeG + h;

                        astarNodeRecord neighborRecord = new astarNodeRecord(neighbor, f, tentativeG, h);
                        backwardOpenSet.add(neighborRecord);
                        backwardClosedSet.put(expression, neighborRecord);

                        if (forwardClosedSet.containsKey(expression)) {
                            synchronized (lock) {
                                pathFound.set(true);
                                meetingNode.set(neighbor);
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread forwardThread = new Thread(forwardTask, "Forward A*");
        Thread backwardThread = new Thread(backwardTask, "Backward A*");

        forwardThread.start();
        backwardThread.start();

        try {
            forwardThread.join();
            backwardThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        if (meetingNode != null) {
            ArrayList<DeductionGraphNode> forwardPath = reconstructPath(meetingNode.get(), forwardClosedSet);
            ArrayList<DeductionGraphNode> backwardPath = reconstructPath(meetingNode.get(), backwardClosedSet);
            Collections.reverse(backwardPath);
            forwardPath.addAll(backwardPath.subList(1, backwardPath.size()));
            return forwardPath;
        }

        return null;
    }

    private int heuristic(DeductionGraphNode start, DeductionGraphNode goal) {
        return 0;
    }

    private ArrayList<DeductionGraphNode> reconstructPath(DeductionGraphNode node, ConcurrentHashMap<String, astarNodeRecord> closedSet) {
        ArrayList<DeductionGraphNode> path = new ArrayList<>();
        path.add(node);

        while (closedSet.containsKey(node.getExpression())) {
            astarNodeRecord nodeRecord = closedSet.get(node.getExpression());
            node = nodeRecord.node;
            path.add(node);
        }

        return path;
    }
}
