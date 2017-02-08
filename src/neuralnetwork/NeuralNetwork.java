package neuralnetwork;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class NeuralNetwork implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1});
		double[][] inputs = new double[][]{
			{0,1},
			{1,0},
			{0,0},
			{1,1}
		};
		double[] output = new double[]{1,1,0,0};
		net.train(inputs, output, 0.25);
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
	public NeuralNetwork(int[] layers){
		this.layers = new NeuronLayer[layers.length];
		for(int i = 0; i < layers.length; i++){
			this.layers[i] = new NeuronLayer(this,layers[i]);
		}
		randomWeights();
	}
	
	public NeuronLayer[] getLayers(){
		return layers;
	}
	
	private void randomWeights(){
		for(int i = 0; i < layers.length-1; i++){
			layers[i].setRandomWeights(layers[i+1]);
		}
	}
	
	public double guess(double[] input){
		for(NeuronLayer layer:layers){
			for(Neuron neuron:layer.getNeurons()){
				neuron.weightedSum = 0;
			}
		}
		return sigmoid(evaluate(input));
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
			System.out.println();
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
	
	void getErrors(double result, double expectedResult){
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
	
	void updateWeights(double learningRate){
		for(int i = layers.length - 1; i > 0; i--){
			NeuronLayer layer = layers[i];
			for(Neuron neuron:layer.getNeurons()){
				for(Dendrite dendrite:neuron.getInputs()){
					double delta = learningRate * neuron.getError() * dendrite.getStart().getOutput();
					dendrite.adjustWeight(-delta);
				}
			}
		}
	}
	
	public void train(double[][] inputs, double[] outputs, double learningRate){
		int runs = 0;
		double startError = 0;
		while(true){
			double errorSum = 0;
			double[][] inputSegment;
			double[] outputSegment;
			if(inputs.length >=1000000){
				inputSegment = new double[100][];
				outputSegment = new double[100];
				HashSet<Integer> indexes = new HashSet<Integer>();
				Random random = new Random();
				int index = 0;
				while(index < inputSegment.length){
					int rand = random.nextInt(inputs.length);
					if(indexes.add(rand)){
						inputSegment[index] = inputs[rand];
						outputSegment[index] = outputs[rand];
						index++;
					}
				}
				System.out.println("Smaller dataset of size: " + inputSegment.length);
			}
			else {
				inputSegment = inputs;
				outputSegment = outputs;
			}
			for(int i = 0; i < inputSegment.length; i++){
				double sum = evaluate(inputSegment[i]);//get sum
				double result = sigmoid(sum); //calculate final result
				double error = Math.pow(outputSegment[i]-result,2)/2; //calculate mean squared error
				errorSum += error;
				//System.out.println("Error: " + error);
				getErrors(result, outputSegment[i]);
				updateWeights(learningRate);
			}
			double avgError = errorSum/inputSegment.length;
			if(runs == 0) startError = avgError;
			System.out.println("Epoch: " + runs + ", error: " + avgError);
			runs++;
			if(runs>=2000000 || avgError <= Math.pow(0.03, 2)/2) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
		printWeights();
	}
}
