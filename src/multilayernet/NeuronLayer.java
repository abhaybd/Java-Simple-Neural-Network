package multilayernet;

public class NeuronLayer {
	private Neuron[] neurons;
	public NeuronLayer(int neurons){
		this.neurons = new Neuron[neurons];
		for(int i = 0; i < neurons; i++){
			this.neurons[i] = new Neuron(this);
		}
	}
	
	public void setRandomWeights(NeuronLayer next){		
		for(Neuron neuron:neurons){
			neuron.setUpDendrites(next);
		}
	}
	
	public Neuron[] getNeurons(){
		return neurons;
	}
}
