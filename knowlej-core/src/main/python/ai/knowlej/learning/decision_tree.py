import numpy as np


# -------------------------------------------------------------------------
# 1) Decision Tree for Regression
#    - Grows a small tree by finding the best split at each node
#    - Uses mean of target values in a leaf as prediction
# -------------------------------------------------------------------------
class DecisionTreeRegressor:
    def __init__(self,
                 max_depth=3,
                 min_samples_split=2,
                 min_impurity_decrease=1e-7):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.min_impurity_decrease = min_impurity_decrease
        self.root = None

    class Node:
        __slots__ = ("feature_index",
                     "threshold",
                     "left",
                     "right",
                     "value",
                     "impurity")

        def __init__(self,
                     feature_index=None,
                     threshold=None,
                     value=None,
                     left=None,
                     right=None,
                     impurity=None):
            self.feature_index = feature_index
            self.threshold = threshold
            self.value = value
            self.left = left
            self.right = right
            self.impurity = impurity

    def fit(self, X, y):
        """
        Builds the regression tree from data.
        """
        # Build the tree recursively
        self.root = self._build_tree(X, y, depth=0)

    def predict(self, X):
        """
        Predict on a set of samples X.
        """
        return np.array([self._predict_sample(self.root, sample) for sample in X])

    def _predict_sample(self, node, sample):
        """
        Traverse the tree until reaching a leaf.
        """
        if node.value is not None:
            return node.value

        if sample[node.feature_index] <= node.threshold:
            return self._predict_sample(node.left, sample)
        else:
            return self._predict_sample(node.right, sample)

    def _build_tree(self, X, y, depth):
        """
        Recursively find the best split and build subtrees.
        """
        n_samples, n_features = X.shape
        impurity = np.var(y) * len(y)

        if depth >= self.max_depth or n_samples < self.min_samples_split or impurity < self.min_impurity_decrease:
            leaf_value = np.mean(y)
            return self.Node(value=leaf_value, impurity=impurity)

        best_feature, best_threshold, best_impurity = None, None, np.inf
        for feature_index in range(n_features):
            thresholds = np.unique(X[:, feature_index])
            for threshold in thresholds:
                left_mask = X[:, feature_index] <= threshold
                right_mask = ~left_mask

                if np.sum(left_mask) == 0 or np.sum(right_mask) == 0:
                    continue

                y_left = y[left_mask]
                y_right = y[right_mask]

                impurity_left = np.var(y_left) * len(y_left)
                impurity_right = np.var(y_right) * len(y_right)
                total_impurity = impurity_left + impurity_right

                if total_impurity < best_impurity:
                    best_impurity = total_impurity
                    best_feature = feature_index
                    best_threshold = threshold

        if best_feature is None:
            leaf_value = np.mean(y)
            return self.Node(value=leaf_value, impurity=impurity)

        left_mask = X[:, best_feature] <= best_threshold
        right_mask = ~left_mask

        left_node = self._build_tree(X[left_mask], y[left_mask], depth+1)
        right_node = self._build_tree(X[right_mask], y[right_mask], depth+1)

        node = self.Node(
            feature_index=best_feature,
            threshold=best_threshold,
            left=left_node,
            right=right_node,
            impurity=best_impurity
        )
        return node

# -------------------------------------------------------------------------
# 2) Gradient Boosting Regressor
#    - Iteratively builds an additive model: F_{m}(x) = F_{m-1}(x) + learning_rate * h_m(x)
#    - Each new tree h_m(x) is fit to the negative gradient (residual) of the loss
# -------------------------------------------------------------------------
class GradientBoostingRegressor:
    def __init__(self,
                 n_estimators=50,
                 max_depth=3,
                 learning_rate=0.1,
                 min_samples_split=2,
                 min_impurity_decrease=1e-7):
        self.n_estimators = n_estimators
        self.max_depth = max_depth
        self.learning_rate = learning_rate
        self.min_samples_split = min_samples_split
        self.min_impurity_decrease = min_impurity_decrease

        self.trees = []
        self.F0 = None

    def fit(self, X, y):
        """
        Train GBDT on the training data X, y using MSE as the loss.
        """
        # Initialize model with mean (or median) of y
        self.F0 = np.mean(y)
        current_prediction = np.full(len(y), self.F0)

        for _ in range(self.n_estimators):
            residuals = y - current_prediction

            tree = DecisionTreeRegressor(
                max_depth=self.max_depth,
                min_samples_split=self.min_samples_split,
                min_impurity_decrease=self.min_impurity_decrease
            )
            tree.fit(X, residuals)
            self.trees.append(tree)

            update = tree.predict(X)
            current_prediction += self.learning_rate * update

    def predict(self, X):
        """
        Predict new samples by summing the initial model and all boosted trees.
        """
        y_pred = np.full(X.shape[0], self.F0, dtype=float)

        for tree in self.trees:
            y_pred += self.learning_rate * tree.predict(X)

        return y_pred

