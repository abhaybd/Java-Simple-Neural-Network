package firstneuralnet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

public class FirstNeuralNetwork {
	public static void main(String[] args){
		FirstNeuralNetwork net = new FirstNeuralNetwork();
		Integer[][][] training = new Integer[][][]{
			{{1,0,1},{0}},
			{{1,0,0},{1}},
			{{1,1,1},{0}},
			{{1,1,0},{0}}
		};
		BigDecimal[] weights = new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
		net.learn(training, weights, 1, new BigDecimal("0.1"));
		String response = "";
		Scanner input = new Scanner(System.in);
		while(!(response = input.nextLine()).equals("quit")){
			String[] parts = response.split(",");
			Integer[] nums = new Integer[parts.length];
			for(int i = 0; i < parts.length; i++){
				nums[i] = Integer.parseInt(parts[i]);
			}
			System.out.println(net.guess(nums));
			
		}
		input.close();
	}
	
	private BigDecimal[] weights = null;
	private double threshold;
	
	public static <T> void printArray(T[] array){
		for(int i = 0; i < array.length; i++){
			System.out.print(array[i].toString() + ((i == array.length-1)?"":","));
		}
	}
	
	public int guess(Integer[] input){
		if(weights == null){
			System.out.println("Network hasn't been trained yet!");
			return -1;
		}
		BigDecimal weightedSum = new BigDecimal("0");
		for(int j = 0; j < input.length; j++){
			weightedSum = weightedSum.add(weights[j].multiply(new BigDecimal(input[j])));
		}
		if(weightedSum.doubleValue() >= threshold) return 1;
		return 0;
	}
	
	public void learn(Integer[][][] training, BigDecimal[] weights, double threshold, BigDecimal learningRate){
		if(weights.length != training[0][0].length){
			System.err.println("Length of weights is not equal to length of data!");
			return;
		}
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
				BigDecimal weightedSum = new BigDecimal("0");
				for(int j = 0; j < training[i][0].length; j++){
					weightedSum = weightedSum.add(weights[j].multiply(new BigDecimal(training[i][0][j])));
				}
				//System.out.println("Weighted sum: " + weightedSum);
				
				//get result of neuron
				int result = (weightedSum.doubleValue() >= threshold)?1:0;
				
				//print out result
				System.out.println("Expected result: " + training[i][1][0] + ", Actual result: " + result);
				
				//calculate and increment error
				int error = training[i][1][0] - result;
				if(error != 0) errorCount++;
				
				//adjust weights
				for(int j = 0; j < training[i][0].length; j++){
					weights[j] = weights[j].add(learningRate.multiply(new BigDecimal(error * training[i][0][j])));
				}
			}
			if(errorCount == 0) break;
		}
		System.out.println("Final weights: " + Arrays.toString(weights));
		this.weights = weights;
		this.threshold = threshold;
	}
}
