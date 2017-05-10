package com.coolioasjulio.neuralnetwork;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;
import com.coolioasjulio.neuralnetwork.activationstrategy.SigmoidActivationStrategy;

public class NeuralNetwork implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		double[][] inputs = new double[][]{
			{0,1},
			{1,0},
			{0,0},
			{1,1}
		};
		double[][] output = new double[][]{{1},{1},{0},{0}};
		NeuralNetwork net = new NeuralNetwork(new int[]{inputs[0].length,2,1}, new int[]{1,1,0},"XOR", 2000, Math.pow(0.03, 2)/2);
		net.train(inputs, output, 0.1, 0.9, 0.0001);
		net.printWeights(System.out);
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
	//private DataVisualizer dv = null;
	private boolean trainedWithSoftMax = false;
	
	/**
	 * Instantiate Neural Network with specified layers and bias.
	 * @param layers int array specifying number of neurons in each layer. {2,2,1} mains 2 inputs, 2 hidden, 1 output
	 * @param bias Structured similarly to above, but for bias neurons. Generally, only 1 bias is needed per layer, because the weights can change.
	 */
	public NeuralNetwork(int[] layers, int[] bias){
		init(layers, bias, ActivationStrategy.fillArray(SigmoidActivationStrategy.class, layers.length));
	}
	
	public NeuralNetwork(NeuralNetworkParams params){
		init(params.layers, params.bias, params.strategies);
	}
	
	/**
	 * Instantiate Neural Network with specified layers and bias, and a visualizer window.
	 * @param layers int array specifying number of neurons in each layer. {2,2,1} mains 2 inputs, 2 hidden, 1 output
	 * @param bias Structured similarly to above, but for bias neurons. Generally, only 1 bias is needed per layer, because the weights can change.
	 * @param title Title for visualizer window
	 * @param scale Number to scale the values by on the window. (aesthetics only)
	 * @param threshold Threshold to display on the window.
	 */
	public NeuralNetwork(int[] layers, int[] bias, String title, float scale, double threshold){
		init(layers, bias, ActivationStrategy.fillArray(SigmoidActivationStrategy.class, layers.length));
		//dv = new DataVisualizer(title,scale,threshold);
	}
	
	static class NeuralNetworkParams{ 
		public int[] layers, bias;
		public ActivationStrategy[] strategies;
	}
	
	private void init(int[] layers, int[] bias, ActivationStrategy[] strategies){
		if(bias == null || bias.length != layers.length){
			bias = new int[layers.length];
		}
		this.layers = new NeuronLayer[layers.length];
		for(int i = 0; i < layers.length; i++){
			this.layers[i] = new NeuronLayer(this,layers[i],bias[i], strategies[i]);
		}
		randomWeights();
	}
	
	/**
	 * Instatiate neural network with neuron layers.
	 * @param layers Neuron layers
	 */
	public NeuralNetwork(NeuronLayer[] layers){
		this.layers = layers;
	}
	
	public boolean isClassification(){
		return trainedWithSoftMax;
	}
	
	public NeuronLayer[] getLayers(){
		return layers;
	}
	
	/**
	 * Evaluate output from supplied input
	 * @param input to use
	 * @return output gotten from input
	 */
	public double[] guess(double[] input){
		return evaluate(input);
	}
	
	/**
	 * Evaluate output from supplied input using softmax. This scales the output neurons so the sum of all the outputs is 1.
	 * @param input Input to use
	 * @param softMax Whether to use softmax or not.
	 * @return Output gotten from input
	 */
	public double[] guess(double[] input, boolean softMax){
		return evaluate(input, softMax);
	}
	
	/**
	 * Train neural network with supplied parameters
	 * @param inputs Inputs to train with
	 * @param outputs Outputs corresponding to inputs
	 * @param learningRate Learning rate
	 * @param momentum Momentum
	 * @param maxIterations Maximum iterations of training to run.
	 */
	public void train(double[][] inputs, double[][] outputs, double learningRate, double momentum, double errorThreshold){
		train(inputs, outputs, learningRate, momentum, errorThreshold, false, inputs.length);
	}
	
	/**
	 * Train neural network with supplied parameters
	 * @param inputs Inputs to train with
	 * @param outputs Outputs corresponding to inputs
	 * @param learningRate Learning rate
	 * @param momentum Momentum
	 * @param maxIterations Maximum iterations of training to run.
	 * @param classification use softmax?
	 * @param batch size of batch to use for Stochastic Gradient Descent
	 */
	public void train(double[][] allInputs, double[][] allOutputs, double learningRate, double momentum, double errorThreshold, boolean classification, int batch){
		trainedWithSoftMax = classification;
		int runs = 0;
		double startError = 0;
		stopButton();
		double lastError = -1;
		double runningError = 0;
		while(true){
			Data data = getBatch(allInputs, allOutputs, batch);
			double[][] inputs = data.input;
			double[][] outputs = data.output;
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			for(int i = 0; i < inputs.length; i++){
				double[] results = evaluate(inputs[i], classification);
				double error = calculateAggregateError(results,outputs[i]); //calculate mean squared error
				errorSum += error;
				getErrors(results, outputs[i]);
				updateWeights(dendriteDeltaMap, learningRate, momentum);
			}
			double batchError = errorSum/inputs.length;
			runningError += batchError;
			if(runs == 0) startError = batchError;
			//if(dv != null)dv.addError((float)avgError);
			if(runs % 25 == 0){
				lastError = runningError/25d;
				runningError = 0;
			}
			System.out.println("Epoch: " + runs + ", error: " + batchError + ", avgError: " + lastError);
			runs++;
			if(lastError <= errorThreshold && runs > 100 || stop) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
	}
	
	/**
	 * Train neural network with supplied parameters
	 * @param inputs Inputs to train with
	 * @param outputs Outputs corresponding to inputs
	 * @param learningRate Learning rate
	 * @param momentum Momentum
	 * @param maxTime max number of milliseconds to train for
	 */
	public void trainForMilliseconds(double[][] inputs, double[][] outputs, double learningRate, double momentum, long maxTime){
		trainForMilliSeconds(inputs, outputs, learningRate, momentum, maxTime, false);
	}
	
	boolean stop = false;
	
	/**
	 * Train neural network with supplied parameters
	 * @param inputs Inputs to train with
	 * @param outputs Outputs corresponding to inputs
	 * @param learningRate Learning rate
	 * @param momentum Momentum
	 * @param maxTime max number of milliseconds to train for
	 * @param classification use softmax?
	 */
	public void trainForMilliSeconds(double[][] inputs, double[][] outputs, double learningRate, double momentum, long maxTime, boolean classification){
		int runs = 0;
		double startError = 0;
		long startTime = System.currentTimeMillis();
		boolean done = false;
		stopButton();
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
			//if(dv != null)dv.addError((float)avgError);
			System.out.println("Epoch: " + runs + ", error: " + avgError);
			runs++;
			done = Math.abs(System.currentTimeMillis() - startTime) >= maxTime || avgError <= Math.pow(0.03, 2)/2 || stop;
		}
		System.out.println("\nFinished after running for " + Math.abs(System.currentTimeMillis() - startTime)/1000 + " seconds!");
		System.out.println("Start error: " + startError);
		//printWeights();
	}
	
	private void stopButton(){
		JFrame frame = new JFrame();
		JButton button = new JButton("Stop");
		JButton weights = new JButton("Print weights");
		button.addActionListener(e -> {
			stop = true;
		});
		weights.addActionListener(e -> {
			try {
				PrintStream out = new PrintStream(new FileOutputStream("weights.log"));
				printWeights(out);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		frame.getContentPane().add(button);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Write neural network to disk. NOT WORKING
	 * @param path path to write to.
	 */
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
	
	static class Data { public double[][] input, output; }
	
	private Data getBatch(double[][] inputs, double[][] outputs, int batch){
		Data data = new Data();
		batch = Math.min(inputs.length, batch);
		if(batch == inputs.length){
			data.input = inputs;
			data.output = outputs;
			return data;
		}
		double[][] dataInput = new double[batch][];
		double[][] dataOutput = new double[batch][];
		Random random = new Random();
		Set<Integer> picked = new HashSet<Integer>();
		int currentIndex = 0;
		for(int i = 0; i < batch; i++){
			boolean done = false;
			while(!done){
				int index = random.nextInt(inputs.length);
				done = picked.add(index);
				if(done){
					dataInput[currentIndex] = inputs[index];
					dataOutput[currentIndex] = outputs[index];
					currentIndex++;
				}				
			}
		}
		data.input = dataInput;
		data.output = dataOutput;
		return data;
	}
	
	private double calculateAggregateError(double[] results, double[] expectedResults){
		double error = 0;
		for(int i = 0; i < results.length; i++){
			error += Math.pow(expectedResults[i] - results[i], 2);
		}
		return error/2;
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
					dendriteDeltaMap.put(dendrite, -delta);
					dendrite.adjustWeight(-delta);
				}
			}
		}
	}
	
	
	/**
	 * Print all the weights to the supplied printstream.
	 * @param out PrintStream to print out to.
	 */
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
