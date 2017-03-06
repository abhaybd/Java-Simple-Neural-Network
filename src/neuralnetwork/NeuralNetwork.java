package neuralnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class NeuralNetwork implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static NeuralNetwork readFromDisk(String path){
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
			return null;
		}
		
		try(DataInputStream in = new DataInputStream(new FileInputStream(file))){
			int numLayers = in.readInt();
			NeuronLayer[] layers = new NeuronLayer[numLayers];
			for(int a= 0; a < numLayers; a++){
				layers[a] = new NeuronLayer(null,in.readInt(),in.readInt());
			}
			for(int i = 0; i < layers.length-1; i++){
				NeuronLayer layer = layers[i];
				for(int j = 0; j < layer.getNeurons().length; j++){
					Dendrite[] dendrites = new Dendrite[in.readInt()];
					for(int k = 0; k < layers[i+1].getNeurons().length; k++){
						dendrites[k] = new Dendrite(layer.getNeurons()[j],layers[i+1].getNeurons()[k],in.readDouble());
					}
				}
				for(int j = 0; j < layer.getBiasNeurons().length; j++){
					Dendrite[] dendrites = new Dendrite[in.readInt()];
					for(int k = 0; k < layers[i+1].getNeurons().length; k++){
						dendrites[k] = new Dendrite(layer.getBiasNeurons()[j],layers[i+1].getNeurons()[k],in.readDouble());
					}
				}
			}
			return new NeuralNetwork(layers);
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args){
		NeuralNetwork net = new NeuralNetwork(new int[]{2,2,1}, new int[]{1,1,0},"XOR", 4500, Math.pow(0.03, 2)/2);
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
	private DataVisualizer dv = null;
	public NeuralNetwork(int[] layers, int[] bias){
		init(layers,bias);
	}
	
	public NeuralNetwork(int[] layers, int[] bias, String title, float scale, double threshold){
		init(layers, bias);
		dv = new DataVisualizer(title,scale,threshold);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void init(int[] layers, int[] bias){
		if(bias == null || bias.length != layers.length){
			bias = new int[layers.length];
		}
		this.layers = new NeuronLayer[layers.length];
		for(int i = 0; i < layers.length; i++){
			this.layers[i] = new NeuronLayer(this,layers[i],bias[i]);
		}
		randomWeights();
	}
	
	public NeuralNetwork(NeuronLayer[] layers){
		this.layers = layers;
	}
	
	public NeuronLayer[] getLayers(){
		return layers;
	}
	
	public double[] guess(double[] input){
		return evaluate(input);
	}
	
	public double[] guess(double[] input, boolean softMax){
		return evaluate(input, softMax);
	}
	
	public void train(double[][] inputs, double[][] outputs, double learningRate, double momentum, int maxIterations){
		train(inputs, outputs, learningRate, momentum, maxIterations, false);
	}
	
	public void train(double[][] inputs, double[][] outputs, double learningRate, double momentum, int maxIterations, boolean classification){
		int runs = 0;
		double startError = 0;
		while(true){
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			for(int i = 0; i < inputs.length; i++){
				double[] results = evaluate(inputs[i], classification);
				double error = calculateAggregateError(results,outputs[i]); //calculate mean squared error
				errorSum += error;
				getErrors(results, outputs[i]);
				updateWeights(dendriteDeltaMap, learningRate, momentum);
			}
			double avgError = errorSum/inputs.length;
			if(runs == 0) startError = avgError;
			if(dv != null)dv.addError((float)avgError);
			System.out.println("Epoch: " + runs + ", error: " + avgError);
			runs++;
			if(runs>=maxIterations || avgError <= Math.pow(0.03, 2)/2) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
		//printWeights();
	}
	
	public void trainForMilliseconds(double[][] inputs, double[][] outputs, double learningRate, double momentum, long maxTime){
		trainForMilliSeconds(inputs, outputs, learningRate, momentum, maxTime, false);
	}
	
	public void trainForMilliSeconds(double[][] inputs, double[][] outputs, double learningRate, double momentum, long maxTime, boolean classification){
		int runs = 0;
		double startError = 0;
		long startTime = System.currentTimeMillis();
		boolean done = false;
		while(!done){
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			for(int i = 0; i < inputs.length; i++){
				double[] results = evaluate(inputs[i], classification);
				double error = calculateAggregateError(results,outputs[i]); //calculate mean squared error
				errorSum += error;
				getErrors(results, outputs[i]);
				updateWeights(dendriteDeltaMap, learningRate, momentum);
			}
			double avgError = errorSum/inputs.length;
			if(runs == 0) startError = avgError;
			if(dv != null)dv.addError((float)avgError);
			System.out.println("Epoch: " + runs + ", error: " + avgError);
			runs++;
			done = Math.abs(System.currentTimeMillis() - startTime) >= maxTime || avgError <= Math.pow(0.03, 2)/2;
		}
		System.out.println("\nFinished after running for " + Math.abs(System.currentTimeMillis() - startTime)/1000 + " seconds!");
		System.out.println("Start error: " + startError);
		//printWeights();
	}
	
	public void writeToDisk(String path){
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(path)))) {
			out.writeInt(layers.length);
			for(NeuronLayer layer:layers){
				out.writeInt(layer.getNeurons().length);
				out.writeInt(layer.getBiasNeurons().length);
			}
			for(NeuronLayer nl:layers){
				for(Neuron n:nl.getNeurons()){
					out.writeInt(n.getDendrites().length);
					for(Dendrite d:n.getDendrites()){
						out.writeDouble(d.getWeight());
					}
				}
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
		return evaluate(input,false);
	}

	private double[] evaluate(double[] input, boolean softMax){
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
		if(softMax){
			return layers[layers.length-1].softMax();
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
						//System.out.println("momentum!");
					}
					dendriteDeltaMap.put(dendrite, delta);
					dendrite.adjustWeight(-delta);
				}
			}
		}
	}
	
	public void printWeights(java.io.PrintStream out){
		for(int i = 0; i < layers.length-1; i++){
			NeuronLayer layer = layers[i];
			out.print("Layer " + i + ": ");
			for(Neuron neuron:layer.getNeurons()){
				for(Dendrite dendrite:neuron.getDendrites()){
					out.print(dendrite.weight + ",");
				}
				out.print("    ");
			}
			out.print("Bias: ");
			for(Neuron neuron:layer.getBiasNeurons()){
				for(Dendrite dendrite:neuron.getDendrites()){
					out.print(dendrite.weight + ", ");
				}
			}
			out.println();
		}
	}
}
