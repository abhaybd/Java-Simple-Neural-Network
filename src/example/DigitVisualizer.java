package example;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class DigitVisualizer {
    public static void main(String[] args) throws IOException {
        BufferedImage[] trainImages = ImageUtils.readImages("data/train-images.idx3-ubyte");

        BufferedImage[] testImages = ImageUtils.readImages("data/t10k-images.idx3-ubyte");

        NeuralNetwork network = NeuralNetwork.loadFromDisk("DigitRecognizer.net");

        PrintStream out = new PrintStream(new FileOutputStream("F:\\data.dat"));

        System.out.println("Loaded data. Starting guessing.");

        for (int i = 0; i < trainImages.length; i++) {
            double[] guess = network.guess(ImageUtils.getDataFromBufferedImage(trainImages[i]));
            out.println(Arrays.toString(guess));
        }

        System.out.println("Wrote train images.");

        for (int i = 0; i < testImages.length; i++) {
            double[] guess = network.guess(ImageUtils.getDataFromBufferedImage(testImages[i]));
            out.println(Arrays.toString(guess));
        }
        out.close();

        System.out.println("Finished.");
    }
}
