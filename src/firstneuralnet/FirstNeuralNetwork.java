package firstneuralnet;

import java.math.BigDecimal;
import java.util.Arrays;

public class FirstNeuralNetwork {
	public static void main(String[] args){
		FirstNeuralNetwork net = new FirstNeuralNetwork();
		Integer[][][] training = new Integer[][][]{
			{{1,0,1},{1}},
			{{1,1,0},{1}},
			{{1,1,1},{0}},
			{{1,0,0},{0}}
		};
		double[] weights = new double[]{0,0,0};
		net.learn(training, weights, 1, 0.1);
	}
	
	public static <T> void printArray(T[] array){
		for(int i = 0; i < array.length; i++){
			System.out.print(array[i].toString() + ((i == array.length-1)?"":","));
		}
	}
	
	public void learn(Integer[][][] training, double[] weights, double threshold, double learningRate){
		if(weights.length != training[0][0].length){
			System.err.println("Length of weights is not equal to length of data!");
			return;
		}
		int runs = 0;
		while(true){
			int errorCount = 0;
			for(int i = 0; i < training.length; i++){
				System.out.println("Weights: " + Arrays.toString(weights));
				
				//print out data line
				for(Integer[] arr:training[i]){
					printArray(arr);
					System.out.print("    ");
				}
				System.out.println();
				
				//calculate the weighted sum
				BigDecimal weightedSum = new BigDecimal(0);
				for(int j = 0; j < training[i][0].length; j++){
					weightedSum.add(new BigDecimal(weights[j] * (double)training[i][0][j]));
					System.out.println("Data: " + training[i][0][j]);
				}
				System.out.println("Weighted sum: " + weightedSum);
				
				//get result of neuron
				int result = (weightedSum.doubleValue() >= threshold)?1:0;
				
				//print out result
				System.out.println("Expected result: " + training[i][1][0] + ", Actual result: " + result);
				
				//calculate and increment error
				int error = training[i][1][0] - result;
				if(error != 0) errorCount++;
				
				//adjust weights
				for(int j = 0; j < training[i][0].length; j++){
					weights[j] += learningRate * error * training[i][0][j];
				}
			}
			runs++;
			if(errorCount == 0||runs>=10) break;
		}
		System.out.println("Final weights: " + Arrays.toString(weights));
	}
}
