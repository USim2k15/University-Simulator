package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Sprite {
	Texture img;
	int x, y;
	
	public Sprite(Texture t, int ix, int iy){
		img = t;
		x = ix;
		y = iy;
	}
	
	public Texture getTexture(){ return img;}//Used by USim2k15
	
	public int getX(){ return x;}
	public int getY(){ return y;}
}
