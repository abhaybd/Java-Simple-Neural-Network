package example;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import neuralnetwork.NeuralNetwork;

public class Guesser {
	public static void main(String[] args){
		try {
			int index = 1;
			NeuralNetwork network = NeuralNetwork.readFromDisk("DigitRecognizer.net");
			network.printWeights(System.out);
			BufferedImage[] images = ImageUtils.getImages("data/t10k-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/t10k-labels.idx1-ubyte");
			System.out.println(Arrays.toString(network.guess(DigitRecognizer.getDataFromBufferedImage(images[index]))));
			System.out.println(Arrays.toString(output[index]));
			ImageUtils.showImage(images[index]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
