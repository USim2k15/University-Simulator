package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Button extends Sprite {
	private Texture t, hover;
	private int width, height;
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

	
	//getters and setters
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
