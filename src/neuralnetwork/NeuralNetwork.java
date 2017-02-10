package neuralnetwork;

import java.util.HashMap;
import java.util.Scanner;

public class NeuralNetwork implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1}, new int[]{1,0,0});
		double[][] inputs = new double[][]{
			{0,1},
			{1,0},
			{0,0},
			{1,1}
		};
		double[] output = new double[]{1,1,0,0};
		net.train(inputs, output, 0.1, 0.9, 10000);
		String response = "";
		Scanner input = new Scanner(System.in);
		while(!(response = input.nextLine()).equals("quit")){
			String[] parts = response.split(",");
			double[] nums = new double[parts.length];
			for(int i = 0; i < parts.length; i++){
				nums[i] = Double.parseDouble(parts[i]);
			}
			System.out.println(net.guess(nums));
			
		}
		input.close();
	}
	
	private NeuronLayer[] layers;
	public NeuralNetwork(int[] layers, int[] bias){
		if(bias == null || bias.length != layers.length){
			bias = new int[layers.length];
		}
		this.layers = new NeuronLayer[layers.length];
		for(int i = 0; i < layers.length; i++){
			this.layers[i] = new NeuronLayer(this,layers[i],bias[i]);
		}
		randomWeights();
	}
	
	public NeuronLayer[] getLayers(){
		return layers;
	}
	
	public double guess(double[] input){
		for(NeuronLayer layer:layers){
			for(Neuron neuron:layer.getNeurons()){
				neuron.weightedSum = 0;
			}
		}
		return sigmoid(evaluate(input));
	}
	
	public void train(double[][] inputs, double[] outputs, double learningRate, double momentum, int maxIterations){
		int runs = 0;
		double startError = 0;
		while(true){
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			//if(runs>=maxIterations)break;
			for(int i = 0; i < inputs.length; i++){
				double sum = evaluate(inputs[i]);//get sum
				double result = sigmoid(sum); //calculate final result
				double error = Math.pow(outputs[i]-result,2)/2; //calculate mean squared error
				errorSum += error;
				//System.out.println("Error: " + error);
				getErrors(result, outputs[i]);
				updateWeights(dendriteDeltaMap, learningRate, momentum);
			}
			double avgError = errorSum/inputs.length;
			if(runs == 0) startError = avgError;
			System.out.println("Epoch: " + runs + ", error: " + avgError);
			runs++;
			if(runs>=maxIterations || avgError <= Math.pow(0.03, 2)/2) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
		printWeights();
	}

	private void randomWeights(){
		for(int i = 0; i < layers.length-1; i++){
			layers[i].setRandomWeights(layers[i+1]);
		}
	}

	private double evaluate(double[] input){
		//reset the neuron values
		for(NeuronLayer layer:layers){
			for(Neuron neuron:layer.getNeurons()){
				neuron.weightedSum = 0;
			}
		}
		
		//set the output of the input neurons to the input
		for(int i = 0; i < layers[0].getNeurons().length; i++){
			layers[0].getNeurons()[i].output = input[i];
		}
		
		//cycle through all the neurons
		for(int i = 0; i < layers.length; i++){
			for(Neuron neuron:layers[i].getNeurons()){
				if(i != 0) neuron.activationFunction(); //apply the activation function if not an input neuron
				if(i != layers.length - 1){
					for(Dendrite dendrite:neuron.getDendrites()){
						//Increment the weightedSum of the destination neuron by the source neuron output scaled by the weight
						dendrite.getEnd().weightedSum += neuron.output * dendrite.weight;
					}
				}
			}
		}
		double result = layers[layers.length-1].getNeurons()[0].weightedSum; //return the output of the first output neuron.
		return result;
	}
	
	private double sigmoid(double x){
		return 1/(1+Math.pow(Math.E, -x));
	}
	
	private void getErrors(double result, double expectedResult){
		for(int i = layers.length - 1; i > 0; i--){
			NeuronLayer layer = layers[i];
			for(int j = 0; j < layer.getNeurons().length; j++){
				Neuron neuron = layer.getNeurons()[j];
				double neuronError = 0;
				if(i == layers.length - 1){
					neuronError = neuron.getDerivative() * (result - expectedResult);
				}
				else{
					neuronError = neuron.getDerivative();
					
					double sum = 0;
					for(Dendrite dendrite:neuron.getDendrites()){
						sum += dendrite.weight * dendrite.getEnd().getError();
					}
					neuronError *= sum;
				}
				neuron.setError(neuronError);
			}
		}
	}
	
	private void updateWeights(HashMap<Dendrite,Double> dendriteDeltaMap, double learningRate, double momentum){
		for(int i = layers.length - 1; i > 0; i--){
			NeuronLayer layer = layers[i];
			for(Neuron neuron:layer.getNeurons()){
				for(Dendrite dendrite:neuron.getInputs()){
					double delta = learningRate * neuron.getError() * dendrite.getStart().getOutput();
					if(dendriteDeltaMap.get(dendrite) != null){
						delta += momentum * dendriteDeltaMap.get(dendrite);
					}
					dendriteDeltaMap.put(dendrite, delta);
					dendrite.adjustWeight(-delta);
				}
			}
		}
	}
	
	private void printWeights(){
		for(int i = 0; i < layers.length-1; i++){
			NeuronLayer layer = layers[i];
			System.out.print("Layer " + i + ": ");
			for(Neuron neuron:layer.getNeurons()){
				for(Dendrite dendrite:neuron.getDendrites()){
					System.out.print(dendrite.weight + ",");
				}
				System.out.print("    ");
			}
			System.out.print("Bias: ");
			for(Neuron neuron:layer.getBiasNeurons()){
				for(Dendrite dendrite:neuron.getDendrites()){
					System.out.print(dendrite.weight + ", ");
				}
			}
			System.out.println();
		}
	}
}
