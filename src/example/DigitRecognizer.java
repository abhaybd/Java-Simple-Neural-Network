package example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import neuralnetwork.NeuralNetwork;

public class DigitRecognizer {
	public static final int WIDTH = 28;
	public static final int HEIGHT = 28;
	public static void main(String[] args) throws IOException{
		new DigitRecognizer("data/train-labels.idx1-ubyte","data/train-images.idx3-ubyte");
	}
	
	void saveNeuralNetwork(NeuralNetwork network, String path){
		try{
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.defaultWriteObject();
			
			//out.writeObject(network);
			out.close();
			fileOut.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private NeuralNetwork network;
	public DigitRecognizer(String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.getImages(imagePath);
		ImageUtils.showImage(images[2]);
		double[][] outputs = ImageUtils.getLabels(labelPath);
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < inputs.length; i++){
			//inputs[i] = ImageUtils.getDataFromBufferedImage(images[i]);
			inputs[i] = ImageUtils.getDataFromBufferedImage(images[i]);
		}
		network = new NeuralNetwork(new int[]{inputs[0].length,(inputs[0].length+10)*2/3,10}, new int[]{1,1,0}, "Digit",500,Math.pow(0.03, 2)/2);
		network.train(inputs, outputs, 0.1, 0.9, 10, false);
		//saveNeuralNetwork(network,"DigitRecognizer.net");
		network.writeToDisk("DigitRecognizer.net");
		System.out.println("Saved!");
		Guesser.guessAll(network);
	}
	
	public int guess(String path) throws IOException{
		BufferedImage image = ImageUtils.toBufferedImage(ImageIO.read(new File(path)).getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST));
		//double[] input = ImageUtils.getDataFromBufferedImage(image);
		double[] input = ImageUtils.getDataFromBufferedImage(image);
		double[] result = network.guess(input);
		String binary = "";
		for(int i = 0; i < result.length; i++){
			binary += Math.round((float)result[i]);
		}
		return Integer.parseInt(binary, 2);
	}
	
	static void showImage(BufferedImage img){
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(img.getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST).getScaledInstance(WIDTH*10, HEIGHT*10, BufferedImage.SCALE_FAST)));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}
}
