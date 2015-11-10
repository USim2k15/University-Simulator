package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Slider extends Sprite{
	
	int begin;
	int width;
	
	Slider(Texture t, int ix, int iy, int w){
		super(t, ix, iy);
		begin = ix; //stays the same
		width = w;
	}
	
	public void setPos(double p){ //p is a decimal 0 - 1
		if(p == 0) x = begin;
		else x = (int)(begin + width*p);
	}
	
	public double getPos(){ 
		//DIFFERENT THAN inherited getX(). This returns a decimal value 0 - 1.
		return ((x - begin) / (double)width);
	}
	
	public void slide(int mousex){
		x = mousex - 15;
		
		if(x < begin) x = begin;
		else if(x > begin + width) x = begin + width;
	}
}
