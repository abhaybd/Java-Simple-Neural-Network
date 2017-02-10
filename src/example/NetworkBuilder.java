package example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import neuralnetwork.InputUtils;
import neuralnetwork.NeuralNetwork;
import neuralnetwork.InputUtils.InvalidInputException;

public class NetworkBuilder {
	public static void main(String[] args){
		NeuralNetwork network = new NeuralNetwork(new int[]{2,3,1});
		try {
			//get inputs and output from disk
			double[][] inputs = InputUtils.getInputFromFile("data/input.txt");
			double[] output = InputUtils.getOutputFromFile("data/output.txt");
			network.train(inputs, output, 0.1, 0.9, 1000000); //train the network
			
			saveNeuralNetwork(network,"data/network.net"); //save the network
			
			//extrapolate the output of data inputted by user
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
