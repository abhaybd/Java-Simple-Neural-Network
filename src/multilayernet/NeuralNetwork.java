package multilayernet;

import java.util.Arrays;
import java.util.Scanner;

public class NeuralNetwork {
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1});
		int[][] inputs = new int[][]{
			{0,1},
			{1,0},
			{1,1},
			{0,0}
		};
		double[] output = new double[]{1,1,0,0};
		net.train(inputs, output, 0.25);
		//net.setWeights();
		String response = "";
		Scanner input = new Scanner(System.in);
		while(!(response = input.nextLine()).equals("quit")){
			String[] parts = response.split(",");
			int[] nums = new int[parts.length];
			for(int i = 0; i < parts.length; i++){
				nums[i] = Integer.parseInt(parts[i]);
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
	}
	
	public NeuronLayer[] getLayers(){
		return layers;
	}
	
	private void randomWeights(){
		for(int i = 0; i < layers.length-1; i++){
			layers[i].setRandomWeights(layers[i+1]);
		}
	}
	
	public double guess(int[] input){
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
	
	private double evaluate(int[] input){
		for(int i = 0; i < layers[0].getNeurons().length; i++){
			layers[0].getNeurons()[i].output = input[i];
		}
		for(int i = 0; i < layers.length; i++){
			for(Neuron neuron:layers[i].getNeurons()){
				if(i != 0) neuron.activationFunction();
				if(i != layers.length - 1){
					for(Dendrite dendrite:neuron.getDendrites()){
						dendrite.getEnd().weightedSum += neuron.output * dendrite.weight;
					}
				}
			}
		}
		double result = layers[layers.length-1].getNeurons()[0].weightedSum;
		//System.out.println(result);
		//printWeights();
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
					//System.out.println("output: " + neuron.getDerivative());
				}
				else{
					neuronError = neuron.getDerivative();
					
					double sum = 0;
					for(Dendrite dendrite:neuron.getDendrites()){
						sum += dendrite.weight * dendrite.getEnd().getError();
						//System.out.println("weight: " + dendrite.weight);
					}
					//System.out.println("sum: " + sum);
					neuronError *= sum;
				}
				neuron.setError(neuronError);
				//System.out.println("Error: " + neuron.getError());
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
					//System.out.println("change: " + delta);
				}
			}
		}
	}
	
	public void train(int[][] inputs, double[] outputs, double learningRate){
		randomWeights();
		//setWeights();
		int runs = 0;
		double startError = 0;
		while(true){
			double errorSum = 0;
			for(int i = 0; i < inputs.length; i++){
				System.out.println(Arrays.toString(inputs[i]));
				double sum = evaluate(inputs[i]);//get sum
				double result = sigmoid(sum); //calculate final result
				double error = Math.pow(outputs[i]-result,2)/2; //calculate mean squared error
				errorSum += error;
				System.out.println("Result: " + result); //print result
				
				getErrors(result, outputs[i]);
				updateWeights(learningRate);
				
				//printWeights(); //print new weights
				
				//reset the neuron values
				for(NeuronLayer layer:layers){
					for(Neuron neuron:layer.getNeurons()){
						neuron.weightedSum = 0;
					}
				}
				
			}
			if(runs == 0) startError = errorSum/4;
			System.out.println("Epoch: " + runs + ", error: " + errorSum/4);
			runs++;
			if(runs>=200000) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
		printWeights();
	}
}
