package ai.knowlej.Automation.API

import io.grpc.Server
import io.grpc.ServerBuilder
import java.io.IOException

import ai.knowlej.Automation.API.Services.ComputationGraphService
import ai.knowlej.Automation.API.Services.Neo4jService
import ai.knowlej.Automation.API.Services.NodeDataService

class KnowleJGrpcServer {
    private var server: Server? = null

    @Throws(IOException::class)
    private fun start() {
        server = ServerBuilder.forPort(50051)
            .addService(ComputationGraphService())
            .addService(Neo4jService())
            .addService(NodeDataService())
            .build()
            .start()

        println("Server started, listening on port 50051")

        // Hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(Thread {
            System.err.println("*** shutting down gRPC server since JVM is shutting down")
            this@KnowleJGrpcServer.stop()
            System.err.println("*** server shut down")
        })
    }

    private fun stop() {
        server?.shutdown()
    }

    @Throws(InterruptedException::class)
    private fun blockUntilShutdown() {
        server?.awaitTermination()
    }

    companion object {
        fun initialize() {
            val grpcServer = KnowleJGrpcServer()
            try {
                grpcServer.start()
                grpcServer.blockUntilShutdown()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
