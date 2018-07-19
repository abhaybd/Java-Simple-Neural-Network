package com.coolioasjulio.neuralnetwork;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;

public class DenseLayer extends Layer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Neuron[] neurons;
    private Neuron bias;

    public DenseLayer(int neurons, ActivationStrategy strategy) {
        this(neurons, strategy, true);
    }

    public DenseLayer(int neurons, ActivationStrategy strategy, boolean useBias) {
        super(strategy);
        this.neurons = new Neuron[neurons];
        for (int i = 0; i < neurons; i++) {
            this.neurons[i] = new Neuron();
        }
        if (useBias) {
            bias = new Neuron();
            bias.output = 1;
        }
    }

    public void activate() {
        double[] output = strategy.activate(this);
        double[] derivatives = strategy.derivativeOutput(this);
        for (int i = 0; i < output.length; i++) {
            neurons[i].output = output[i];
            neurons[i].derivative = derivatives[i];
        }
    }

    public void setRandomWeights(DenseLayer next) {
        for (Neuron neuron : neurons) {
            neuron.setUpDendrites(next);
        }

        bias.setUpDendrites(next);
    }

    public Neuron getBiasNeuron() {
        return bias;
    }

    public double[] getFlattenedOutput() {
        return Arrays.stream(neurons).mapToDouble(e -> e.output).toArray();
    }

    @Override
    public double[] getFlattenedWeightedSums() {
        return Arrays.stream(neurons).mapToDouble(e -> e.weightedSum).toArray();
    }

    public Neuron[] getNeurons() {
        return neurons;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DenseLayer)) return false;
        DenseLayer other = (DenseLayer) o;
        if (neurons.length != other.getNeurons().length) return false;
        if ((bias != null && other.bias == null) || (bias == null && other.bias != null)) return false;

        for (int i = 0; i < neurons.length; i++) {
            if (!neurons[i].equals(other.getNeurons()[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(neurons, bias);
    }
}
