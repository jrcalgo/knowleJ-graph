package ai.knowlej.DataStructures.Automation;

import ai.onnxruntime.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A record representing an ONNX model loaded using Microsoft ONNX Runtime.
 * It provides functionality to perform inference with the loaded model.
 */
public record ONNXModel(String modelPath, OrtSession session) implements AutoCloseable {
    private static final OrtEnvironment env;

    static {
        env = OrtEnvironment.getEnvironment();
    }

    /**
     * Factory method to load an ONNX model from the specified file path.
     *
     * @param modelPath The file path to the ONNX model.
     * @return An instance of ONNXModel with the loaded session.
     * @throws OrtException If an error occurs while loading the model.
     */
    public static ONNXModel loadModel(String modelPath) throws OrtException {
        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
        sessionOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);

        OrtSession session = env.createSession(modelPath, sessionOptions);
        return new ONNXModel(modelPath, session);
    }

    /**
     * Performs inference using the loaded ONNX model.
     *
     * @param inputData  The input data as a float array.
     * @param inputShape The shape of the input tensor.
     * @return The model's output as a float 2D array.
     * @throws OrtException If an error occurs during inference.
     */
    public float[][] runInference(float[] inputData, long[] inputShape) throws OrtException {
        // Create input tensor
        try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputData), inputShape)) {
            Map<String, OnnxTensor> inputs = new HashMap<>();
            String inputName = session.getInputNames().iterator().next();
            inputs.put(inputName, inputTensor);

            // Run inference
            try (OrtSession.Result results = session.run(inputs)) {
                String outputName = session.getOutputNames().iterator().next();
                Optional<OnnxValue> outputValue = results.get(outputName);
                return (float[][]) outputValue.get().getValue();
            }
        }
    }

    /**
     * Closes the ONNX Runtime session and releases resources.
     *
     * @throws OrtException If an error occurs while closing the session.
     */
    @Override
    public void close() throws OrtException {
        if (session != null) {
            session.close();
        }
    }
}