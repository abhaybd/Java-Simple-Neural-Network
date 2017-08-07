package com.coolioasjulio.neuralnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainParams {
	public double learningRate, momentum, errorThreshold;
	public boolean classification;
	public int batchSize;
	public int threads = 1;
	public Map<double[],List<double[]>> trainingData;
	
	public TrainParams(double[][] inputs, double[][] outputs, double learningRate, double momentum, double errorThreshold){
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.errorThreshold = errorThreshold;
		batchSize = inputs.length;
		
		trainingData = new HashMap<double[],List<double[]>>();
		for(int i = 0; i < outputs.length; i++){
			trainingData.putIfAbsent(outputs[i], new ArrayList<double[]>());
			trainingData.get(outputs[i]).add(inputs[i]);
		}
	}
	
	public TrainParams(Map<double[],List<double[]>> trainingData, double learningRate, double momentum, double errorThreshold){
		this.trainingData = trainingData;
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.errorThreshold = errorThreshold;
		int numEntries = 0;
		for(double[] key:trainingData.keySet()){
			numEntries += trainingData.get(key).size();
		}
		batchSize = numEntries;
	}
}
