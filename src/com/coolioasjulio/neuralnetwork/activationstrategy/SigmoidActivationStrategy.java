package com.coolioasjulio.neuralnetwork.activationstrategy;

public class SigmoidActivationStrategy implements ActivationStrategy {
	public static ActivationStrategy[] fillArray(int length){
		ActivationStrategy[] arr = new ActivationStrategy[length];
		for(int i = 0; i < arr.length; i++){
			arr[i] = new SigmoidActivationStrategy();
		}
		return arr;
	}
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
