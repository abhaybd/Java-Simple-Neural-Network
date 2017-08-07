package com.coolioasjulio.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;

public class Neuron implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	public Dendrite[] dendrites;
	private NeuronLayer layer;
	public double weightedSum;
	public double output;
	public double error;
	private ArrayList<Dendrite> inputs;
	private ActivationStrategy strategy;
	
	public Neuron(NeuronLayer layer, ActivationStrategy strategy){
		this.layer = layer;
		weightedSum = 0;
		output = 0;
		error = 0;
		inputs = new ArrayList<Dendrite>();
		dendrites = new Dendrite[0];
		this.strategy = strategy;
	}
	
	public void setDendrites(Dendrite[] dendrites){
		this.dendrites = dendrites;
	}
	
	public double getWeightedSum(){
		return weightedSum;
	}
	
	public List<Dendrite> getInputs(){
		return inputs;
	}
	
	public void addInput(Dendrite dendrite){
		inputs.add(dendrite);
	}
	
	public void setError(double error){
		this.error = error;
	}
	
	public double getError(){
		return error;
	}
	
	public double getOutput(){
		return output;
	}
	
	public double activationFunction(){
		output = strategy.activate(weightedSum);
		return output;
	}
	
	public NeuronLayer getLayer(){
		return layer;
	}
	
	public double getDerivative(){
		return strategy.derivativeOutput(output);
	}
	
	public void setUpDendrites(NeuronLayer nextLayer){
		dendrites = new Dendrite[nextLayer.getNeurons().length];
		Random rand = new Random();
		for(int i = 0; i < dendrites.length; i++){
			dendrites[i] = new Dendrite(this,nextLayer.getNeurons()[i],(rand.nextDouble()-0.5));
		}		
	}
	
	public Dendrite[] getDendrites(){
		return dendrites;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Neuron)) return false;
		Neuron other = (Neuron)o;
		return error == other.getError() && output == other.getOutput() && weightedSum == other.weightedSum;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(error, output, weightedSum, getDendrites());
	}
}
