package multilayernet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import multilayernet.InputUtils.InvalidInputException;

public class NetworkBuilder {
	public static void main(String[] args){
		NeuralNetwork network = new NeuralNetwork(new int[]{2,3,1});
		try {
			double[][] inputs = InputUtils.getInputFromFile("input.txt");
			double[] output = InputUtils.getOutputFromFile("output.txt");
			network.train(inputs, output, 0.25);
			saveNeuralNetwork(network,"network.net");
			String response = "";
			Scanner input = new Scanner(System.in);
			while(!(response = input.nextLine()).equals("quit")){
				String[] parts = response.split(",");
				double[] nums = new double[parts.length];
				for(int i = 0; i < parts.length; i++){
					nums[i] = Double.parseDouble(parts[i]);
				}
				System.out.println(network.guess(nums));
				
			}
			input.close();
		} catch (FileNotFoundException | InvalidInputException e) {
			e.printStackTrace();
		}
	}
	
	static void saveNeuralNetwork(NeuralNetwork network, String path){
		try{
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(network);
			out.close();
			fileOut.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
