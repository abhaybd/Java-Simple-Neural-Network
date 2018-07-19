package example;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.coolioasjulio.neuralnetwork.Dendrite;
import com.coolioasjulio.neuralnetwork.NeuralNetwork;
import com.coolioasjulio.neuralnetwork.DenseLayer;
import com.coolioasjulio.neuralnetwork.utils.ImageUtils;

public class HeatMap {

    public static void main(String[] args) throws IOException {
        BufferedImage[] images = ImageUtils.readImages("data/t10k-images.idx3-ubyte");
        ImageUtils.showImage(images[0]);

        NeuralNetwork net = NeuralNetwork.loadFromDisk("DigitRecognizer.net");
        HeatMap hm = new HeatMap(net);
        BufferedImage map = hm.calculateHeatMap(ImageUtils.getDataFromBufferedImage(images[0]), 28, 28);
        ImageUtils.showImage(ImageUtils.toBufferedImage(map.getScaledInstance(280, 280, BufferedImage.SCALE_SMOOTH)));
    }

    private NeuralNetwork network;

    public HeatMap(NeuralNetwork network) {
        this.network = network;
    }

    public BufferedImage calculateHeatMap(double[] input, int width, int height) {
        if (input.length != width * height) return null;
        if (input.length != network.getLayers()[0].getNeurons().length) return null;

        network.guess(input);
        BufferedImage map = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        DenseLayer layer = network.getLayers()[0];

        for (int i = 0; i < layer.getNeurons().length; i++) {
            int x = i % map.getWidth();
            int y = i / map.getWidth();
            double score = 0;
            for (Dendrite d : layer.getNeurons()[i].getDendrites()) {
                score += d.getWeight();
            }
            score = Math.abs(score);
            int r = Math.min(round(score < 0 ? Math.abs(score) * 255 : 0), 255);
            int b = Math.min(round(score >= 0 ? Math.abs(score) * 255 : 0), 255);
            Color color = new Color(r, 0, b);
            map.setRGB(x, y, color.getRGB());
        }
        return map;
    }

    private int round(double d) {
        if (d % 1 >= 0.5) {
            return (int) d + 1;
        }
        return (int) d;
    }
}
