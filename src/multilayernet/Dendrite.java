package multilayernet;

public class Dendrite {
	public double weight;
	private Neuron start, end;
	private double lastChange;
	public Dendrite(Neuron start, Neuron end, double weight){
		this.weight = weight;
		this.start = start;
		this.end = end;
		end.addInput(this);
	}
	
	public Neuron getStart(){
		return start;
	}
	
	public Neuron getEnd(){
		return end;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public void adjustWeight(double amount){
		lastChange = amount;
		weight += amount;
	}
	
	public double getLastChange(){
		return lastChange;
	}
}
