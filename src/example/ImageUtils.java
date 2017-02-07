package example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}
