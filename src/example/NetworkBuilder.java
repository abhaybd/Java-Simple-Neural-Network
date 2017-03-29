package example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import com.coolioasjulio.neuralnetwork.InputUtils;
import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.InputUtils.InvalidInputException;

public class NetworkBuilder {
	public static void main(String[] args){
		NeuralNetwork network = new NeuralNetwork(new int[]{2,3,1}, new int[]{0,0,0}, "Network", 3000, Math.pow(0.03, 2)/2);
		try {
			//get inputs and output from disk
			double[][] inputs = InputUtils.getInputFromFile("data/input.txt");
			double[][] output = InputUtils.getOutputFromFile("data/output.txt");
			network.train(inputs, output, 0.25, 0, 1000000); //train the network
			
			saveNeuralNetwork(network,"data/network.net"); //save the network
			
			//extrapolate the output of data inputed by user
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
	public static final double learningRate = 0.25;
	
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
