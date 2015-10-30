package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

public class TextDisplay {
	String text;
	int x, y, tWidth;
	float scale;
	Color color;
	
	public TextDisplay(String txt, int ix, int iy, int width, float scl, Color clr){
		text = txt;
		x = ix;
		y = iy;
		tWidth = width;
		scale = scl;
		color = clr;
	}
	
}
