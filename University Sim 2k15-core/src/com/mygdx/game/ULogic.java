package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ULogic {
	
	
	//date vars
	Date date;
	SimpleDateFormat ft;
	
	final double DAYS_PER_FRAME = 0.1; //this actually doesn't have to be final, we can let the user change it to fast forward
	
	//background
	Texture t_bg;
	Texture t_overlay;
	//buildings
	Texture t_admin1, t_admin2, t_compSci, t_genSci, t_engineering, t_math, t_ass, t_res, t_test1;
	Texture t_adminfull; //only used by building selector
	//UI
	Texture t_stats, t_quit, t_slider;
	
	//Sprite lists
	List<Building> buildings;
	List<Slider> sliders;
	List<TextDisplay> statsFonts;
	List<TextDisplay> selectorFonts;
	
	Twit twit;
	
	//Money
	int money, students, capacity, tuition, happiness, targetStudents, upkeep;
	
	double TUITION_MAX;
	int COST_ADMIN, COST_COMPSCI, COST_GENSCI, COST_ENGINEERING, COST_MATH, COST_ASS, COST_RES;
	
	//random twits vars
	int rand_twit = 0, randit_twit = 0;
	
	//building selector view vars
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
	
	public ULogic(){ //constructor called in USim2k15::create()
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
		t_slider = new Texture("slider.png");
		//t_stats = new Texture("stats.png");
		//t_quit = new Texture("quit.png");
	}
	
	public void init(){
		loadImages();
		
		mapIndex = new ArrayList<Integer>();
		
		date = new Date();
		ft = new SimpleDateFormat ("MMMM d',' yyyy");
		
		loadData();
		
		twit = new Twit();
		
		//initialize lists
		buildings = new ArrayList<Building>();
		sliders = new ArrayList<Slider>();
		statsFonts = new ArrayList<TextDisplay>();
		selectorFonts = new ArrayList<TextDisplay>();
		
		
		//add text
		statsFonts.add(new TextDisplay(Integer.toString(money), 975, 715, 0, 2, Color.BLACK, -1)); //statsFonts.get(0) //money
		statsFonts.add(new TextDisplay("Students: " + Integer.toString(students), 930, 100, 330, 2, Color.BLACK, 1)); //statsFonts.get(1)
		statsFonts.add(new TextDisplay("Capacity: " + Integer.toString(capacity), 930, 150, 330, 2, Color.BLACK, 1)); //statsFonts.get(2)
		statsFonts.add(new TextDisplay("Happiness: " + Integer.toString(happiness), 930, 50, 330, 2, Color.BLACK, 1)); //statsFonts.get(3)
		statsFonts.add(new TextDisplay(ft.format(date), 10, 710, 330, 1.5f, Color.WHITE, -1)); //statsFonts.get(4)
		statsFonts.add(new TextDisplay("$ " + tuition, 540, 715, 0, 2, Color.WHITE, -1)); //statsFonts.get(5)
		
		statsFonts.add(new TextDisplay("Tuition: ", 400, 715, 0, 2, Color.WHITE, 0)); //eventually should be in some kind of menu fonts
		sliders.add(new Slider(t_slider, 400, 687, 100));
		sliders.get(0).setPos(tuition / TUITION_MAX);
	}
	
	public void loop(){ //called in USim2k15::render()
		
		compileMap();
		
		targetStudents = capacity*(50+happiness)/150;
		if(targetStudents < 0) targetStudents = 0;
		
		tuition = (int)( (sliders.get(0).getPos()) * TUITION_MAX);
		
		money += (tuition*students) / (365/DAYS_PER_FRAME);
		money -= upkeep; //can multiple if crazy
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		
		statsFonts.get(0).text = nf.format(money);
		statsFonts.get(1).text = "Students: " + Integer.toString(students);
		statsFonts.get(2).text = "Capacity: " + Integer.toString(capacity);
		statsFonts.get(3).text = "Happiness: " + Integer.toString(happiness);
		statsFonts.get(4).text = ft.format(date);
		statsFonts.get(5).text = "$ " + tuition;
		
		//new twit message every random amount of time
		if(randit_twit == rand_twit){
			twit.addTwitMessage(happiness);
			randit_twit = 0;
			rand_twit = ThreadLocalRandom.current().nextInt(30, 150);
		}
		randit_twit++;
		
		//this pseudorandom stuff makes students approach the target value (see above, based on 
		//capacity and happiness) then bounce around the target randomly.
		if(students != targetStudents){
			students+= ((targetStudents - students) / (ThreadLocalRandom.current().nextInt(1, 1000)));
			
		}else students += ThreadLocalRandom.current().nextInt(-15, 15);
		
		if(students > capacity) students = capacity;
		if(students <= 0) students = 0;
		
		handleInput();
		
		date.setTime(date.getTime() + (long)(8.64e+7 * DAYS_PER_FRAME)); 
	}
	
	public List<Sprite> getSprites(){ //called by USim2k15, compiles list of sprites to draw each loop
		List<Sprite> sprites = new ArrayList<Sprite>();
		sprites.add(new Sprite(t_bg,0,0));
		sprites.add(new Sprite(t_overlay,0,0));
		sprites.addAll(buildings);
		sprites.addAll(sliders);
		//sprites.addAll(menus);
		
		if(buildingSelector == 1)
			sprites.add(new Sprite(t_adminfull, 243, 110));
		else if(buildingSelectorSprite != null)
			sprites.add(new Sprite(buildingSelectorSprite, 275, 110));
		
		return sprites;
	}
	
	public List<TextDisplay> getFonts(){ //called by USim2k15, compiles list of texts to draw each loop
		List<TextDisplay> fonts = new ArrayList<TextDisplay>();
		fonts.addAll(statsFonts);
		fonts.addAll(selectorFonts);
		fonts.addAll(twit.twitFonts);
		
		return fonts;
	}
	
	public void compileMap(){ //called each loop
		if(!(oldMapIndex.equals(mapIndex))){ //only does its thing if the map is different
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
	
	public void handleInput(){
		//dev tools
		if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)){
			money+=1000000000;
		}
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
			Gdx.graphics.setDisplayMode(1280, 720, true);
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.graphics.setDisplayMode(1280, 720, false);
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
			saveData();
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
			money = 1000000;
			upkeep = 0;
			students = 0;
			date = new Date();
			for(int i = 0; i < 40; i++){
				mapIndex.set(i, 0);
			}
		}
		
		//building selector (can be done better)
		String info;
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
			buildingSelector = 1;
			buildingSelectorSprite = t_adminfull;
			info = "Admin Building\nPrice: " + COST_ADMIN;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			buildingSelector = 3;
			buildingSelectorSprite = t_compSci;
			info = "Computer Science\nPrice: " + COST_COMPSCI;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
			buildingSelector = 4;
			buildingSelectorSprite = t_genSci;
			info = "Science\nPrice: " + COST_GENSCI;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
			buildingSelector = 5;
			buildingSelectorSprite = t_engineering;
			info = "Engineering\nPrice: " + COST_ENGINEERING;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)){
			buildingSelector = 6;
			buildingSelectorSprite = t_math;
			info = "Mathematics\nPrice: " + COST_MATH;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)){
			buildingSelector = 7;
			buildingSelectorSprite = t_ass;
			info = "Arts & Social Sciences\nPrice: " + COST_ASS;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_7)){
			buildingSelector = 8;
			buildingSelectorSprite = t_res;
			info = "Residence\nPrice: " + COST_RES;
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_8)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_9)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else{
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}

		selectorFonts.clear();
		selectorFonts.add(new TextDisplay(info, 226, 105, 160, 1, Color.BLACK, 1));
		
		if(Gdx.input.justTouched()) 
			handleClick(Gdx.input.getX(), Gdx.input.getY());
		
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			int mx = Gdx.input.getX();
			int my = Gdx.input.getY();
			//check and handle sliders
			for(int i = 0; i < sliders.size(); i++){
				int curx = sliders.get(i).getX();
				int cury = 720 - sliders.get(i).getY(); //flip y to compare to mouse
				if(my >= cury-30 && my < cury && mx > curx && mx < curx + 30){
					sliders.get(i).slide(mx);
				}
			}
		}
	}
	
	public void handleClick(int clickx, int clicky){ //called from handleInput() //coordinates are topleft index!

		
		if( ((clicky >= 214 && clicky < 278) || (clicky >= 424 && clicky < 488)) && buildingSelector != -1 ){ //clicked somehwere on a plot
			int itx = 0, ity = 214;
			for(int i = 0; i < 40; i++){
				
				if(itx>=1280){
					itx = 0;
					ity = 424;
				}
				
				if(clickx >= itx && clickx < itx+64 && clicky >= ity && clicky < ity+64){
					if(mapIndex.get(i) == 0){
						if(buildingSelector == 1){
							if(mapIndex.get(i+1) != 0 || i == 19 || i == 39 || money < COST_ADMIN){ //no room for admin (or not enough $$$)
								//trying to set admin building but can't
							}else{
								mapIndex.set(i, 1);
								mapIndex.set(i+1, 2);
								
								money -= COST_ADMIN; //admin cost 200k
								upkeep += 50;
							}
						}
						else {
							
							//take off proper cost
							if(buildingSelector == 3 && money >= COST_COMPSCI){ //computer science
								money -= COST_COMPSCI;
								upkeep += 300;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector >= 4 && buildingSelector <= 7 && money >= COST_GENSCI){ //4-7
								money -= COST_GENSCI;
								upkeep += 200;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector == 8 && money >= COST_RES){ //res
								money -= COST_RES; //student residences are fuckin EXPENSIVE
								upkeep += 500;
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
		
		else if(clicky < 200){ //clicked in menu area
			
		}
	}
	
	///save and load data from files
	
	public void saveData(){
		//map data
		try{
			File f_map = new File("C:/USim2k15/map.udat");
			
			if(!f_map.exists()){
				File directory = new File(f_map.getParentFile().getAbsolutePath());
				directory.mkdirs();
				
				f_map.createNewFile();
			}
			
			FileWriter fw = new FileWriter(f_map.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < mapIndex.size(); i++){
				bw.write(Integer.toString(mapIndex.get(i)) + " ");
			}
			
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//game data
		try{
			File f_dat = new File("C:/USim2k15/save.udat");
			
			if(!f_dat.exists()){
				File directory = new File(f_dat.getParentFile().getAbsolutePath());
				directory.mkdirs();
				
				f_dat.createNewFile();
			}
			
			FileWriter fw = new FileWriter(f_dat.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(money + " ");
			bw.write(students + " ");
			bw.write(date.getTime() + " ");
			bw.write(upkeep + " ");
			bw.write(tuition + " ");
			bw.write(happiness + " ");
			
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadData(){
		//map data
		try{
			File f_map = new File("C:/USim2k15/map.udat");
			
			if(!f_map.exists()){
				File directory = new File(f_map.getParentFile().getAbsolutePath());
				directory.mkdirs();
				
				f_map.createNewFile();
			}
			
			Scanner in = new Scanner(f_map);
			
			while(in.hasNextInt()){
				mapIndex.add(in.nextInt());
			}
			
			for(int i = mapIndex.size(); i < 40; i++) mapIndex.add(0);
			
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//game data
		
		//if date can be retrieved from file
		///set date to file
		//(no else because it's already initialized to current time)
		
		try{
			File f_dat = new File("C:/USim2k15/save.udat");
			
			if(!f_dat.exists()){
				File directory = new File(f_dat.getParentFile().getAbsolutePath());
				directory.mkdirs();
				
				f_dat.createNewFile();
			}
			
			Scanner in = new Scanner(f_dat);
			
			if(in.hasNextInt()) money = in.nextInt(); else money = 1000000;
			if(in.hasNextInt()) students = in.nextInt(); else students = 0;
			if(in.hasNextLong()){
				long d = in.nextLong();
				if(d != 0) date.setTime(d);
			} //no else because date is set by default when object is created				
			if(in.hasNextInt()) upkeep = in.nextInt(); else upkeep = 0;
			if(in.hasNextInt()) tuition = in.nextInt(); else tuition = 10000;
			if(in.hasNextInt()) happiness = in.nextInt(); else happiness = 100;
			
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		getData();
	}
	
	public void getData(){
		//Creating a SocketClient object
		//IP 142.177.105.129
		//LAN 192.168.2.13
    	ServerData client = new ServerData ("142.177.105.129", 1615);
        try {
            //trying to establish connection to the server
            client.connect();
            //waiting to read response from server
            String response = client.readResponse();
            String[] values = response.split(" ");
            
            TUITION_MAX = Double.parseDouble(values[0]);
        	COST_ADMIN = Integer.parseInt(values[1]); 
        	COST_COMPSCI = Integer.parseInt(values[2]);  
        	COST_GENSCI = Integer.parseInt(values[3]);  
        	COST_ENGINEERING = Integer.parseInt(values[4]);  
        	COST_MATH = Integer.parseInt(values[5]);  
        	COST_ASS = Integer.parseInt(values[6]);  
        	COST_RES = Integer.parseInt(values[7]); 
            
        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
	}
}
