package example;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;

public class DigitRecognizer {
	public static final int WIDTH = 28;
	public static final int HEIGHT = 28;
	public static void main(String[] args) throws IOException{
		new DigitRecognizer("data/train-labels.idx1-ubyte","data/train-images.idx3-ubyte");
	}
	
	private NeuralNetwork network;
	public DigitRecognizer(String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.getImages(imagePath);
		double[][] outputs = ImageUtils.getLabels(labelPath);
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < images.length; i++){
			inputs[i] = ImageUtils.getCondensedData(images[i]);
		}
		ImageUtils.showImage(images[0]);
		System.out.println(Arrays.toString(inputs[0]));
		System.out.println(Arrays.toString(outputs[0]));
		int size = inputs[0].length;
		network = new NeuralNetwork(new int[]{size, (size+10)*2/3, 10}, new int[]{1,1,0});
		network.train(inputs, outputs, 0.1, 0.9, 0.1);
		
		Guesser.guessAll(network);
	}
}
