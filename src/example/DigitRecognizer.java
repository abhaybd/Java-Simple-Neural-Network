package example;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
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
	
	private NeuralNetwork network;
	public DigitRecognizer(String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.getImages(imagePath);
		ImageUtils.showImage(images[2]);
		double[] outputs = ImageUtils.getLabels(labelPath);
		System.out.println(Arrays.toString(outputs));
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < inputs.length; i++){
			inputs[i] = getDataFromBufferedImage(images[i]);
		}
		network = new NeuralNetwork(new int[]{5,2,1}, new int[]{1,1,0});
		network.train(inputs, outputs, 0.1, 0.9, 100000);
		saveNeuralNetwork(network,"DigitRecognizer.net");
	}
	
	public int guess(String path) throws IOException{
		BufferedImage image = ImageUtils.toBufferedImage(ImageIO.read(new File(path)).getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST));
		double[] input = getDataFromBufferedImage(image);
		double result = network.guess(input);
		return (int) (result*10);
	}
	
	static double[] getDataFromBufferedImage(BufferedImage img){
		double[] toReturn = new double[5];
		Area a = new Area();
		double numPixels = 0;
		for(int i = 0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				if(!new Color(img.getRGB(j,i)).equals(Color.BLACK)){
					a.add(new Area(new Rectangle(j,i,1,1)));
					numPixels++;
				}
			}
		}
		Rectangle bounds = a.getBounds();
		toReturn[0] = bounds.getCenterX();
		toReturn[1] = bounds.getCenterY();
		toReturn[2] = bounds.getWidth();
		toReturn[3] = bounds.getHeight();
		toReturn[4] = numPixels;
		return toReturn;
	}
	
	void showImage(BufferedImage img){
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		ImageUtils.monoColor(img);
		label.setIcon(new ImageIcon(img.getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST).getScaledInstance(WIDTH*10, HEIGHT*10, BufferedImage.SCALE_FAST)));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}
}
