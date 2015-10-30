/*
 * https://libgdx.badlogicgames.com/documentation.html
 * 
 */



package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Texture;

import java.util.List;
import java.util.ArrayList;

import com.mygdx.game.ULogic;

public class USim2k15 extends ApplicationAdapter {
	SpriteBatch batch;
	
	ULogic uLogic;
	
	private BitmapFont font;
	
	long splashTimer;
	
	Texture splash;
	
	@Override
	public void create () {
		
		splashTimer = TimeUtils.millis() + 0;
		splash = new Texture("splash.jpg");
		
		uLogic = new ULogic();
		
		batch = new SpriteBatch();
		
		font = new BitmapFont();
	}

	@Override
	public void render () {
		
		if(TimeUtils.millis() - splashTimer < 0){
			
			batch.begin();
			
			batch.draw(splash, 0, 0);
			
			batch.end();
			
			return;
		}
		
		uLogic.loop();
		
		List<Sprite> sprites = uLogic.getSprites();
		List<TextDisplay> fonts = new ArrayList<TextDisplay>();
		fonts.addAll(uLogic.twit.twitFonts);
		fonts.addAll(uLogic.statsFonts);
		fonts.addAll(uLogic.selectorFonts);
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		for(int i = 0; i < sprites.size(); i++){
			batch.draw(sprites.get(i).getTexture(), sprites.get(i).getX(), sprites.get(i).getY());
		}
		for(int i = 0; i < fonts.size(); i++){
			TextDisplay current = fonts.get(i);
			font.getData().setScale(current.scale, current.scale);
			font.setColor(current.color);
			font.draw(batch, current.text, current.x, current.y, current.tWidth, 1, true);
		}
		batch.end();
	}
	
	@Override
	public void dispose(){
		uLogic.saveData();
	}
}
