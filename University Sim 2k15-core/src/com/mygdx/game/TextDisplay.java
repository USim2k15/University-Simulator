package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

public class TextDisplay {
	String text;
	int x, y, tWidth;
	float scale;
	Color color;
	int align;
	
	public TextDisplay(String txt, int ix, int iy, int width, float scl, Color clr, int algn){
		text = txt;
		x = ix;
		y = iy;
		tWidth = width;
		scale = scl;
		color = clr;
		align = algn;
	}
	
}
