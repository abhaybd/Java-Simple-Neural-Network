package com.coolioasjulio.neuralnetwork;

import com.coolioasjulio.neuralnetwork.activationstrategy.ActivationStrategy;

public abstract class Layer {
    protected ActivationStrategy strategy;

    public Layer(ActivationStrategy strategy) {
        this.strategy = strategy;
    }

    public abstract double[] getFlattenedWeightedSums();

    public abstract double[] getFlattenedOutput();

    public abstract Neuron[] getNeurons();

    public abstract Neuron getBiasNeuron();
}
