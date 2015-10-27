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

import java.util.List;
import java.util.ArrayList;

import com.mygdx.game.ULogic;

public class USim2k15 extends ApplicationAdapter {
	SpriteBatch batch;
	
	ULogic uLogic;
	
	private BitmapFont font;
	
	@Override
	public void create () {
		
		uLogic = new ULogic();
		
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.setColor(Color.BLACK);
	}

	@Override
	public void render () {
		
		uLogic.loop();
		
		List<Sprite> sprites = uLogic.getSprites();
		List<TextDisplay> fonts = new ArrayList<TextDisplay>();
		fonts.addAll(uLogic.twitFonts);
		fonts.addAll(uLogic.fonts);
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		for(int i = 0; i < sprites.size(); i++){
			batch.draw(sprites.get(i).getTexture(), sprites.get(i).getX(), sprites.get(i).getY());
		}
		for(int i = 0; i < fonts.size(); i++){
			float scale = fonts.get(i).scale;
			font.getData().setScale(scale, scale);
			font.draw(batch, fonts.get(i).text, fonts.get(i).x, fonts.get(i).y, 180, 1, true);
		}
		batch.end();
	}
}
