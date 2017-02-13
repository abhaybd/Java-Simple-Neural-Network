package neuralnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class NeuralNetwork implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static void readFromDisk(String path){
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
			
		}
		
		try(DataInputStream in = new DataInputStream(new FileInputStream(file))){
			int numLayers = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1}, new int[]{1,1,0});
		double[][] inputs = new double[][]{
			{0,1},
			{1,0},
			{0,0},
			{1,1}
		};
		double[][] output = new double[][]{{1},{1},{0},{0}};
		net.train(inputs, output, 0.1, 0.9, 100000);
		String response = "";
		Scanner input = new Scanner(System.in);
		while(!(response = input.nextLine()).equals("quit")){
			String[] parts = response.split(",");
			double[] nums = new double[parts.length];
			for(int i = 0; i < parts.length; i++){
				nums[i] = Double.parseDouble(parts[i]);
			}
			System.out.println(Arrays.toString(net.guess(nums)));
			
		}
		input.close();
	}
	
	protected NeuronLayer[] layers;
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
	
	public double[] guess(double[] input){
		return evaluate(input);
	}
	
	public void train(double[][] inputs, double[][] outputs, double learningRate, double momentum, int maxIterations){
		int runs = 0;
		double startError = 0;
		while(true){
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			//if(runs>=maxIterations)break;
			for(int i = 0; i < inputs.length; i++){
				double[] results = evaluate(inputs[i]);
				double error = calculateAggregateError(results,outputs[i]); //calculate mean squared error
				errorSum += error;
				//System.out.println("Error: " + error);
				getErrors(results, outputs[i]);
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
	
	public void writeToDisk(String path){
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(path)))) {
			out.writeInt(layers.length);
			for(NeuronLayer layer:layers){
				out.writeInt(layer.getNeurons().length);
				out.writeInt(layer.getBiasNeurons().length);
			}
			for(NeuronLayer nl:layers){
				out.writeInt(nl.getNeurons().length);
				for(Neuron n:nl.getNeurons()){
					out.writeInt(n.getDendrites().length);
					for(Dendrite d:n.getDendrites()){
						out.writeDouble(d.getWeight());
					}
				}
				out.writeInt(nl.getBiasNeurons().length);
				for(Neuron n:nl.getBiasNeurons()){
					out.writeInt(n.getDendrites().length);
					for(Dendrite d:n.getDendrites()){
						out.writeDouble(d.getWeight());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double calculateAggregateError(double[] results, double[] expectedResults){
		double error = 0;
		for(int i = 0; i < results.length; i++){
			error += Math.pow(expectedResults[i] - results[i], 2)/2;
		}
		return error;
	}

	private double[] evaluate(double[] input){
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
			for(Neuron bias:layers[i].getBiasNeurons()){
				for(Dendrite dendrite:bias.getDendrites()){
					dendrite.getEnd().weightedSum += bias.output * dendrite.weight;
				}
			}
		}
		
		//get results
		double[] results = new double[layers[layers.length-1].getNeurons().length];
		for(int i = 0; i < results.length; i++){
			results[i] = layers[layers.length-1].getNeurons()[i].getOutput();
		}
		return results;
	}
	
	private void randomWeights(){
		for(int i = 0; i < layers.length-1; i++){
			layers[i].setRandomWeights(layers[i+1]);
		}
	}
	
	double sigmoid(double x){
		return 1/(1+Math.pow(Math.E, -x));
	}
	
	private void getErrors(double[] results, double[] expectedResults){
		for(int i = layers.length - 1; i > 0; i--){
			NeuronLayer layer = layers[i];
			ArrayList<Neuron> neurons = new ArrayList<>();
			for(Neuron n:layer.getNeurons()){
				neurons.add(n);
			}
			if(layer.getBiasNeurons() != null && layer.getBiasNeurons().length > 0){
				for(Neuron n:layer.getBiasNeurons()){
					neurons.add(n);
				}
			}
			for(int j = 0; j < neurons.size(); j++){
				Neuron neuron = neurons.get(j);
				double neuronError = 0;
				if(i == layers.length - 1){
					neuronError = neuron.getDerivative() * (results[j] - expectedResults[j]);
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
