package multilayernet;

import java.util.Arrays;
import java.util.Scanner;

public class NeuralNetwork {
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1});
		int[][] inputs = new int[][]{
			{1,1},
			{0,1}
		};
		double[] output = new double[]{1,0};
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
			this.layers[i] = new NeuronLayer(layers[i]);
		}
	}
	
	private void randomWeights(){
		for(int i = 0; i < layers.length-1; i++){
			layers[i].setRandomWeights(layers[i+1]);
		}
	}
	
	public double guess(int[] input){
		for(NeuronLayer layer:layers){
			for(Neuron neuron:layer.getNeurons()){
				neuron.tempSum = 0;
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
			layers[0].getNeurons()[i].tempSum = input[i];
		}
		for(int i = 0; i < layers.length-1; i++){
			for(Neuron neuron:layers[i].getNeurons()){
				if(i != 0) neuron.activationFunction();
				for(Dendrite dendrite:neuron.getDendrites()){
					dendrite.getEnd().tempSum += neuron.tempSum * dendrite.weight;
				}
			}
		}
		double result = layers[layers.length-1].getNeurons()[0].tempSum;
		//System.out.println(result);
		//printWeights();
		return result;
	}
	
	private double sigmoid(double x){
		return 1/(1+Math.pow(Math.E, -x));
	}
	
	private double sigmoidPrime(double x){
		return sigmoid(x)*(1-sigmoid(x));
	}
	
	void setWeights(){
		randomWeights();
		double[][] weights = new double[][]{
			{0.62,0.42,0.55,-0.17},
			{0.35,0.81}
		};
		
		for(int i = 0; i < layers.length-1; i++){
			int index = 0;
			for(int j = 0; j < layers[i].getNeurons().length; j++){
				for(Dendrite d:layers[i].getNeurons()[j].getDendrites()){
					d.setWeight(weights[i][index]);
					index++;
				}
			}
		}
	}
	
	public void train(int[][] inputs, double[] outputs, double learningRate){
		//randomWeights();
		setWeights();
		int runs = 0;
		while(true){
			int errorCount = 0;
			for(int i = 0; i < inputs.length; i++){
				System.out.println(Arrays.toString(inputs[i]));
				double sum = evaluate(inputs[i]);//get sum
				double result = sigmoid(sum);
				double error = outputs[i]-result;
				
				System.out.println("Result: " + result);
				
				if(Math.abs(error) > 0.5) errorCount++;
				
				for(Neuron neuron:layers[1].getNeurons()){
					for(Dendrite dendrite:neuron.getDendrites()){
						dendrite.adjustWeight(learningRate * error * neuron.tempSum * result * (1-result));
					}
				}
				
				for(Neuron neuron:layers[0].getNeurons()){
					for(Dendrite dendrite:neuron.getDendrites()){
						dendrite.adjustWeight(learningRate * error * neuron.tempSum * dendrite.getEnd().tempSum * (1-dendrite.getEnd().tempSum));
					}
				}
				
				printWeights();
				System.out.println("-------------------------------");
				for(NeuronLayer layer:layers){
					for(Neuron neuron:layer.getNeurons()){
						neuron.tempSum = 0;
					}
				}
				
			}
			runs++;
			if(errorCount == 0||runs>=200000) break;
			//break;
		}
		System.out.println("\nFinished!");
		printWeights();
	}
}
