package example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.NeuralNetworkParams;
import com.coolioasjulio.neuralnetwork.Neuron;
import com.coolioasjulio.neuralnetwork.NeuronLayer;
import com.coolioasjulio.neuralnetwork.TrainParams;

public class AutoEncoder {
	public static void main(String[] args){
		NeuralNetworkParams params = new NeuralNetworkParams(new int[] {10, 3, 10});
		NeuralNetwork network = new NeuralNetwork(params);
		double[][] data = readData("F:\\data.dat");
		System.out.println(Arrays.toString(data[0]));
		TrainParams tp = new TrainParams(data, data, 0.02, 0.9, 0.004);
		network.train(tp);
		
		guessAll(network, data);
		
		System.out.println("Writing to file...");
		try (PrintStream out = new PrintStream(new FileOutputStream("F:\\condensed.dat"))){
			for(double[] input:data){
				network.guess(input);
				NeuronLayer layer = network.getLayers()[1];
				Neuron[] neurons = layer.getNeurons();
				double[] condensed = new double[neurons.length];
				for(int i = 0; i < condensed.length; i++){
					condensed[i] = neurons[i].getOutput();
				}
				out.println(maxIndex(input) + "-" + Arrays.toString(condensed));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Finished!");
	}
	
	static void guessAll(NeuralNetwork network, double[][] data){
		int correct = 0;
		for(double[] input:data){
			double[] guess = network.guess(input);
			if(maxIndex(input) == maxIndex(guess)){
				correct++;
			}
		}
		System.out.println("Got " + correct + " out of " + data.length + " correct.");
		double avg = (double)correct/(double)data.length;
		System.out.println(avg * 100 + "%");
	}
	
	static int maxIndex(double[] arr){
		int maxIndex = 0;
		for(int i = 0; i < arr.length; i++){
			if(arr[i] > arr[maxIndex]){
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	static double[][] readData(String path){
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)))){
			List<double[]> data = new ArrayList<>();
			String s;
			while((s = in.readLine()) != null){
				String[] stringArr = s.replaceAll("[\\[\\]]", "").split(", ");
				double[] datum = new double[stringArr.length];
				for(int i = 0; i < stringArr.length; i++){
					datum[i] = Double.parseDouble(stringArr[i]);
				}
				data.add(datum);
			}
			return data.toArray(new double[10][]);
		} catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
}
