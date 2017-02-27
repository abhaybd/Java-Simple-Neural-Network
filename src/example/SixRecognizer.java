package example;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import neuralnetwork.NeuralNetwork;

public class SixRecognizer {
	public static void main(String[] args) throws IOException{
		new SixRecognizer("data/train-labels.idx1-ubyte","data/train-images.idx3-ubyte");
	}
	public SixRecognizer(String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.getImages(imagePath);
		ImageUtils.showImage(images[2]);
		double[][] outputs = getSixLabels(labelPath);
		for(double[] arr:outputs){
			System.out.println(Arrays.toString(arr));
		}
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < inputs.length; i++){
			//inputs[i] = getDataFromBufferedImage(images[i]);
			inputs[i] = ImageUtils.getCondensedData(images[i]);
		}
		NeuralNetwork network = new NeuralNetwork(new int[]{inputs[0].length,inputs[0].length/2,1}, new int[]{1,1,0},true,"Digit",5000,Math.pow(0.03, 2)/2);
		network.train(inputs, outputs, 0.1, 0.9, 20000);
		//saveNeuralNetwork(network,"DigitRecognizer.net");
		network.writeToDisk("SixRecognizer.net");
		System.out.println("Saved!");
		Guesser.guessRandom(network);
		Guesser.guessSpecific(network, 6);
	}
	
	private double[][] getSixLabels(String path) throws IOException{
		try(DataInputStream in = new DataInputStream(new FileInputStream(path))){
			in.readInt();
			int numLabels = in.readInt();
			numLabels = 100;
			double[][] labels = new double[numLabels][1];
			for(int i = 0; i < labels.length; i++){
				int read = in.read();
				if(read == 6){
					labels[i][0] = 1;
				}
			}
			return labels;
		}
		catch(IOException e){
			throw e;
		}
	}
}
