package example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DigitRecognizer {
	public static void main(String[] args) throws IOException{
		new DigitRecognizer(ImageIO.read(new File("data/butterfly.jpg")));
	}
	
	public DigitRecognizer(BufferedImage image){
		showImage(image);
	}
	
	void showImage(BufferedImage img){
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		ImageUtils.monoColor(img);
		label.setIcon(new ImageIcon(img.getScaledInstance(28, 28, BufferedImage.SCALE_FAST).getScaledInstance(280, 280, BufferedImage.SCALE_FAST)));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}
}
