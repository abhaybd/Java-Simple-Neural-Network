package com.coolioasjulio.neuralnetwork;

import java.util.ArrayList;
import java.util.Collections;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class DataVisualizer {
	
	public static int X_PADDING = 50;
	public static int Y_PADDING = 50;
	
	private Visualizer v;
	
	public DataVisualizer(){
		new Thread(() -> {
			try{
				v = new Visualizer("Neural Network");
				AppGameContainer appgc = new AppGameContainer(v);
				appgc.setDisplayMode(1024, 756, false);
				appgc.setShowFPS(false);
				appgc.setTargetFrameRate(100);
				appgc.start();
				System.err.println("started!");
			}
			catch(SlickException e){
				e.printStackTrace();
			}
		}).start();
	}
	
	public DataVisualizer(String title, float scale, double threshold){
		new Thread(() -> {
			try{
				v = new Visualizer(title,scale,threshold);
				AppGameContainer appgc = new AppGameContainer(v);
				appgc.setDisplayMode(1024, 756, false);
				appgc.start();
				System.err.println("started!");
			}
			catch(SlickException e){
				e.printStackTrace();
			}
		}).start();
	}
	
	public void addError(float error){
		if(v==null)return;
		v.addData(error);
	}
	
	public class Visualizer extends BasicGame {
		private ArrayList<Float> errors;
		private float scale;
		private double threshold;
		public Visualizer(String title) {
			super(title);
			errors = new ArrayList<Float>();
			scale = 4500;
			threshold = 0.03;
		}
		
		public Visualizer(String title, float scale, double threshold){
			super(title);
			errors = new ArrayList<Float>();
			this.scale = scale;
			this.threshold = threshold;
		}

		public void addData(float data){
			Collections.synchronizedList(errors).add(data*scale);
		}
		
		@Override
		public void render(GameContainer gc, Graphics g) throws SlickException {
			int skip = Collections.synchronizedList(errors).size()/(gc.getWidth()-X_PADDING*2);
			if(skip < 1) skip = 1;
			int x = 0;
			for(int i = 0; i < Collections.synchronizedList(errors).size(); i += skip){
				g.setColor(Color.white);
				g.drawLine(X_PADDING + x, gc.getHeight()-Y_PADDING, X_PADDING + x, gc.getHeight() - (Y_PADDING + Collections.synchronizedList(errors).get(i)));
				g.setColor(Color.red);
				g.drawLine(0, gc.getHeight()-(float)(threshold*scale)-Y_PADDING, gc.getWidth(), gc.getHeight()-(float)(threshold*scale) - Y_PADDING);
				x++;
			}
		}
		
		@Override
		public void init(GameContainer gc) throws SlickException {
		}
		
		@Override
		public void update(GameContainer gc, int arg1) throws SlickException {
		}
	}
}
