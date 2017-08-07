package com.coolioasjulio.neuralnetwork;

import java.util.Objects;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;

public class NeuronLayer implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Neuron[] neurons;
	private Neuron[] bias;
	public NeuronLayer(int neurons, int biasNeurons, ActivationStrategy strategy){
		this.neurons = new Neuron[neurons];
		for(int i = 0; i < neurons; i++){
			this.neurons[i] = new Neuron(this, strategy);
		}
		bias = new Neuron[biasNeurons];
		for(int i = 0; i < biasNeurons; i++){
			bias[i] = new Neuron(this, strategy);
			bias[i].output = 1;
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
	
	public void setRandomWeights(NeuronLayer next){		
		for(Neuron neuron:neurons){
			neuron.setUpDendrites(next);
		}
		for(Neuron neuron:bias){
			neuron.setUpDendrites(next);
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
		if(neurons.length != other.getNeurons().length || bias.length != other.getBiasNeurons().length) return false;
		for(int i = 0; i < neurons.length; i++){
			if(!neurons[i].equals(other.getNeurons()[i])) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(neurons, bias);
	}
}
