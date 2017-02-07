package example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	
	
	public DigitRecognizer(String path){
		File folder = new File(path);
		File[] images = folder.listFiles();
		if(images == null){
			System.err.println("Supplied path is not a folder!");
			return;
		}
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
