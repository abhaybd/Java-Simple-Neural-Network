package example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import neuralnetwork.NeuralNetwork;

public class DigitRecognizer {
	public static final int WIDTH = 28;
	public static final int HEIGHT = 28;
	public static void main(String[] args) throws IOException{
		
	}
	
	private NeuralNetwork network;
	public DigitRecognizer(String path){
		File folder = new File(path);
		if(!folder.isDirectory()){
			System.err.println("Supplied path is not a folder!");
			return;
		}
		File[] images = folder.listFiles();
		ArrayList<double[]> inputList = new ArrayList<double[]>();
		ArrayList<Double> outputList = new ArrayList<Double>();
		for(File file:images){
			if(!file.getName().endsWith("jpg") && !file.getName().endsWith("png") && !file.getName().endsWith("jpeg")) continue;
			try {
				BufferedImage img = ImageUtils.toBufferedImage(ImageIO.read(file).getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST));
				ImageUtils.monoColor(img);
				double[] input = getDataFromBufferedImage(img);
				String result = file.getName().substring(file.getName().lastIndexOf(".")-3,file.getName().lastIndexOf("."));
				outputList.add(Double.parseDouble(result));
				inputList.add(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		double[][] inputs = inputList.toArray(new double[0][0]);
		double[] outputs = arrayListToArray(outputList);
		network = new NeuralNetwork(new int[]{WIDTH*HEIGHT,WIDTH*HEIGHT/2,1});
		network.train(inputs, outputs, 0.25);
	}
	
	public int guess(String path) throws IOException{
		BufferedImage image = ImageUtils.toBufferedImage(ImageIO.read(new File(path)).getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST));
		double[] input = getDataFromBufferedImage(image);
		double result = network.guess(input);
		return (int) (result*10);
	}
	
	double[] arrayListToArray(ArrayList<Double> list){
		double[] toReturn = new double[list.size()];
		for(int i = 0; i < list.size(); i++){
			toReturn[i] = list.get(i);
		}
		return toReturn;
	}
	
	double[] getDataFromBufferedImage(BufferedImage img){
		double[] toReturn = new double[img.getHeight()*img.getWidth()];
		for(int i = 0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				toReturn[img.getWidth()*j+i] = img.getRGB(i, j);
			}
		}
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
