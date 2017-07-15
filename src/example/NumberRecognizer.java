package example;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.TrainParams;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class NumberRecognizer {
	public static void main(String[] args) throws IOException{
		new NumberRecognizer(1,"data/train-labels.idx1-ubyte","data/train-images.idx3-ubyte");
	}
	
	public NumberRecognizer(int number, String labelPath, String imagePath) throws IOException{
		BufferedImage[] images = ImageUtils.readImages(imagePath);
		double[][] outputs = getLabels(labelPath, number);
		double[][] inputs = new double[images.length][];
		for(int i = 0; i < images.length; i++){
			inputs[i] = ImageUtils.getDataFromBufferedImage(images[i]);
		}
		int size = inputs[0].length;
		NeuralNetwork network = new NeuralNetwork(new int[]{size, size/2, 28, 1}, new int[]{1,1,0});
		TrainParams tp = new TrainParams(inputs, outputs, 0.5, 0.9, 0.05);
		network.train(tp);
		guess(network, number);
	}
	
	private void guess(NeuralNetwork network, int number){
		try{
			BufferedImage[] images = ImageUtils.readImages("data/t10k-images.idx3-ubyte");
			double[][] output = getLabels("data/t10k-labels.idx1-ubyte", number);
			int index = 0;
			int val = 0;
			Random r = new Random();
			for(int i = 5; i < output.length; i++){
				val = Math.round((float)output[i][0]);
				if(val == number){
					index = i;
					if(r.nextInt(10) == 0){
						break;						
					}
				}
			}
			ImageUtils.showImage(images[index], 280, 280);
			System.out.println("Guessing: " + val);
			System.out.println(Arrays.toString(network.guess(ImageUtils.getDataFromBufferedImage(images[index]))));
			System.out.println();
			System.out.println("Guessing: " + output[0][0]);
			System.out.println(Arrays.toString(network.guess(ImageUtils.getDataFromBufferedImage(images[0]))));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private double[][] getLabels(String path, int number) throws IOException{
		try(DataInputStream in = new DataInputStream(new FileInputStream(path))){
			in.readInt();
			int numLabels = in.readInt();
			numLabels = 100;
			double[][] labels = new double[numLabels][1];
			for(int i = 0; i < labels.length; i++){
				int read = in.read();
				if(read == number){
					labels[i][0] = 1;
				}
			}
			return labels;
		}
		catch(IOException e){
			throw e;
		}
	}
}
