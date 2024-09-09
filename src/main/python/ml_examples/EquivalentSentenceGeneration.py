import py4j

import torch
import torch.nn as nn
import torch.optim as optim

class LogicNet(nn.Module):
    def __init__(self):
        super(LogicNet, self).__init__()
