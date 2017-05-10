package com.coolioasjulio.neuralnetwork.activationstrategy;

public class SigmoidActivationStrategy implements ActivationStrategy {
	@Override
	public double activate(double weightedSum) {
		return 1.0/(1.0 + Math.pow(Math.E, -weightedSum));
	}

	@Override
	public double derivativeWeightedSum(double weightedSum) {
		double out = activate(weightedSum);
		return derivativeOutput(out);
	}

	@Override
	public double derivativeOutput(double output) {
		return output * (1-output);
	}
}
