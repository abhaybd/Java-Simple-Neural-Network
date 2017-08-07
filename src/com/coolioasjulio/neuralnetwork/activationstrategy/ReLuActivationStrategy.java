package com.coolioasjulio.neuralnetwork.activationstrategy;

public class ReLuActivationStrategy implements ActivationStrategy {

	@Override
	public double activate(double weightedSum) {
		return Math.max(weightedSum, 0);
	}

	@Override
	public double derivativeWeightedSum(double weightedSum) {
		return derivativeOutput(activate(weightedSum));
	}

	@Override
	public double derivativeOutput(double output) {
		if(output <= 0) return 0;
		return 1;
	}

}
