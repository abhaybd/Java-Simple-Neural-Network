package com.coolioasjulio.neuralnetwork;

import java.util.Arrays;

public class NeuronLayer implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Neuron[] neurons;
	private Neuron[] bias;
	private NeuralNetwork network;
	private boolean isInput;
	private boolean isOutput;
	public NeuronLayer(NeuralNetwork network, int neurons, int biasNeurons){
		this.network = network;
		this.neurons = new Neuron[neurons];
		for(int i = 0; i < neurons; i++){
			this.neurons[i] = new Neuron(this);
		}
		bias = new Neuron[biasNeurons];
		for(int i = 0; i < biasNeurons; i++){
			bias[i] = new Neuron(this);
			bias[i].output = 1;
		}
		if(network != null){
			int index = Arrays.asList(network.getLayers()).indexOf(this);
			isInput = index == 0;
			isOutput = index == network.getLayers().length - 1;			
		}
	}
	
	public double[] softMax(){
		double sum = 0;
		for(Neuron neuron:neurons){
			sum += Math.pow(Math.E, neuron.getWeightedSum());
		}
		double[] toReturn = new double[neurons.length];
		for(int i = 0; i < neurons.length; i++){
			toReturn[i] = Math.pow(Math.E, neurons[i].getWeightedSum())/sum;
		}
		return toReturn;
	}
	
	public boolean isInput(){
		return isInput;
	}
	
	public boolean isOutput(){
		return isOutput;
	}
	
	public NeuralNetwork getNetwork(){
		return network;
	}
	
	public void setRandomWeights(NeuronLayer next){		
		for(Neuron neuron:neurons){
			neuron.setUpDendrites(next,false);
		}
		for(Neuron neuron:bias){
			neuron.setUpDendrites(next, true);
		}
	}
	
	public Neuron[] getBiasNeurons(){
		return bias;
	}
	
	public Neuron[] getNeurons(){
		return neurons;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof NeuronLayer)) return false;
		NeuronLayer other = (NeuronLayer)o;
		if(neurons.length != other.getNeurons().length || isInput != other.isInput() || isOutput != other.isOutput()) return false;
		for(int i = 0; i < neurons.length; i++){
			if(!neurons[i].equals(other.getNeurons()[i])) return false;
		}
		return true;
	}
}
