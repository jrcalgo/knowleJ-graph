package ai.knowlej.Automation.API.Services

import ai.knowlej.Automation.API.Neo4jServiceGrpc
import ai.knowlej.Automation.API.GetDesiredGraphRequest
import ai.knowlej.Automation.API.GetDesiredGraphResponse

import io.grpc.stub.StreamObserver

class Neo4jService : Neo4jServiceGrpc.Neo4jServiceImplBase() {

    override fun getDesiredGraph(
        request: GetDesiredGraphRequest,
        responseObserver: StreamObserver<GetDesiredGraphResponse>
    ) {
        val graphId = request.graphId
        val graphData =

        // 2. Build and send the response
        val response = GetDesiredGraphResponse.newBuilder()
            .setGraphData(graphData)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
