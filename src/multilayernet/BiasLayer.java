package multilayernet;

public class BiasLayer {
	private BiasNeuron[] neurons;
	public BiasLayer(int neurons){
		this.neurons = new BiasNeuron[neurons];
		for(int i = 0; i < neurons; i++){
			this.neurons[i] = new BiasNeuron(this);
		}
	}
	
	public BiasNeuron[] getNeurons(){
		return neurons;
	}
}
