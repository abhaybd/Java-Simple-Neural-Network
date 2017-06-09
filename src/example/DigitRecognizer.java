package example;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class DigitRecognizer {
	public static final int WIDTH = 28;
	public static final int HEIGHT = 28;
	public static void main(String[] args) throws IOException{
		new DigitRecognizer("data/train-labels.idx1-ubyte","data/train-images.idx3-ubyte");
	}
	
	private NeuralNetwork network;
	public DigitRecognizer(String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.readImages(imagePath);
		double[][] outputs = ImageUtils.readLabels(labelPath);
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < images.length; i++){
			inputs[i] = ImageUtils.getDataFromBufferedImage(images[i]);
		}
		//ImageUtils.showImage(images[0],280,280);
		System.out.println(Arrays.toString(inputs[0]));
		System.out.println(Arrays.toString(outputs[0]));
		//network = new NeuralNetwork(new int[]{inputs[0].length, 1000, 10}, new int[]{1,1,0}); //(size+10)*2/3
		network = NeuralNetwork.loadFromDisk("recognizer2.net");
		network.activateVisualizer("DigitRecognizer");
		network.train(inputs, outputs, 0.1, 0.9, 0.02, false, 10);
		//network = NeuralNetwork.loadFromDisk("recognizer.net");
		Guesser.guessAll(network);
		network.writeToDisk("recognizer.net");
		PrintStream out = new PrintStream(new FileOutputStream("weights.log"));
		network.printWeights(out);
		out.close();
		System.out.println("Printed weights!");
	}
}
