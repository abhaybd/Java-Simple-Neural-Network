package firstneuralnet;

public class FirstNeuralNetwork {
	public static void main(String[] args){
		
	}
	
	public static <T> void printArray(T[] array){
		for(int i = 0; i < array.length; i++){
			System.out.print(array[i].toString());
		}
	}
	
	public void learn(Integer[][][] training, double[] weights, double threshold, double learningRate){
		if(weights.length != training[0][0].length){
			System.err.println("Length of weights is not equal to length of data!");
			return;
		}
		while(true){
			int errorCount = 0;
			for(int i = 0; i < training.length; i++){
				for(Integer[] arr:training[i]){
					printArray(arr);
				}
				int weightedSum = 0;
				for(int j = 0; j < training[i][0].length; j++){
					weightedSum += weights[j] * training[i][0][j];
				}
				int result = (weightedSum >= threshold)?1:0;
				System.out.println("Expected result: " + training[i][1][0] + ", Actual result: " + result);
				int error = training[i][1][0] - result;
				if(error != 0) errorCount++;
				
				for(int j = 0; j < training[i][0].length; j++){
					weights[j] += learningRate * error * training[i][0][j];
				}
			}			
		}
	}
}
