package com.coolioasjulio.neuralnetwork;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;

public class NeuralNetworkParams {
	public NeuralNetworkParams(int[] layers){
		this.layers = layers;
	}
	
	public int[] bias;
	private int[] layers;
	public ActivationStrategy[] strategies;
	public String title;
	
	public int[] getLayers(){
		return layers;
	}
}
