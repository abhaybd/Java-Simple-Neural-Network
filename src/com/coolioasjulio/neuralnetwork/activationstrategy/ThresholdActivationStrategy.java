package com.coolioasjulio.neuralnetwork.activationstrategy;

public class ThresholdActivationStrategy implements ActivationStrategy {

	@Override
	public double activate(double weightedSum) {
		return weightedSum >= 1?1:0;
	}

	@Override
	public double derivativeWeightedSum(double weightedSum) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double derivativeOutput(double output) {
		// TODO Auto-generated method stub
		return 0;
	}

}
