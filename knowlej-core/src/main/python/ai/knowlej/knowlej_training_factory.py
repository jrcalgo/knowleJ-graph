import asyncio
import logging
import os
import sys
import time
import traceback
import grpcio

from ai.knowlej.grpc_client import KnowlejGrpcClient
from ai.knowlej.data.preprocessing import *
from ai.knowlej.automation import *

class KnowleJTrainingFactory:
    def __init__(self):
        pass

    def train_best_chain_sampler(self):
        pass

    def train_neo4j_domain_graph(self):
        pass

    def train_neo4j_subdomain_graph(self):
        pass

