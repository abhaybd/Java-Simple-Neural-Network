package secondtry;

import java.util.Scanner;

public class Network {
	public static void main (String [] args) {
        Neuron xor = new Neuron(0.5f);
        Neuron left = new Neuron(1.5f);
        Neuron right = new Neuron(0.5f);
        left.setWeight(-1.0f);
        right.setWeight(1.0f);
        xor.connect(left, right);
        
        String response = "";
		Scanner input = new Scanner(System.in);
		while(!(response = input.nextLine()).equals("quit")){
			String[] parts = response.split(",");
			for(String s:parts){
				Neuron op = new Neuron(0.0f);
				op.setWeight(Boolean.parseBoolean(s));
				left.connect(op);
				right.connect(op);
			}
			xor.fire();
			System.out.println("Result: " + xor.isFired());			
		}
		input.close();



    }
}
