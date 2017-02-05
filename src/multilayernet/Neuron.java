package multilayernet;

import java.util.Random;

public class Neuron {
	public Dendrite[] dendrites;
	private NeuronLayer layer;
	public double tempSum;
	public Neuron(NeuronLayer layer){
		this.layer = layer;
		tempSum = 0;
	}
	
	public double activationFunction(){
		tempSum = 1/(1+Math.pow(Math.E, -tempSum)); //sigmoid the neuron value
		return tempSum;
	}
	
	public void setUpDendrites(NeuronLayer nextLayer){
		dendrites = new Dendrite[nextLayer.getNeurons().length];
		Random rand = new Random();
		for(int i = 0; i < dendrites.length; i++){
			dendrites[i] = new Dendrite(this,nextLayer.getNeurons()[i],rand.nextDouble());
		}		
	}
	
	public Dendrite[] getDendrites(){
		return dendrites;
	}
}
