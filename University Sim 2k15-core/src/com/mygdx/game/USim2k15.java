/*
 * https://libgdx.badlogicgames.com/documentation.html
 * 
 */



package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

import com.mygdx.game.ULogic;

public class USim2k15 extends ApplicationAdapter {
	enum State{
		NONE,
		MENU,
		GAME
	}
	
	static State state;
	State oldState;
	
	SpriteBatch batch;
	
	static ULogic uLogic;
	static Menu menu;
	
	private BitmapFont font;
	
	long splashTimer;
	
	Texture splash;
	Texture open;
	
	final int SPLASH_TIME = 3000;
	
	@Override
	public void create () {
		
		splashTimer = TimeUtils.millis() + SPLASH_TIME;
		
		splash = new Texture(Gdx.files.internal("data/splash.jpg"));
		
		oldState = State.NONE;
		state = State.MENU;
		
		batch = new SpriteBatch();
		font = new BitmapFont();

	}

	@Override
	public void render () {
		
		//splash screen
		if(TimeUtils.millis() - splashTimer < 0){
			
			batch.begin();
			
			batch.setColor(1.0f, 1.0f, 1.0f, (TimeUtils.millis() - splashTimer) / (float)SPLASH_TIME);
			
			batch.draw(splash, 0, 0);
			
			batch.end();
			
			return;
		}
		
		//game
		
		List<Sprite> sprites = new ArrayList<Sprite>();
		List<TextDisplay> fonts = new ArrayList<TextDisplay>();
		
		//retrieve data based on state
		if(state == State.GAME){
			if(state != oldState){ //if state switched, initialize
				oldState = state;
				uLogic = new ULogic();
			}
			
			uLogic.loop();
			
			sprites = uLogic.getSprites();
			fonts = uLogic.getFonts();
		}else if(state == State.MENU){
			if(state != oldState){ //if state switched, initialize
				oldState = state;
				menu = new Menu();
			}
			
			menu.loop();
			
			sprites = menu.getSprites();
			//no fonts in menu yet.
		}
		
		//draw everything
		batch.setColor(Color.WHITE);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		for(int i = 0; i < sprites.size(); i++){
			batch.draw(sprites.get(i).getTexture(), sprites.get(i).getX(), sprites.get(i).getY());
		}
		for(int i = 0; i < fonts.size(); i++){
			TextDisplay current = fonts.get(i);
			font.getData().setScale(current.scale, current.scale);
			font.setColor(current.color);
			font.draw(batch, current.text, current.x, current.y, current.tWidth, current.align, true);
		}
		batch.end();
		
		Gdx.graphics.setTitle("University Simulator    FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	
	@Override
	public void dispose(){
		uLogic.saveData();
	}
}
