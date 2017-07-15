package com.coolioasjulio.neuralnetwork;

public class TrainParams {
	public double[][] inputs, outputs;
	public double learningRate, momentum, errorThreshold;
	public boolean classification;
	public int batchSize;
	public int threads = 1;
	
	public TrainParams(double[][] inputs, double[][] outputs, double learningRate, double momentum, double errorThreshold){
		this.inputs = inputs;
		this.outputs = outputs;
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.errorThreshold = errorThreshold;
		batchSize = inputs.length;
	}
}
