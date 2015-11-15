package com.mygdx.game;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Menu {
	
	final int OFFSET_X = 400, OFFSET_Y = 146, NAV_WIDTH = 100, NAV_HEIGHT = 30;
	
	enum MenuType {
		NONE,
		GENERAL
	}
	
	MenuType type;
	
	int x, y;
	Texture t;
	
	List<Slider> sliders;
	List<TextDisplay> fonts;
	List<Sprite> navs;
	
	public Menu(){
		sliders = new ArrayList<Slider>();
		fonts = new ArrayList<TextDisplay>();
		navs = new ArrayList<Sprite>();
		
		type = MenuType.NONE;
		
		navs.add(new Sprite(ULogic.t_dashGen, OFFSET_X, OFFSET_Y));
	}
	
	public void handleMouse(int mx){ //only need mx because my is checked prior to calling this
		//do some hover stuff later...
		
		/*for(int i = 0; i < navs.size(); i++){
			navs.get(i).setHover(mx > OFFSET_X + (i*100) && mx < OFFSET_X + (i+1)*100);
		}*/
	}

	public void handleClick(int mx, int my){
		if(mx > OFFSET_X && mx < OFFSET_X + NAV_WIDTH){
			if(type == MenuType.GENERAL){
				type = MenuType.NONE;
				sliders.clear();
				fonts.clear();
				navs.get(0).img = ULogic.t_dashGen;
			}else{
			
				type = MenuType.GENERAL;
				
				sliders.clear();
				fonts.clear();
				
				navs.get(0).img = ULogic.t_dashGen_s;
				//img for all others = not _s
				
				fonts.add(new TextDisplay("Tuition: ", OFFSET_X + 25, OFFSET_Y - 20, 0, 1.5f, Color.WHITE, -1));
				sliders.add(new Slider(ULogic.t_slider, OFFSET_X + 100, OFFSET_Y - 45, 100));
				fonts.add(new TextDisplay("$ " + ULogic.tuition, OFFSET_X  + 250, OFFSET_Y - 20, 0, 1.5f, Color.WHITE, -1));
				sliders.get(0).setPos(ULogic.tuition / ULogic.TUITION_MAX);
				
				fonts.add(new TextDisplay("Speed: ", OFFSET_X + 25, OFFSET_Y - 75, 0, 1.5f, Color.WHITE, -1));
				sliders.add(new Slider(ULogic.t_slider, OFFSET_X + 100, OFFSET_Y - 100, 100));
				fonts.add(new TextDisplay("" + ULogic.gameSpeed, OFFSET_X + 250, OFFSET_Y - 75, 0, 1.5f, Color.WHITE, -1));
				sliders.get(1).setPos(ULogic.gameSpeed);
			}
			
		}else if(mx > OFFSET_X + NAV_WIDTH && mx < OFFSET_X + 2*NAV_WIDTH){
			
		}
			
	}
	
	public Texture getT() {return t;}
	public int getX() {return x;}
	public int getY() {return y;}
}
