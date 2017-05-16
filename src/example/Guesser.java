package example;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class Guesser {
	
	public static void main(String[] args){
		try{
			BufferedImage[] images = ImageUtils.getImages("data/t10k-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/t10k-labels.idx1-ubyte");
			int correct = 0;
			NeuralNetwork net = NeuralNetwork.loadFromDisk("results/recognizer1494907831132.net");
			System.out.println("Starting");
			HashMap<Integer,Integer> record = new HashMap<>();
			for(int i = 0; i < images.length; i++){
				double[] guess = net.guess(ImageUtils.getDataFromBufferedImage(images[i]));
				double[] label = output[i];
				int correctIndex = maxIndex(label);
				if(maxIndex(guess) == correctIndex){
					correct++;
					System.out.println("Correctly identified " + correctIndex);
				}
				else{
					record.putIfAbsent(correctIndex, 0);
					record.put(correctIndex,record.get(correctIndex)+1);
					System.out.println("Incorrectly identified " + correctIndex);
				}
			}
			System.out.println("Got " + correct + " out of " + images.length);
			System.out.println(100d*(double)correct/(double)images.length + "%");
			System.out.println(record.toString());
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
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
	
	public static void guessRandom(NeuralNetwork network){
		try{
			BufferedImage[] images = ImageUtils.getImages("data/train-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/train-labels.idx1-ubyte");
			int index = new Random().nextInt(images.length);
			System.out.println(Arrays.toString(network.guess(ImageUtils.getCondensedData(images[index]),network.isClassification())));
			System.out.println(Arrays.toString(output[index]));
			ImageUtils.showImage(images[index]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int indexOf(double[] arr, double search){
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == search) return i;
		}
		return -1;
	}
	
	private static <T> boolean containsNull(T[] arr){
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == null){
				return true;
			}
		}
		return false;
	}
	
	public static void guessAll(NeuralNetwork network){
		try{
			BufferedImage[] images = ImageUtils.getImages("data/t10k-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/t10k-labels.idx1-ubyte");
			guessAll(network,images,output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void guessAll(NeuralNetwork network, BufferedImage[] images, double[][] output){
		guessAll(network, images, output, System.out);
	}
	
	public static void guessAll(NeuralNetwork network, BufferedImage[] images, double[][] output, PrintStream out){
		DataPoint[] data = new DataPoint[10];
		for(int i = 0; i < images.length; i++){
			int index = indexOf(output[i],1);
			if(data[index] == null){
				DataPoint dp = new DataPoint();
				dp.input = images[i];
				dp.output = output[i];
				data[index] = dp;
			}
			if(!containsNull(data)) break;
		}
		for(int i = 0; i < data.length; i++){
			DataPoint dp = data[i];
			out.println("========================\nTesting: " + i);
			out.println(Arrays.toString(network.guess(ImageUtils.getDataFromBufferedImage(dp.input),true)));
			out.println(Arrays.toString(dp.output));
		}		
	}
	
	static class DataPoint{
		public BufferedImage input;
		public double[] output;
	}
	
	public static void guessSpecific(NeuralNetwork network, int toGuess){
		try{
			BufferedImage[] images = ImageUtils.getImages("data/t10k-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/t10k-labels.idx1-ubyte");
			int i = 0;
			while(Integer.parseInt(Arrays.toString(output[i]).replaceAll("\\D|(\\.\\d)", ""),2) != toGuess){
				i++;
			}
			System.out.println(Arrays.toString(network.guess(ImageUtils.getDataFromBufferedImage(images[i]))));
			System.out.println(output[i][0]);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
