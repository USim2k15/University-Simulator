/*
 * Modeled after ULogic, Menu is used when the menu is active rather than the game. USim2k15 handles this.
 * 
 */

package com.mygdx.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;

public class Menu {
	//textures
	Texture t_bg, t_start, t_start_hover;
	
	//Sprite lists
	List<Button> buttons;
	
	public Menu(){
		init();
	}
	
	public void init(){
		loadImages();
		
		buttons = new ArrayList<Button>();
		
		buttons.add(new Button(t_start, t_start_hover, 300, 300, Button.ButtonType.START));
	}
	
	private void loadImages(){
		//background
		t_bg = new Texture(Gdx.files.internal("data/menu/menubackground.png"));
		
		//buttons
		t_start = new Texture(Gdx.files.internal("data/menu/start.png"));
		t_start_hover = new Texture(Gdx.files.internal("data/menu/start_hover.png"));
	}
	
	public void loop(){
		handleInput();
	}
	
	public List<Sprite> getSprites(){
		List<Sprite> sprites = new ArrayList<Sprite>();
		sprites.add(new Sprite(t_bg, 0, 0));
		sprites.addAll(buttons);
		
		return sprites;
	}
	
	public void handleInput(){
		//F -> fullscreen
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
			Gdx.graphics.setDisplayMode(1280, 720, true);
		}
		
		//ESC -> escape fullscreen
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.graphics.setDisplayMode(1280, 720, false);
		}
		
		if(Gdx.input.justTouched()) handleClick(Gdx.input.getX(), Gdx.input.getY());
		
		for(Button b : buttons){
			b.handleMouse(Gdx.input.getX(), Gdx.input.getY());
		}
	}
	
	public void handleClick(int clickx, int clicky){
		clicky = 720 - clicky;
		for(Button b : buttons){
			if(clickx > b.x && clickx < b.x + b.width && clicky > b.y && clicky < b.y + b.width){
				if(b.type == Button.ButtonType.START) USim2k15.state = USim2k15.State.GAME;
				else if(b.type == Button.ButtonType.OPTIONS) ; //deal with options
			}
		}
	}
}
