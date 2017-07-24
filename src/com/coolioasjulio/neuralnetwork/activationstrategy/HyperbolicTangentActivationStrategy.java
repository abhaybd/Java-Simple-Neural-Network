package com.coolioasjulio.neuralnetwork.activationstrategy;

public class HyperbolicTangentActivationStrategy implements ActivationStrategy {

	@Override
	public double activate(double weightedSum) {
		return Math.tanh(weightedSum);
	}

	@Override
	public double derivativeWeightedSum(double weightedSum) {
		double out = activate(weightedSum);
		return derivativeOutput(out);
	}

	@Override
	public double derivativeOutput(double output) {
		return 1/Math.cosh(output);
	}

}
