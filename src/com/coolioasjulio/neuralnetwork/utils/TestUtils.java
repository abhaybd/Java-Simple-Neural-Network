package com.coolioasjulio.neuralnetwork.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class TestUtils {
	
	private static int maxIndex(double[] arr){
		int maxIndex = 0;
		double max = arr[0];
		for(int i = 0; i < arr.length; i++){
			if(arr[i] > max){
				max = arr[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public static void guessAll(NeuralNetwork network, String imagePath, String labelPath, int size){
		try{
			BufferedImage[] images = ImageUtils.readImages(imagePath);
			double[][] output = ImageUtils.readLabels(labelPath, size);
			guessAll(network,images,output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void guessAll(NeuralNetwork network, BufferedImage[] images, double[][] output){
		guessAll(network, images, output, System.out);
	}
	
	public static void guessAll(NeuralNetwork network, BufferedImage[] images, double[][] output, PrintStream out){
		int correct = 0;
		out.println("Starting");
		HashMap<Integer,Integer> record = new HashMap<>();
		for(int i = 0; i < images.length; i++){
			double[] guess = network.guess(ImageUtils.getDataFromBufferedImage(images[i]));
			double[] label = output[i];
			int correctIndex = maxIndex(label);
			if(maxIndex(guess) == correctIndex){
				correct++;
			}
			else{
				record.putIfAbsent(correctIndex, 0);
				record.put(correctIndex,record.get(correctIndex)+1);
			}
		}
		out.println("Got " + correct + " out of " + images.length);
		out.println(100d*(double)correct/(double)images.length + "%");
		out.println(record.toString());	
	}
}
