package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class ULogic {
	
	//saved file data
	File f_map;
	
	
	//background(s)
	Texture t_bg;
	Texture t_overlay;
	//buildings
	Texture t_admin1, t_admin2, t_compSci, t_genSci, t_engineering, t_math, t_ass, t_res, t_test1, t_test2;
	Texture t_adminfull; //only used by building selector
	final int NUMBER_OF_BUILDINGS = 8;
	//UI
	Texture t_stats, t_quit;
	
	//Sprite lists
	List<Building> buildings;
	List<TextDisplay> fonts;
	List<TextDisplay> twitFonts;
	
	Menu menu = new Menu();
	
	//Money
	int money,students,capacity,tuition,happiness, targetStudents, upkeep;
	String twitMessage;
	int rand_twit = 0, randit_twit = 0; //random twits vars
	int buildingSelector;
	Texture buildingSelectorSprite;
	//Font
	BitmapFont font;
	
	List<Integer> mapIndex;
	/*
	 * 0 = nothing
	 * 1 = admin1
	 * 2 = admin2
	 * 3 = computer science
	 * 4 = science
	 * 5 = engineering
	 * 6 = math
	 * 7 = arts and social sciences
	 * 8 = residence
	 */
	List<Integer> oldMapIndex = new ArrayList<Integer>();
	
	int testVar = 0;
	
	public ULogic(){ //constructor called in USim2k15.create()
		loadImages();
		init();
	}
	
	private void loadImages(){
		//background(s)
		t_bg = new Texture("background.png");
		t_overlay = new Texture("overlay.png");
		
		//buildings
		t_admin1 = new Texture("admin1.png"); //1
		t_admin2 = new Texture("admin2.png"); //2
		t_compSci = new Texture("compsci.png"); //3
		t_genSci = new Texture("gen_science.png"); //4
		t_engineering = new Texture("engineering.png"); //5
		t_math = new Texture("math.png"); //6
		t_ass = new Texture("a&ss.png"); //7
		t_res = new Texture("res.png"); //8
		
		t_adminfull = new Texture("admin.png");
		
		//UI
		//t_stats = new Texture("stats.png");
		//t_quit = new Texture("quit.png");
	}
	
	public void init(){
		
		upkeep = 0;
		
		loadData();
		
		mapIndex = new ArrayList<Integer>();
		
		for(int i = 0; i < 41; i++){
			mapIndex.add(0);
		}
		
		menu.init();
		
		//Initialize students,money etc
		money = 1000000;
		students = 0;
		capacity = 0;
		tuition = 100; //= kb.nextInt();
		happiness = 100;
		
		
		//initialize lists
		buildings = new ArrayList<Building>();
		//menus = new ArrayList<Menu>();
		fonts = new ArrayList<TextDisplay>();
		twitFonts = new ArrayList<TextDisplay>();
		
		//add text
		fonts.add(new TextDisplay(Integer.toString(money), 975, 715, 180, 2)); //fonts.get(0)
		
		twitMessage = (menu.getNewTwitMessage(happiness)); //initialize
		twitFonts.add(new TextDisplay(twitMessage, 15, 170, 180, 1));
		
		fonts.add(new TextDisplay("Students: " + Integer.toString(students), 930, 100, 330, 2)); //fonts.get(1)
		fonts.add(new TextDisplay("Capacity: " + Integer.toString(capacity), 930, 150, 330, 2)); //fonts.get(2)
		fonts.add(new TextDisplay("Happiness: " + Integer.toString(happiness), 930, 50, 330, 2)); //fonts.get(3)
		
	}
	
	public void loop(){ //called in USim2k15.render()
		
		targetStudents = capacity - (100-happiness)*50;
		if(targetStudents < 0) targetStudents = 0;
		
		money+=tuition*students/3650;
		money-= upkeep *0.01; //can multiple if crazy
		//money+=500;
		fonts.get(0).text = Integer.toString(money);
		fonts.get(1).text = "Students: " + Integer.toString(students);
		fonts.get(2).text = "Capacity: " + Integer.toString(capacity);
		fonts.get(3).text = "Happiness: " + Integer.toString(happiness);
		
		if(randit_twit == rand_twit){
			twitMessage = (menu.getNewTwitMessage(happiness));
			for(int i = 0; i < twitFonts.size(); i++){
				twitFonts.get(i).y-=33;
			}
			twitFonts.add(0, new TextDisplay(twitMessage, 15, 170, 180, 1));
			if(twitFonts.size() > 5){
				twitFonts.remove(5);
			}
			randit_twit = 0;
			rand_twit = ThreadLocalRandom.current().nextInt(30, 150);
		}
		randit_twit++;
		
		if(students != targetStudents){
			//TODO figure out random student adding
			students+= ((targetStudents - students) / (ThreadLocalRandom.current().nextInt(50, 100))) + ThreadLocalRandom.current().nextInt(0, 2);
			
		}else students ++;
		
		if(students > capacity) students = capacity;
		if(students <= 0) students = 0;
		
		
		handleInput();
		
		compileMap();
	}
	
	public List<Sprite> getSprites(){
		List<Sprite> sprites = new ArrayList<Sprite>();
		sprites.add(new Sprite(t_bg,0,0));
		sprites.add(new Sprite(t_overlay,0,0));
		sprites.addAll(buildings);
		//sprites.addAll(menus);
		
		if(buildingSelector == 1)
			sprites.add(new Sprite(t_adminfull, 250, 100));
		else if(buildingSelectorSprite != null)
			sprites.add(new Sprite(buildingSelectorSprite, 250, 100));
		
		return sprites;
	}
	
	public void compileMap(){
		if(!(oldMapIndex.equals(mapIndex))){
			capacity = 0;
			
			buildings.clear();
			
			int itx = 0, ity = 442; //x and y iterators
			for(int i = 0; i < mapIndex.size(); i++){
				if(itx>=1280){
					itx = 0;
					ity = 232;
				}
				
				//make SURE YOU INCREASE ITERATOR itx
				if(mapIndex.get(i) == 0){
					itx+=64;
				}else if(mapIndex.get(i) == 1){
					buildings.add(new Building(t_admin1,itx,ity));
					itx+=64;
				}else if(mapIndex.get(i) == 2){
					buildings.add(new Building(t_admin2, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 3){
					capacity+=300;
					buildings.add(new Building(t_compSci, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 4){
					capacity+=200;
					buildings.add(new Building(t_genSci, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 5){
					capacity+=200;
					buildings.add(new Building(t_engineering, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 6){
					capacity+=200;
					buildings.add(new Building(t_math, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 7){
					capacity+=400;
					buildings.add(new Building(t_ass, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 8){
					capacity+=750;
					buildings.add(new Building(t_res, itx, ity));
					itx+=64;
				}
			}
			
		}
		
		oldMapIndex.clear();
		oldMapIndex.addAll(mapIndex);
	}
	
	public void handleClick(int clickx, int clicky){

		
		if((clicky >= 214 && clicky < 278) || (clicky >= 424 && clicky < 488)){
			int itx = 0, ity = 214;
			for(int i = 0; i < 40; i++){
				
				if(itx>=1280){
					itx = 0;
					ity = 424;
				}
				
				if(clickx >= itx && clickx < itx+64 && clicky >= ity && clicky < ity+64){
					if(mapIndex.get(i) == 0){
						if(buildingSelector == 1){
							if(mapIndex.get(i+1) != 0 || i == 19 || i == 39 || money < 200000){ //no room for admin (or not enough $$$)
								//trying to set admin building but can't
							}else{
								mapIndex.set(i, 1);
								mapIndex.set(i+1, 2);
								
								money -= 200000; //admin cost 200k
								upkeep += 20;
							}
						}
						else {
							
							//take off proper cost
							if(buildingSelector == 3 && money >= 100000){ //computer science
								money -= 100000;
								upkeep += 150;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector >= 4 && buildingSelector <= 7 && money >= 75000){ //4-7
								money -= 75000;
								upkeep += 100;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector == 8 && money >= 500000){ //res
								money -= 500000; //student residences are fuckin EXPENSIVE
								upkeep += 250;
								mapIndex.set(i, buildingSelector);
							}
						}
						
						break;
					}else{ //already a building here (right now we can't destroy)
						break;
					}
				}
				itx+=64;
			}
		}
	}
	
	public void saveData(){
		try{
			f_map = new File("map.txt");
			
			if(!f_map.exists()){
				f_map.createNewFile();
			}
			
			FileWriter fw = new FileWriter(f_map.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < 48; i++){
				bw.write(Integer.toString(mapIndex.get(i)) + " ");
			}
			
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadData(){
		try{
			f_map = new File("map.txt");
			
			if(!f_map.exists()){
				f_map.createNewFile();
			}
			
			Scanner in = new Scanner(f_map);
			
			for(int i = 0; in.hasNextInt(); i++){
				mapIndex.set(i, in.nextInt());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void handleInput(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
			Gdx.graphics.setDisplayMode(1280, 720, true);
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.graphics.setDisplayMode(1280, 720, false);
		}
		
		//building selector (can be done better)
		//what the fuck?
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
			buildingSelector = 1;
			buildingSelectorSprite = t_admin1;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			buildingSelector = 3;
			buildingSelectorSprite = t_compSci;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
			buildingSelector = 4;
			buildingSelectorSprite = t_genSci;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
			buildingSelector = 5;
			buildingSelectorSprite = t_engineering;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)){
			buildingSelector = 6;
			buildingSelectorSprite = t_math;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)){
			buildingSelector = 7;
			buildingSelectorSprite = t_ass;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_7)){
			buildingSelector = 8;
			buildingSelectorSprite = t_res;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_8)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_9)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
		}else{
			buildingSelector = -1;
			buildingSelectorSprite = null;
		}
		
		if(Gdx.input.justTouched()) 
			if(buildingSelector != -1)
				handleClick(Gdx.input.getX(), Gdx.input.getY());
	}
}
