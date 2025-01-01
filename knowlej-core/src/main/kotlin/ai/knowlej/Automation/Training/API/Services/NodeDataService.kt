package ai.knowlej.Automation.API.Services

import ai.knowlej.Automation.API.NodeDataServiceGrpc
import ai.knowlej.Automation.API.GetNodeDataRequest
import ai.knowlej.Automation.API.GetNodeDataResponse
import ai.knowlej.Automation.API.NodeData

import io.grpc.stub.StreamObserver

class NodeDataService : NodeDataServiceGrpc.NodeDataServiceImplBase() {

    override fun getNodeData(
        request: GetNodeDataRequest,
        responseObserver: StreamObserver<GetNodeDataResponse>
    ) {
        val nodeIds = request.nodeIdsList

        val nodes = nodeIds.map { nodeId ->
            NodeData.newBuilder()
                .setNodeId(nodeId)
                .setContent("Content for node $nodeId")
                .build()
        }

        val response = GetNodeDataResponse.newBuilder()
            .addAllNodes(nodes)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
