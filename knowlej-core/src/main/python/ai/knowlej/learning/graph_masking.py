"""
Geometric Deep Learning model architecture for learning graphs and graphlets from the knowleJ graph, and optimizing
connections between nodes, inference chains & graph shapes.
"""

import torch
import torch.nn.functional as F
from torch_geometric.nn import GCNConv


def continuous_adjacency_matrix(edge_index, num_nodes):
    """
    Converts edge index to continuous adjacency matrix.
    """
    adj = torch.zeros((num_nodes, num_nodes), dtype=torch.float)
    for i, j in edge_index.t().tolist():
        adj[i, j] = 1
    return adj


class GlobalGraphNetwork(torch.nn.Module):
    """
    A "global" graph optimizer model architecture for learning from a knowledge graph
    and optimizing connections or graph shapes at a broader (global) level.
    """
    def __init__(self, in_channels: int, hidden_channels: int, out_channels: int):
        super().__init__()
        # Example: 3 GCN layers in sequence
        self.conv1 = GCNConv(in_channels, hidden_channels)
        self.conv2 = GCNConv(hidden_channels, hidden_channels)
        self.conv3 = GCNConv(hidden_channels, out_channels)

    def forward(self, x, edge_index):
        """
        Arguments:
          x (Tensor): Node feature matrix of shape [num_nodes, in_channels].
          edge_index (LongTensor): Graph connectivity of shape [2, num_edges].
        """
        x = F.relu(self.conv1(x, edge_index))
        x = F.relu(self.conv2(x, edge_index))
        x = self.conv3(x, edge_index)
        return x


class LocalGraphNetwork(torch.nn.Module):
    """
    A "local" graph optimizer model architecture for focusing on smaller subgraphs or
    'graphlets,' refining node connections at a more granular (local) level.
    """
    def __init__(self, in_channels: int, hidden_channels: int, out_channels: int):
        super().__init__()
        # Again, 3 GCN layers; you can customize architecture depth or type
        self.conv1 = GCNConv(in_channels, hidden_channels)
        self.conv2 = GCNConv(hidden_channels, hidden_channels)
        self.conv3 = GCNConv(hidden_channels, out_channels)

    def forward(self, x, edge_index):
        """
        Similar arguments as GlobalGraphOptimizer:
          x (Tensor): Node feature matrix of shape [num_nodes, in_channels].
          edge_index (LongTensor): Graph connectivity of shape [2, num_edges].
        """
        x = F.relu(self.conv1(x, edge_index))
        x = F.relu(self.conv2(x, edge_index))
        x = self.conv3(x, edge_index)
        return x


class DifferentiableMask(torch.nn.Module):
    """
    Learns a differentiable mask for the adjacency matrix.
    """
    def __init__(self, global_in_channels: int, global_hidden_channels: int,
                 global_out_channels: int, local_in_channels: int, local_hidden_channels: int,
                 local_out_channels: int, edge_index: torch.Tensor, num_nodes: int, mask_threshold: float, lr: float):
        super().__init__()
        self.global_network = GlobalGraphNetwork(global_in_channels, global_hidden_channels,
                                                 global_out_channels)
        self.local_network = LocalGraphNetwork(local_in_channels, local_hidden_channels,
                                               local_out_channels)
        self.edge_index = edge_index
        self.num_nodes = num_nodes

        self.num_edges = edge_index.size(1)

        self.edge_params = torch.nn.Parameter(torch.full((self.num_edges,), mask_threshold, dtype=torch.float))
        self.adjacency_matrix = continuous_adjacency_matrix(edge_index, num_nodes)

        self.optimizer = torch.optim.Adam(self.parameters(), lr=lr)

    def global_forward(self, X: torch.Tensor):
        edge_weight = torch.sigmoid(self.edge_params)
        return self.global_network(X, self.edge_index, edge_weight=edge_weight)

    def local_forward(self, X: torch.Tensor):
        edge_weight = torch.sigmoid(self.edge_params)
        return self.local_network(X, self.edge_index, edge_weight=edge_weight)

    def train_global_model(self, X, y):
        """
        Trains the model.
        """
        for epoch in range(self.num_epochs):
            self.train()
            self.optimizer.zero_grad()
            loss = self._global_loss(X, y)
            loss.backward()
            self.optimizer.step()

    def _global_loss(self, X, y):
        """
        Returns the loss.
        """
        global_output = self.global_forward(X)
        return F.cross_entropy(global_output, y)

    def train_local_model(self, X, y):
        """
        Trains the model.
        """
        for epoch in range(self.num_epochs):
            self.train()
            self.optimizer.zero_grad()
            loss = self._local_loss(X, y)
            loss.backward()
            self.optimizer.step()

    def _local_loss(self, X, y):
        """
        Returns the loss.
        """
        local_output = self.local_forward(X)
        return F.cross_entropy(local_output, y)

    def get_edge_weights(self):
        """
        Returns the edge weights.
        """
        return torch.sigmoid(self.edge_params)

    def get_edge_mask(self):
        with torch.no_grad():
            mask = self.get_edge_weights() >= self.mask_threshold
        return mask

    def get_adjacency_matrix(self):
        with torch.no_grad():
            mask = self.get_edge_mask()
        return self.adjacency_matrix * mask
