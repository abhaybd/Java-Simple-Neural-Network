package example;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageUtils {
	public static void main(String[] args) throws IOException{
		BufferedImage img = ImageIO.read(new File("data/butterfly.jpg"));
		monoColor(img);
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(img));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}
	public static void monoColor(BufferedImage img){
		for(int i = 0; i < img.getWidth(); i++){
			for(int j = 0; j < img.getHeight(); j++){
				Color c = new Color(img.getRGB(i, j));
				int avg = (c.getRed() + c.getGreen() + c.getBlue())/3;
				Color mono = new Color(avg, avg, avg);
				img.setRGB(i,j, mono.getRGB());
			}
		}
	}
}
