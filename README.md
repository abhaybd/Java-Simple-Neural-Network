# Java-Simple-Neural-Network
A very rudimentary playground for my neural net experiments

## What it can do
* Adjustable number of layers
* Adjustable number of neurons per layer
* Stochastic backpropogation
* momentum
* `guess()` method extrapolates data based on current weights
* train based on data supplied in text files
* train based on data supplied as arrays

## What it can't do (but is on the to-do list)
* Bias neurons
* Dropout

## What it can't do (and probably will never do)
* GPU acceleration
* Most fancy-schmancy tricks



## Rough outline
Neural network class contains an array of NeuronLayers.
Each NeuronLayer contains an array of Neurons
Each Neuron has an array of Dendrites which connect to each Neuron on the next NeuronLayer.
Each Neuron also has a List of Dendrites which connect to each Neuron on the previous NeuronLayer.
The array and List hold references to Dendrites, so the Dendrites can be accessed/modified through either method.

The weighted sum (before activation function) can be accessed through the `getWeightedSum()` method of a Neuron.
The output (after activation function) can be accessed through the `getOutput()` method of a Neuron.

Dendrites have a start Neuron, end Neuron, and weight.

During forward propogation, the output of a Neuron is multiplied by the weight of the Dendrite and is added to the weightedSum of the end Neuron.
