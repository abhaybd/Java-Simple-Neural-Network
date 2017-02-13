package neuralnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InputUtils {
	public static double[][] getInputFromFile(String path) throws FileNotFoundException, InvalidInputException{
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
			System.err.println("Invalid file!");
			throw new FileNotFoundException();
		}
		
		try(Scanner in = new Scanner(file)){
			ArrayList<double[]> inputList = new ArrayList<double[]>();
			while(in.hasNextLine()){
				String[] parts = in.nextLine().split(",");
				double[] inputs = new double[parts.length];
				for(int i = 0; i < parts.length; i++){
					if(isDouble(parts[i])){
						inputs[i] = Double.parseDouble(parts[i]);
					}
					else throw new InvalidInputException();
				}
				inputList.add(inputs);
			}
			return inputList.toArray(new double[0][0]);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static double[][] getOutputFromFile(String path) throws FileNotFoundException, InvalidInputException{
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
			System.err.println("Invalid file!");
			throw new FileNotFoundException();
		}
		
		try(Scanner in = new Scanner(file)){
			ArrayList<double[]> outputList = new ArrayList<double[]>();
			while(in.hasNextLine()){
				String[] parts = in.nextLine().split(",");
				double[] inputs = new double[parts.length];
				for(int i = 0; i < parts.length; i++){
					if(isDouble(parts[i])){
						inputs[i] = Double.parseDouble(parts[i]);
					}
					else throw new InvalidInputException();
				}
				outputList.add(inputs);
			}
			return outputList.toArray(new double[0][0]);
		} catch (FileNotFoundException e) {
			throw e;
		}
	}
	
	public static boolean isDouble(String s){
		try{
			Double.parseDouble(s);
			return true;
		}
		catch(NumberFormatException e){
			return false;
		}
	}
	
	public static class InvalidInputException extends Exception{
		private static final long serialVersionUID = 1L;
		
	}
}
