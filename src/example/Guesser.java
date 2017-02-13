package example;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import neuralnetwork.NeuralNetwork;

public class Guesser {
	public static void main(String[] args){
		try {
			int index = 0;
			FileInputStream in = new FileInputStream("DigitRecognizer.net");
			ObjectInputStream objOut = new ObjectInputStream(in);
			NeuralNetwork network = (NeuralNetwork)objOut.readObject();
			objOut.close();
			BufferedImage[] images = ImageUtils.getImages("data/t10k-images.idx3-ubyte");
			double[][] output = ImageUtils.getLabels("data/t10k-labels.idx1-ubyte");
			System.out.println(network.guess(DigitRecognizer.getDataFromBufferedImage(images[index])));
			System.out.println(output[index]);
			ImageUtils.showImage(images[index]);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
