package example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageUtils {
	public static void main(String[] args) throws IOException{
		double[] labels = getLabels("data/test-labels.idx1-ubyte");
		BufferedImage[] images = getImages("data/train-images.idx3-ubyte");
		System.out.println(labels.length + " : " + images.length);
	}
	
	public static double[] getLabels(String path) throws IOException{
		try(DataInputStream in = new DataInputStream(new FileInputStream(path))){
			in.readInt();
			int numLabels = in.readInt();
			numLabels = 100;
			double[] labels = new double[numLabels];
			for(int i = 0; i < labels.length; i++){
				labels[i] = ((double)in.read())/10.0;
			}
			return labels;
		}
		catch(IOException e){
			throw e;
		}
	}
	
	public static BufferedImage[] getImages(String path) throws IOException{
		try(DataInputStream in = new DataInputStream(new FileInputStream(path))){
			in.readInt();
			int numImages = in.readInt();
			numImages = 100;
			int rows = in.readInt();
			int cols = in.readInt();
			
			BufferedImage[] images = new BufferedImage[numImages];
			
			for(int i = 0; i < images.length; i++){
				BufferedImage image = new BufferedImage(rows,cols,BufferedImage.TYPE_INT_RGB);
				for(int x = 0; x < rows; x++){
					for(int y = 0; y < cols; y++){
						int val = in.read();
						image.setRGB(y, x, new Color(val,val,val).getRGB());
					}
				}
				images[i] = image;
			}
			
			return images;
		} catch (IOException e) {
			throw e;
		}
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
	
	public static void showImage(BufferedImage image){
		JFrame frame = new JFrame();
		frame.setSize(280, 280);
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(image.getScaledInstance(280, 280, BufferedImage.SCALE_FAST)));
		frame.getContentPane().add(label);
		frame.setVisible(true);
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
