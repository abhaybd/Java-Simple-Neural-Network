package multilayernet;

import java.util.Random;

public class BiasNeuron {
	private BiasLayer layer;
	public double weight;
	public BiasNeuron(BiasLayer layer){
		this.layer = layer;
		weight = new Random().nextDouble();
	}
	
	public double setWeight(double weight){
		this.weight = weight;
		return weight;
	}
	
	public double adjustWeight(double adjust){
		weight += adjust;
		return weight;
	}
	
	public double getWeight(){
		return weight;
	}
}
