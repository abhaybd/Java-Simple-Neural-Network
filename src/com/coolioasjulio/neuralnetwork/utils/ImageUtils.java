package com.coolioasjulio.neuralnetwork.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("unused")
public class ImageUtils {
    public static void main(String[] args) throws IOException {
        BufferedImage[] images = readImages("data/t10k-images.idx3-ubyte");
        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        double[] data = getDataFromBufferedImage(images[0]);
        int index = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = Math.round((float) (data[index] * 255d));
                int rgb = new Color(val, val, val).getRGB();
                image.setRGB(x, y, rgb);
                index++;
            }
        }
        showImage(images[0], 280, 280);
        showImage(image, 280, 280);
    }

    /**
     * Returns labels as double[][]. 2nd dimension double[] consists of all zeros, except for the designated output as a 1.
     *
     * @param path       Path of the file
     * @param numOutputs Number of possible output (length of 2nd dimension double[]
     * @return double[][] of the outputs
     * @throws IOException
     */
    public static double[][] readLabels(String path, int numOutputs) throws IOException {
        return readLabels(path, -1, numOutputs);
    }

    /**
     * Returns labels as double[][]. 2nd dimension double[] consists of all zeros, except for the designated output as a 1.
     *
     * @param path       Path of the file
     * @param length     number of labels to read. If length < 0 or length > numLabels all labels are returned
     * @param numOutputs Number of possible output (length of 2nd dimension double[]
     * @return double[][] of the outputs
     * @throws IOException
     */
    public static double[][] readLabels(String path, int length, int numOutputs) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(path))) {
            int magic = in.readInt();
            if (magic != 2049) {
                throw new IOException("Supplied file must be in the idx1-ubyte file format with magic number 2049");
            }
            int numLabels = in.readInt();
            if (length > 0 && length <= numLabels) numLabels = length;
            double[][] labels = new double[numLabels][numOutputs];
            for (int i = 0; i < labels.length; i++) {
                int val = in.read();
                labels[i][val] = 1;
            }
            return labels;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Reads all images from supplied path in idx3-ubyte file format.
     *
     * @param path path to image file
     * @return BufferedImage[] of all images
     * @throws IOException
     */
    public static BufferedImage[] readImages(String path) throws IOException {
        return readImages(path, -1);
    }

    /**
     * Reads `length` images from supplied path in idx3-ubyte file format. If length < 0 or length > numImages, the entire file will be returned.
     *
     * @param path to image file
     * @return BufferedImage[] of length `length` containing the read images
     * @throws IOException
     */
    public static BufferedImage[] readImages(String path, int length) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(path))) {
            int magic = in.readInt();
            if (magic != 2051) {
                throw new IOException("Supplied file must be in the idx3-ubyte file format with magic number 2051");
            }
            int numImages = in.readInt();
            if (length > 0 && length <= numImages) numImages = length;
            int rows = in.readInt();
            int cols = in.readInt();

            BufferedImage[] images = new BufferedImage[numImages];

            for (int i = 0; i < images.length; i++) {
                BufferedImage image = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < rows; x++) {
                    for (int y = 0; y < cols; y++) {
                        int val = in.read();
                        image.setRGB(y, x, new Color(val, val, val).getRGB());
                    }
                }
                images[i] = image;
            }

            return images;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Create a idx1-ubyte file with the supplied int[] as the labels for corresponding images.
     *
     * @param path   Path of ubyte file
     * @param labels int[] of labels to write
     * @throws IOException
     */
    public static void writeLabelUbyte(String path, int[] labels) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
        out.writeInt(2049);
        out.writeInt(labels.length);
        for (int label : labels) {
            out.write(label);
        }
        out.close();
    }

    /**
     * Creates a idx3-ubyte file with the supplied BufferedImage[] as the images for corresponding labels.
     *
     * @param path   Path of ubyte file
     * @param images BufferedImage[] of images to write
     * @throws IOException
     */
    public static void writeImageUbyte(String path, BufferedImage[] images) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
        out.writeInt(2051);
        out.writeInt(images.length);
        out.writeInt(images[0].getWidth());
        out.writeInt(images[0].getHeight());

        for (BufferedImage img : images) {
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    Color c = new Color(img.getRGB(x, y));
                    int val = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
                    out.write(val);
                }
            }
        }
        out.close();
    }

    /**
     * Converts BufferedImage to grayscale. This will change the supplied image
     *
     * @param img image to transform
     */
    public static void monoColor(BufferedImage img) {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color c = new Color(img.getRGB(i, j));
                int avg = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                Color mono = new Color(avg, avg, avg);
                img.setRGB(i, j, mono.getRGB());
            }
        }
    }

    /**
     * Shows a JFrame with the same dimensions as the image supplied. This image will NOT be scaled at all.
     *
     * @param image Image to show
     * @return Displayed JFrame
     */
    public static JFrame showImage(BufferedImage image) {
        return showImage(image, image.getWidth(), image.getHeight());
    }

    /**
     * Shows a JFrame with the supplied image scaled to the supplied width and height. The JFrame will be the same dimensions as the scaled image.
     *
     * @param image  Image to scale and display.
     * @param width  Width to scale the image to.
     * @param height Height to scale the image to.
     * @return Displayed JFrame
     */
    public static JFrame showImage(BufferedImage image, int width, int height) {
        JFrame frame = new JFrame();
        frame.setSize(width, height);
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(image.getScaledInstance(width, height, BufferedImage.SCALE_FAST)));
        frame.getContentPane().add(label);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Convert Image to BufferedImage
     *
     * @param img Image to convert
     * @return BufferedImage of img
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage buff = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = buff.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return buff;
    }

    /**
     * Gets data from BufferedImage, focused using the otsu threshold
     *
     * @param img image to get data from
     * @return double[] of normalized data from image
     */
    public static double[] getDataFromBufferedImage(BufferedImage img) {
        double[] toReturn = new double[img.getWidth() * img.getHeight()];
        int index = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                toReturn[index] = grayScale(new Color(img.getRGB(x, y)));
                index++;
            }
        }
        return otsu(toReturn);
    }

    /**
     * Returns the average of the rgb channels of the supplied color
     *
     * @param color to turn into grayscale
     * @return grayscale of color. (Average of rgb)
     */
    public static double grayScale(Color color) {
        return (color.getRed() + color.getBlue() + color.getGreen()) / 3;
    }

    /**
     * Same as com.coolioasjulio.neuralnetwork.utils.ImageUtils.grayScale(new Color(rgb));
     *
     * @param rgb Rgb as int to supply to Color constructor
     * @return grayscale of color
     */
    public static double grayScale(int rgb) {
        return grayScale(new Color(rgb));
    }

    private static double[] otsu(double[] original) {
        double[] data = original.clone();
        int[] histogram = new int[256];
        for (double datum : data) {
            histogram[(int) datum]++;
        }

        double sum = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += i * histogram[i];
        }

        double sumB = 0;
        int wB = 0;
        int wF = 0;
        double maxVariance = 0;
        int threshold = 0;
        int i = 0;
        boolean found = false;

        while (i < histogram.length && !found) {
            wB += histogram[i];
            if (wB != 0) {
                wF = data.length - wB;
                if (wF != 0) {
                    sumB += (i * histogram[i]);
                    double mB = sumB / wB;
                    double mF = (sum - sumB) / wF;
                    double varianceBetween = wB * Math.pow((mB - mF), 2);

                    if (varianceBetween > maxVariance) {
                        maxVariance = varianceBetween;
                        threshold = i;
                    }
                } else {
                    found = true;
                }
            }
            i++;
        }

        for (i = 0; i < data.length; i++) {
            data[i] = data[i] <= threshold ? 0 : 1;
        }

        return data;
    }

    private static double[] normalize(double[] original) {
        double[] normalized = new double[original.length];
        for (int i = 0; i < original.length; i++) {
            double rgb = original[i];
            normalized[i] = rgb / 255;
        }
        System.out.println(Arrays.toString(normalized));
        return normalized;
    }
}
