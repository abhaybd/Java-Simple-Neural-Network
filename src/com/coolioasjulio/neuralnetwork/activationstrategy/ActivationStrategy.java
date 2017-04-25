package com.coolioasjulio.neuralnetwork.activationstrategy;

public interface ActivationStrategy {
	public double activate(double weightedSum);
	
	public double derivativeWeightedSum(double weightedSum);
	
	public double derivativeOutput(double output);
}
