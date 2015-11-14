package com.mygdx.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Menu {
	
	public enum MenuType{
		GENERAL
	}
	
	int x, y;
	Texture t;
	
	MenuType type;
	
	List<Slider> sliders;
	List<TextDisplay> fonts;
	
	public Menu(MenuType type, int inX, int inY){
		this.type = type;
		sliders = new ArrayList<Slider>();
		fonts = new ArrayList<TextDisplay>();
		
		x = inX;
		y = inY;
		
		if(type == MenuType.GENERAL){
			t = ULogic.t_menuGeneral;
			fonts.add(new TextDisplay("Tuition: ", x + 25, y + 125, 0, 1.5f, Color.WHITE, -1));
			sliders.add(new Slider(ULogic.t_slider, x + 100, y + 100, 100));
			fonts.add(new TextDisplay("$ " + ULogic.tuition, x  + 250, y + 125, 0, 1.5f, Color.WHITE, -1));
			sliders.get(0).setPos(ULogic.tuition / ULogic.TUITION_MAX);
			
			fonts.add(new TextDisplay("Speed: ", x + 25, y + 45, 0, 1.5f, Color.WHITE, -1));
			sliders.add(new Slider(ULogic.t_slider, x + 100, y + 20, 100));
			fonts.add(new TextDisplay("" + ULogic.gameSpeed, x + 250, y + 45, 0, 1.5f, Color.WHITE, -1));
			sliders.get(0).setPos(ULogic.gameSpeed);
		}
	}
	
	public Texture getT() {return t;}
	public int getX() {return x;}
	public int getY() {return y;}
}
