package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Button extends Sprite {
	Texture t, hover;
	int x, y, width, height;
	ButtonType type;
	
	enum ButtonType{
		START,
		OPTIONS
	}
	
	Button(Texture it, Texture ihover, int ix, int iy, ButtonType itype){
		super(it, ix, iy);
		
		t = it;
		hover = ihover;
		x = ix;
		y = iy;
		width = it.getWidth();
		height = it.getHeight();
		type = itype;
	}
	
	public void handleMouse(int mx, int my){
		my = 720 - my; //invert my
		if(mx > x && mx < x + width && my > y && my < y + height) img = hover;
		else img = t;
	}
}
