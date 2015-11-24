/*
 * Modeled after ULogic, Menu is used when the menu is active rather than the game. USim2k15 handles this.
 * 
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Menu {
	//textures
	Texture t_bg, t_start;
	
	
	public Menu(){
		init();
	}
	
	public void init(){
		loadImages();
	}
	
	private void loadImages(){
		//background
		t_bg = new Texture(Gdx.files.internal("data/menu/menubackground.png"));
		
		//buttons
		t_start = new Texture(Gdx.files.internal("data/menu/start.png"));
	}
}
