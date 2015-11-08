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
	
	public void setPos(int p){ //p is an integer 1 - 100
		if(p < 1) x = begin;
		x = begin + (width - begin)*p/100;
	}
	
	public int getPos(){ 
		//DIFFERENT THAN inherited getX(). This returns a value 0 - 100.
		return (int) (((x - begin) / (double)width) *100);
	}
	
	public void slide(int mousex){
		x = mousex - 15;
		
		if(x < begin) x = begin;
		else if(x > begin + width) x = begin + width;
	}
}