def _traverse_and_collect(
    tree,
    tree_id,
    nodes_featureids,
    nodes_modes,
    nodes_values,
    nodes_truenodeids,
    nodes_falsenodeids,
    nodes_nodeids,
    nodes_treeids,
    target_nodeids,
    target_ids,
    target_weights
):
    """
    Recursively traverse the tree, fill in ONNX arrays for the operator.
    """
    # BFS or DFS stack
    stack = [tree.root]
    while stack:
        node = stack.pop()
        nid = node.node_id
        nodes_treeids.append(tree_id)
        nodes_nodeids.append(nid)

        if node.value is not None:
            nodes_featureids.append(0)
            nodes_modes.append("LEAF")
            nodes_values.append(0.0)

            # Children set to -1 for leaves
            nodes_truenodeids.append(-1)
            nodes_falsenodeids.append(-1)

            target_nodeids.append(nid)
            target_ids.append(0)
            target_weights.append(float(node.value))

        else:
            nodes_featureids.append(node.feature_index)
            nodes_modes.append("BRANCH_LEQ")
            nodes_values.append(float(node.threshold))
            left_id = node.left.node_id
            right_id = node.right.node_id
            nodes_truenodeids.append(left_id)
            nodes_falsenodeids.append(right_id)

            # Push children onto stack
            stack.append(node.right)
            stack.append(node.left)


def export_gbdt_to_onnx(gbdt, name="GBDTModel"):
    """
    Convert our custom GBDT to an ONNX model using TreeEnsembleRegressor operator.
    """
    nodes_treeids = []
    nodes_nodeids = []
    nodes_featureids = []
    nodes_modes = []
    nodes_values = []
    nodes_truenodeids = []
    nodes_falsenodeids = []

    target_nodeids = []
    target_ids = []
    target_weights = []

    for i, tree in enumerate(gbdt.trees):
        _traverse_and_collect(
            tree,
            tree_id=i,
            nodes_featureids=nodes_featureids,
            nodes_modes=nodes_modes,
            nodes_values=nodes_values,
            nodes_truenodeids=nodes_truenodeids,
            nodes_falsenodeids=nodes_falsenodeids,
            nodes_nodeids=nodes_nodeids,
            nodes_treeids=nodes_treeids,
            target_nodeids=target_nodeids,
            target_ids=target_ids,
            target_weights=target_weights
        )

    base_values = [float(gbdt.init_)]

    regressor_attrs = {
        "name": "TreeEnsembleRegressor",
        "domain": "ai.onnx.ml",
        "base_values": base_values,
        "n_targets": 1,
        "nodes_treeids": nodes_treeids,
        "nodes_nodeids": nodes_nodeids,
        "nodes_featureids": nodes_featureids,
        "nodes_modes": nodes_modes,
        "nodes_values": nodes_values,
        "nodes_truenodeids": nodes_truenodeids,
        "nodes_falsenodeids": nodes_falsenodeids,
        "target_nodeids": target_nodeids,
        "target_ids": target_ids,
        "target_weights": target_weights,
        "post_transform": "NONE",
        "aggregate_function": "SUM",
    }

    # Because each tree is effectively scaled by gbdt.learning_rate,
    # we must multiply each leaf by that factor. However, we already
    # stored 'node.value' directly.
    #
    # Two ways:
    # 1. Multiply target_weights by gbdt.learning_rate during the data collection.
    # 2. Or rely on 'post_transform' logic (less direct for regression).
    #
    for i in range(len(target_weights)):
        target_weights[i] *= gbdt.learning_rate

    regressor_attrs["target_weights"] = target_weights

    regressor_node = helper.make_node(
        op_type="TreeEnsembleRegressor",
        inputs=["X"],
        outputs=["Y"],
        **regressor_attrs
    )

    input_tensor = helper.make_tensor_value_info("X", TensorProto.FLOAT, [None, None])
    output_tensor = helper.make_tensor_value_info("Y", TensorProto.FLOAT, [None, 1])

    graph = helper.make_graph(
        nodes=[regressor_node],
        name=name,
        inputs=[input_tensor],
        outputs=[output_tensor]
    )

    onnx_model = helper.make_model(graph, producer_name="CustomGBDTtoONNX")
    onnx_model.opset_import[0].domain = "ai.onnx"
    onnx_model.opset_import[0].version = 13

    onnx_model.opset_import.append(onnx.helper.make_opsetid("ai.onnx.ml", 3))

    return onnx_model
