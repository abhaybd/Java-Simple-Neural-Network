package com.coolioasjulio.neuralnetwork;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DataVisualizer {
	
	public static int X_PADDING = 50;
	public static int Y_PADDING = 50;
	
	private Window w;
	
	public DataVisualizer(String title){
		init(title, 1080, 756);
	}
	
	public DataVisualizer(String title, int width, int height){
		init(title, width, height);
	}
	
	private void init(String title, int width, int height){
		new Thread(() -> {
			JFrame frame = new JFrame(title);
			frame.setSize(width, height);
			frame.setResizable(false);
			w = new Window(width, height);
			frame.add(w);
			frame.setVisible(true);
		}).start();
	}
	
	public void addError(double error, double threshold){
		w.addError(error, threshold);
	}
	
	class Window extends JPanel{
		private static final long serialVersionUID = 1L;
		
		private List<Double> errors;
		private double scale, max, threshold;
		
		public Window(int width, int height){
			errors = new ArrayList<>();
			scale = 1;
			max = 0;
			this.setSize(width, height);
		}
		
		public synchronized void addError(double error, double threshold){
			errors.add(error);
			this.threshold = threshold;
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.clearRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.black);
			
			if(errors.size() == 0) return;
			if(max == -1) max = errors.get(0);
			int change = Math.max(errors.size()/(this.getWidth() - 2*X_PADDING),1);
			int x = X_PADDING;
			for(int i = 0; i < errors.size(); i += change){
				if(errors.get(i) > max) max = errors.get(i);
				scale = (this.getHeight() - 2*Y_PADDING) / max;
				g.drawLine(x, this.getHeight() - Y_PADDING, x, (this.getHeight() - Y_PADDING) - round(scale * errors.get(i)));
				x++;
			}
			
			g.setColor(Color.RED);
			g.drawLine(0, (this.getHeight()-Y_PADDING)-round(threshold * scale), this.getWidth(), (this.getHeight()-Y_PADDING)-round(threshold * scale));
			this.repaint();
		}
		
		private int round(double d){
			if(d % 1 >= 0.5){
				return (int)d+1;
			}
			return (int)d;
		}
	}
}
