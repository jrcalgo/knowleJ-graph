package ai.knowlej.Automation.API.Services

import ai.knowlej.Automation.API.ComputationGraphServiceGrpc
import ai.knowlej.Automation.API.GetComputationGraphRequest
import ai.knowlej.Automation.API.GetComputationGraphResponse

import io.grpc.stub.StreamObserver

class ComputationGraphService : ComputationGraphServiceGrpc.ComputationGraphServiceImplBase() {

    override fun getCurrentComputationGraph(
        request: GetComputationGraphRequest,
        responseObserver: StreamObserver<GetComputationGraphResponse>
    ) {
        val runtimeGraphData =

        val response = GetComputationGraphResponse.newBuilder()
            .setGraphData(runtimeGraphData)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
