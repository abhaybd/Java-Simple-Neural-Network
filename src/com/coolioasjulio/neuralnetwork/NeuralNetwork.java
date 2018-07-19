package com.coolioasjulio.neuralnetwork;

import java.awt.Dimension;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;
import com.coolioasjulio.neuralnetwork.activationstrategy.ReLuActivationStrategy;
import com.coolioasjulio.neuralnetwork.activationstrategy.SigmoidActivationStrategy;

public class NeuralNetwork implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        double[][] inputs = new double[][]{
                {0, 1},
                {1, 0},
                {0, 0},
                {1, 1}
        };
        double[][] output = new double[][]{{1}, {1}, {0}, {0}};
        List<DenseLayer> layers = new ArrayList<>();
        layers.add(new DenseLayer(2, new ReLuActivationStrategy()));
        layers.add(new DenseLayer(2, new ReLuActivationStrategy()));
        layers.add(new DenseLayer(1, new SigmoidActivationStrategy(), false));
        NeuralNetwork net = new NeuralNetwork(layers.toArray(new DenseLayer[0]));

        TrainParams tp = new TrainParams(inputs, output, 0.2, 0.9, 0.0001);
        net.train(tp);
        net.printWeights(System.out);

        try {
            net.writeToDisk("XOR.net");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = "";
        Scanner input = new Scanner(System.in);
        while (!(response = input.nextLine()).equals("quit")) {
            String[] parts = response.split(",");
            double[] nums = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                nums[i] = Double.parseDouble(parts[i]);
            }
            System.out.println(Arrays.toString(net.guess(nums)));

        }
        input.close();
    }

    public static NeuralNetwork loadFromDisk(String path) {
        // TODO: Rewrite this
        return null;
    }

    private DenseLayer[] layers;
    private DataVisualizer dv = null;
    private TrainParams params;

    public NeuralNetwork(DenseLayer... layers) {
        this(null, layers);
    }

    public NeuralNetwork(String title, DenseLayer... layers) {
        if (title != null) dv = new DataVisualizer(title);

        this.layers = layers;

        randomWeights();
    }

    public DenseLayer[] getLayers() {
        return layers;
    }

    /**
     * Evaluate output from supplied input
     *
     * @param input to use
     * @return output gotten from input
     */
    public double[] guess(double[] input) {
        return evaluate(input);
    }

    public void activateVisualizer(String title) {
        if (dv == null) dv = new DataVisualizer(title);
    }

    /**
     * Train neural network with supplied paramaters
     *
     * @param params Params object containing all the parameters, including inputs, outputs, learningRate, momentum, classification, and batchSize.
     */
    public void train(TrainParams params) {
        this.params = params;
        int runs = 0;
        double startError = 0;
        openTrainingFrame();
        Queue<Double> prevErrors = new LinkedList<>();
        while (true) {
            Data data = getBatch(params.trainingData, params.batchSize);
            double[][] inputs = data.input;
            double[][] outputs = data.output;
            HashMap<Dendrite, Double> dendriteDeltaMap = new HashMap<>();
            double errorSum = 0;
            for (int i = 0; i < inputs.length; i++) {
                double[] results = evaluate(inputs[i]);
                double error = calculateAggregateError(results, outputs[i]); //calculate mean squared error
                errorSum += error;
                getErrors(results, outputs[i]);
                updateWeights(dendriteDeltaMap, params.learningRate, params.momentum);
            }
            double batchError = errorSum / inputs.length;
            prevErrors.add(batchError);
            if (runs == 0) startError = batchError;
            while (prevErrors.size() > 25) {
                prevErrors.remove();
            }
            double error = -1;
            error = getAvgError(prevErrors);
            if (dv != null) dv.addError(error, params.errorThreshold);
            System.out.println("Epoch: " + runs + ", error: " + batchError + ", avgError: " + error);
            runs++;
            if (error <= params.errorThreshold && runs > 100 || stop) break;
        }
        frame.dispose();
        System.out.println("\nFinished!");
        System.out.println("Start error: " + startError);
    }

    private double getAvgError(Queue<Double> q) {
        if (q.isEmpty()) return 0.0;
        return q.stream().mapToDouble(e -> e).average().getAsDouble();
    }

    boolean stop = false;
    private JFrame frame;

    private void openTrainingFrame() {
        frame = new JFrame();
        JButton stopButton = new JButton("Stop");
        JButton save = new JButton("Save network");
        stopButton.addActionListener(e -> {
            stop = true;
            frame.dispose();
        });
        save.addActionListener(e -> {
            try {
                writeToDisk("results/recognizer" + getCalendarAsString(Calendar.getInstance()) + ".net");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        JPanel hyperParameters = new JPanel();
        JTextField learningRate = new JTextField(String.valueOf(params.learningRate));
        JTextField momentum = new JTextField(String.valueOf(params.momentum));
        JTextField errorThreshold = new JTextField(String.valueOf(params.errorThreshold));
        hyperParameters.setLayout(new BoxLayout(hyperParameters, BoxLayout.X_AXIS));
        hyperParameters.add(learningRate);
        hyperParameters.add(momentum);
        hyperParameters.add(errorThreshold);
        JButton applyParameters = new JButton("Apply");
        applyParameters.addActionListener(e -> {
            if (isDouble(learningRate.getText())) {
                params.learningRate = Double.parseDouble(learningRate.getText());
            }
            if (isDouble(momentum.getText())) {
                params.momentum = Double.parseDouble(momentum.getText());
            }
            if (isDouble(errorThreshold.getText())) {
                params.errorThreshold = Double.parseDouble(errorThreshold.getText());
            }
        });

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(stopButton);
        frame.add(save);
        frame.add(Box.createRigidArea(new Dimension(0, 20)));
        frame.add(hyperParameters);
        frame.add(applyParameters);
        frame.pack();
        frame.setVisible(true);
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    private String getCalendarAsString(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_hh-mm");
        return sdf.format(c.getTime());
    }

    /**
     * Write neural network to disk. NOT WORKING
     *
     * @param path path to write to.
     */
    public void writeToDisk(String path) throws IOException {
        // TODO: Rewrite this
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    static class Data {
        public double[][] input, output;
    }

    private Data getBatch(Map<double[], List<double[]>> fullData, int batch) {
        List<double[]> keys = new ArrayList<>(fullData.keySet());
        Random rand = new Random();

        double[][] inputs = new double[batch][];
        double[][] outputs = new double[batch][];

        for (int i = 0; i < batch; i++) {
            int index = rand.nextInt(keys.size());
            double[] key = keys.remove(index);
            List<double[]> cases = fullData.get(key);
            inputs[i] = cases.get(rand.nextInt(cases.size()));
            outputs[i] = key;
            if (keys.size() == 0) {
                keys = new ArrayList<>(fullData.keySet());
            }
        }

        Data data = new Data();
        data.input = inputs;
        data.output = outputs;
        return data;
    }

    private double calculateAggregateError(double[] results, double[] expectedResults) {
        double error = 0;
        for (int i = 0; i < results.length; i++) {
            error += Math.pow(expectedResults[i] - results[i], 2);
        }
        return error / results.length;
    }

    private double[] evaluate(double[] input) {
        //reset the neuron values
        for (DenseLayer layer : layers) {
            for (Neuron neuron : layer.getNeurons()) {
                neuron.weightedSum = 0;
            }
        }

        //set the output of the input neurons to the input
        for (int i = 0; i < layers[0].getNeurons().length; i++) {
            layers[0].getNeurons()[i].output = input[i];
        }

        //cycle through all the neurons
        for (int i = 0; i < layers.length; i++) {
            if (i != 0) layers[i].activate();
            for (Neuron neuron : layers[i].getNeurons()) {
                if (i != layers.length - 1) {
                    for (Dendrite dendrite : neuron.getDendrites()) {
                        //Increment the weightedSum of the destination neuron by the source neuron output scaled by the weight
                        dendrite.getEnd().weightedSum += neuron.output * dendrite.weight;
                    }
                }
            }
            if (layers[i].getBiasNeuron() != null) {
                for (Dendrite dendrite : layers[i].getBiasNeuron().getDendrites()) {
                    dendrite.getEnd().weightedSum += layers[i].getBiasNeuron().output * dendrite.weight;
                }
            }
        }

        //get results
        double[] results = new double[layers[layers.length - 1].getNeurons().length];
        for (int i = 0; i < results.length; i++) {
            results[i] = layers[layers.length - 1].getNeurons()[i].output;
        }
        return results;
    }

    private void randomWeights() {
        for (int i = 0; i < layers.length - 1; i++) {
            layers[i].setRandomWeights(layers[i + 1]);
        }
    }

    private void getErrors(double[] results, double[] expectedResults) {
        for (int i = layers.length - 1; i > 0; i--) {
            DenseLayer layer = layers[i];
            ArrayList<Neuron> neurons = new ArrayList<>(Arrays.asList(layer.getNeurons()));
            if (layer.getBiasNeuron() != null) {
                neurons.add(layer.getBiasNeuron());
            }
            for (int j = 0; j < neurons.size(); j++) {
                Neuron neuron = neurons.get(j);
                double neuronError = 0;
                if (i == layers.length - 1) {
                    neuronError = neuron.derivative * (results[j] - expectedResults[j]);
                } else {
                    neuronError = neuron.derivative;

                    double sum = 0;
                    for (Dendrite dendrite : neuron.getDendrites()) {
                        sum += dendrite.weight * dendrite.getEnd().error;
                    }
                    neuronError *= sum;
                }
                neuron.error = neuronError;
            }
        }
    }

    private void updateWeights(HashMap<Dendrite, Double> dendriteDeltaMap, double learningRate, double momentum) {
        for (int i = layers.length - 1; i > 0; i--) {
            DenseLayer layer = layers[i];
            for (Neuron neuron : layer.getNeurons()) {
                for (Dendrite dendrite : neuron.getInputs()) {
                    double delta = learningRate * neuron.error * dendrite.getStart().output;
                    if (dendriteDeltaMap.containsKey(dendrite)) {
                        delta += momentum * dendriteDeltaMap.get(dendrite);
                    }
                    dendriteDeltaMap.put(dendrite, delta);
                    dendrite.adjustWeight(-delta);
                }
            }
        }
    }


    /**
     * Print all the weights to the supplied printstream.
     *
     * @param out PrintStream to print out to.
     */
    public void printWeights(java.io.PrintStream out) {
        for (int i = 0; i < layers.length - 1; i++) {
            DenseLayer layer = layers[i];
            out.print("Layer " + i + ": ");
            for (Neuron neuron : layer.getNeurons()) {
                for (Dendrite dendrite : neuron.getDendrites()) {
                    out.print(dendrite.weight + ",");
                }
                out.print("    ");
            }
            out.print("Bias: ");
            if (layer.getBiasNeuron() != null) {
                for (Dendrite dendrite : layer.getBiasNeuron().getDendrites()) {
                    out.print(dendrite.weight + ", ");
                }
            }
            out.println();
        }
    }
}
