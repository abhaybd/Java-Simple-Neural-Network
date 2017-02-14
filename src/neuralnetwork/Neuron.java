package neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Neuron implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	public Dendrite[] dendrites;
	private NeuronLayer layer;
	public double weightedSum;
	public double output;
	public double error;
	private ArrayList<Dendrite> inputs;
	public Neuron(NeuronLayer layer){
		this.layer = layer;
		weightedSum = 0;
		output = 0;
		error = 0;
		inputs = new ArrayList<Dendrite>();
		dendrites = new Dendrite[0];
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
		output = 1/(1+Math.pow(Math.E, -weightedSum)); //sigmoid the neuron value
		return output;
	}
	
	public NeuronLayer getLayer(){
		return layer;
	}
	
	public double getDerivative(){
		return output*(1-output);
	}
	
	public void setUpDendrites(NeuronLayer nextLayer, boolean isBias){
		dendrites = new Dendrite[nextLayer.getNeurons().length];
		Random rand = new Random();
		for(int i = 0; i < dendrites.length; i++){
			if(isBias) {
				dendrites[i] = new Dendrite(this,nextLayer.getNeurons()[i],1);
			}
			else{
				dendrites[i] = new Dendrite(this,nextLayer.getNeurons()[i],rand.nextDouble());				
			}
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
}
