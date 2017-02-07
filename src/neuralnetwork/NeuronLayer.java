package neuralnetwork;

import java.util.Arrays;

public class NeuronLayer implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Neuron[] neurons;
	private NeuralNetwork network;
	private boolean isInput;
	private boolean isOutput;
	public NeuronLayer(NeuralNetwork network, int neurons){
		this.network = network;
		this.neurons = new Neuron[neurons];
		for(int i = 0; i < neurons; i++){
			this.neurons[i] = new Neuron(this);
		}
		int index = Arrays.asList(network.getLayers()).indexOf(this);
		isInput = index == 0;
		isOutput = index == network.getLayers().length - 1;
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
			neuron.setUpDendrites(next);
		}
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
