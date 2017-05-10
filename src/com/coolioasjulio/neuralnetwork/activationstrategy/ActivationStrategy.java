package com.coolioasjulio.neuralnetwork.activationstrategy;

import java.lang.reflect.Array;
public interface ActivationStrategy {
	public double activate(double weightedSum);
	
	public double derivativeWeightedSum(double weightedSum);
	
	public double derivativeOutput(double output);
	
	/**
	 * Fill an ActivationStrategy[] with the desired activation strategy, one for each layer.
	 * @param strategyType Class of the strategy that implements ActivationStrategy. If the supplied class doesn't implement ActivationStrategy, then a SigmoidActivationStrategy will be used.
	 * @param length length of the array to return
	 * @return An ActivationStrategy[] with length length and made up classes strategyType.
	 */
	public static ActivationStrategy[] fillArray(Class<?> strategyType, final int length) {
		try{
			if(ActivationStrategy.class.isAssignableFrom(strategyType)){
				ActivationStrategy[] strats = (ActivationStrategy[]) Array.newInstance(strategyType, length);
				for(int i = 0; i < strats.length; i++){
					strats[i] = (ActivationStrategy) strategyType.newInstance();
				}
				return  strats;
			}
			throw new InstantiationException();
			
		} catch (InstantiationException | IllegalAccessException e) {
			SigmoidActivationStrategy[] strats = new SigmoidActivationStrategy[length];
			for(int i = 0; i < strats.length; i++){
				strats[i] = new SigmoidActivationStrategy();
			}
			return strats;
		}
	}
}
