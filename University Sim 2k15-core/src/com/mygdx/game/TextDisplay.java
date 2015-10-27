package com.mygdx.game;

public class TextDisplay {
	String text;
	int x, y, tWidth;
	float scale;
	
	public TextDisplay(String txt, int ix, int iy, int width, float scl){
		text = txt;
		x = ix;
		y = iy;
		tWidth = width;
		scale = scl;
	}
}
