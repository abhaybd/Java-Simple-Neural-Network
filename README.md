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
* Bias neurons
* Adjustable activation function for each neuron layer
* ActivationFunction interface, allowing user to create their own activation function.
* `NeuralNetworkParams` object allows any combination of settings on the neural network, while still abstracting away much of the grunt work.

## What it can't do (but is on the to-do list)
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

A `NeuralNetworkParams` object has been added. This allows the user to edit whatever settings they want to fully customize their neural network, and pass the object to the constructor. The only property that must be set is the `layers` property.

To use adjustable activation functions, instantiate a `NeuralNetworkParams` object. From there you should assign the layers, bias, and activationFunction properties. You can automatically fill an `ActivationFunction[]` by calling `ActivationFunction#fillArray()` and passing the class of the ActivationFunction you want and the size of the array.

A DataVisualizer is now back up and running, and I removed the slick dependency. The DataVisualizer can be enabled by setting the `title` property of the `NeuralNetworkParams` object.
