package com.coolioasjulio.neuralnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainParams {
    public double learningRate, momentum, errorThreshold;
    public int batchSize;
    public Map<double[], List<double[]>> trainingData;

    public TrainParams(double[][] inputs, double[][] outputs, double learningRate, double momentum, double errorThreshold) {
        HashMap<double[], List<double[]>> map = new HashMap<>();
        for (int i = 0; i < outputs.length; i++) {
            map.putIfAbsent(outputs[i], new ArrayList<>());
            map.get(outputs[i]).add(inputs[i]);
        }
        init(map, learningRate, momentum, errorThreshold);
    }

    public TrainParams(Map<double[], List<double[]>> trainingData, double learningRate, double momentum, double errorThreshold) {
        init(trainingData, learningRate, momentum, errorThreshold);
    }

    private void init(Map<double[], List<double[]>> trainingData, double learningRate, double momentum, double errorThreshold) {
        this.trainingData = trainingData;
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.errorThreshold = errorThreshold;
        int numEntries = 0;
        for (double[] key : trainingData.keySet()) {
            numEntries += trainingData.get(key).size();
        }
        batchSize = numEntries;
    }
}
