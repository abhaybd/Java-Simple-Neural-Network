package com.coolioasjulio.neuralnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BoxLayout;
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
		NeuralNetwork net = new NeuralNetwork(new int[]{inputs[0].length,2,1}, new int[]{1,1,0}, "XOR");
		TrainParams tp = new TrainParams(inputs, output, 0.2, 0.9, 0.0001);
		net.train(tp);
		//NeuralNetwork net = NeuralNetwork.loadFromDisk("XOR.net");
		net.printWeights(System.out);
		try {
			net.writeToDisk("XOR.net");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public static NeuralNetwork loadFromDisk(String path){
		try(DataInputStream in = new DataInputStream(new FileInputStream(path))){
			int numLayers = in.readInt();
			int[] layers = new int[numLayers];
			int[] biasLayers = new int[numLayers];
			for(int i = 0; i < numLayers; i++){
				layers[i] = in.readInt();
				biasLayers[i] = in.readInt();
			}
			NeuralNetwork network = new NeuralNetwork(layers, biasLayers);
			for(int i = 0; i < numLayers; i++){
				for(Neuron n:network.getLayers()[i].getNeurons()){
					if(in.readInt() != n.dendrites.length) throw new IOException("Invalid dendrite length!");
					for(Dendrite d:n.dendrites){
						d.weight = in.readDouble();
					}
				}
				for(Neuron n:network.getLayers()[i].getBiasNeurons()){
					if(in.readInt() != n.dendrites.length) throw new IOException("Invalid dendrite length!");
					for(Dendrite d:n.dendrites){
						d.weight = in.readDouble();
					}
				}
			}
			return network;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected NeuronLayer[] layers;
	private DataVisualizer dv = null;
	private boolean trainedWithSoftMax = false;
	
	/**
	 * Instantiate Neural Network with specified layers and bias.
	 * @param layers int array specifying number of neurons in each layer. {2,2,1} mains 2 inputs, 2 hidden, 1 output
	 * @param bias Structured similarly to above, but for bias neurons. Generally, only 1 bias is needed per layer, because the weights can change.
	 */
	public NeuralNetwork(int[] layers, int[] bias){
		NeuralNetworkParams params = new NeuralNetworkParams(layers);
		params.bias = bias;
		init(params);
	}
	
	public NeuralNetwork(NeuralNetworkParams params){
		init(params);
	}
	
	/**
	 * Instantiate Neural Network with specified layers and bias, and a visualizer window.
	 * @param layers int array specifying number of neurons in each layer. {2,2,1} mains 2 inputs, 2 hidden, 1 output
	 * @param bias Structured similarly to above, but for bias neurons. Generally, only 1 bias is needed per layer, because the weights can change.
	 * @param title Title for visualizer window
	 * @param scale Number to scale the values by on the window. (aesthetics only)
	 * @param threshold Threshold to display on the window.
	 */
	public NeuralNetwork(int[] layers, int[] bias, String title){
		NeuralNetworkParams params = new NeuralNetworkParams(layers);
		params.bias = bias;
		params.title = title;
		init(params);
	}
	
	private void init(NeuralNetworkParams params){
		if(params.title != null) dv = new DataVisualizer(params.title);
		if(params.bias == null || params.bias.length != params.getLayers().length){
			params.bias = new int[params.getLayers().length];
		}
		if(params.strategies == null){
			params.strategies = ActivationStrategy.fillArray(SigmoidActivationStrategy.class, params.getLayers().length);
		}
		this.layers = new NeuronLayer[params.getLayers().length];
		for(int i = 0; i < layers.length; i++){
			this.layers[i] = new NeuronLayer(params.getLayers()[i],params.bias[i], params.strategies[i]);
		}
		
		new File("results").mkdir();
		randomWeights();
	}
	
	/**
	 * Instantiate neural network with neuron layers.
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
	
	public void activateVisualizer(String title){
		if(dv == null) dv = new DataVisualizer(title);
	}
	
	/**
	 * Train neural network with supplied paramaters
	 * @param params Params object containing all the parameters, including inputs, outputs, learningRate, momentum, classification, batchSize, and numThreads.
	 */
	public void train(TrainParams params){
		trainedWithSoftMax = params.classification;
		int runs = 0;
		double startError = 0;
		stopButton();
		Queue<Double> prevErrors = new LinkedList<>();
		while(true){
			Data data = getBatch(params.inputs, params.outputs, params.batchSize);
			double[][] inputs = data.input;
			double[][] outputs = data.output;
			HashMap<Dendrite,Double> dendriteDeltaMap = new HashMap<>();
			double errorSum = 0;
			for(int i = 0; i < inputs.length; i++){
				double[] results = evaluate(inputs[i], params.classification);
				double error = calculateAggregateError(results,outputs[i]); //calculate mean squared error
				errorSum += error;
				getErrors(results, outputs[i]);
				updateWeights(dendriteDeltaMap, params.learningRate, params.momentum);
			}
			double batchError = errorSum/inputs.length;
			prevErrors.add(batchError);
			if(runs == 0) startError = batchError;
			while(prevErrors.size() > 25){
				prevErrors.remove();
			}
			double error = -1;
			error = getAvgError(prevErrors);
			if(dv != null)dv.addError(error, params.errorThreshold);
			System.out.println("Epoch: " + runs + ", error: " + batchError + ", avgError: " + error);
			runs++;
			if(error <= params.errorThreshold && runs > 100 || stop) break;
		}
		System.out.println("\nFinished!");
		System.out.println("Start error: " + startError);
	}
	
	private double getAvgError(Queue<Double> q){
		double sum = 0;
		for(double d:q){
			sum += d;
		}
		return sum/q.size();
	}
	
	boolean stop = false;
	
	private void stopButton(){
		JFrame frame = new JFrame();
		JButton button = new JButton("Stop");
		JButton save = new JButton("Save network");
		button.addActionListener(e -> {
			stop = true;
		});
		save.addActionListener(e -> {
			try {
				writeToDisk("results/recognizer" + getCalendarAsString(Calendar.getInstance()) + ".net");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.add(button);
		frame.add(save);
		frame.pack();
		frame.setVisible(true);
	}
	
	private String getCalendarAsString(Calendar c){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_hh-mm");
		return sdf.format(c.getTime());
	}
	
	/**
	 * Write neural network to disk. NOT WORKING
	 * @param path path to write to.
	 */
	public void writeToDisk(String path) throws IOException {
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(path)))) {
			out.writeInt(layers.length);
			for(NeuronLayer nl:layers){
				out.writeInt(nl.getNeurons().length);
				out.writeInt(nl.getBiasNeurons().length);
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
			throw e;
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
		return error/results.length;
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
					if(dendriteDeltaMap.containsKey(dendrite)){
						delta += momentum * dendriteDeltaMap.get(dendrite);
					}
					dendriteDeltaMap.put(dendrite, delta);
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
