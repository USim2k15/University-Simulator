package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class ULogic {
	
	
	//date vars
	Date date;
	SimpleDateFormat ft;
	
	static double gameSpeed = 0.1;
	
	//background
	Texture t_bg;
	Texture t_overlay;
	//buildings
	Texture t_admin1, t_admin2, t_compSci, t_genSci, t_engineering, t_math, t_ass, t_res, t_test1;
	Texture t_adminfull; //only used by building selector
	//UI
	static Texture t_stats, t_quit, t_slider, t_shade;
	static Texture t_dashGen, t_dashGen_s,
				   t_dashFin, t_dashFin_s;
	Texture t_student;
	
	//Sprite lists
	List<Building> buildings;
	List<Slider> sliders;
	List<TextDisplay> statsFonts;
	List<TextDisplay> selectorFonts;
	
	List<Student> studs;
	
	Twit twit;
	
	Dash dash;
	int sliding = -1;
	
	//Money
	static int money, students, capacity, tuition, happiness, targetStudents, upkeep;
	
	static double TUITION_MAX;
	int COST_ADMIN, COST_COMPSCI, COST_GENSCI, COST_ENGINEERING, COST_MATH, COST_ASS, COST_RES;
	
	//random twits vars
	int rand_twit = 0, randit_twit = 0;
	
	//building selector view vars
	int buildingSelector;
	int shadex, shadey;
	boolean shading, selecting;
	
	Texture buildingSelectorSprite;
	
	//Font
	BitmapFont font;
	
	static List<Integer> mapIndex; //make this an array
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
		t_bg = new Texture(Gdx.files.internal("data/background.png"));
		t_overlay = new Texture(Gdx.files.internal("data/overlay.png"));
		
		//buildings
		t_admin1 = new Texture(Gdx.files.internal("data/admin1.png")); //1
		t_admin2 = new Texture(Gdx.files.internal("data/admin2.png")); //2
		t_compSci = new Texture(Gdx.files.internal("data/compsci.png")); //3
		t_genSci = new Texture(Gdx.files.internal("data/gen_science.png")); //4
		t_engineering = new Texture(Gdx.files.internal("data/engineering.png")); //5
		t_math = new Texture(Gdx.files.internal("data/math.png")); //6
		t_ass = new Texture(Gdx.files.internal("data/a&ss.png")); //7
		t_res = new Texture(Gdx.files.internal("data/res.png")); //8
		
		t_adminfull = new Texture(Gdx.files.internal("data/admin.png"));
		
		//UI
		t_slider = new Texture(Gdx.files.internal("data/slider.png"));
		t_shade = new Texture(Gdx.files.internal("data/shade.png"));
		
		t_dashGen = new Texture(Gdx.files.internal("data/dash_gen.png"));
		t_dashGen_s = new Texture(Gdx.files.internal("data/dash_gen_s.png"));
		t_dashFin = new Texture(Gdx.files.internal("data/dash_fin.png"));
		t_dashFin_s = new Texture(Gdx.files.internal("data/dash_fin_s.png"));
		
		t_student = new Texture(Gdx.files.internal("data/student.png"));
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
		
		studs = new ArrayList<Student>();
		
		dash = new Dash();
		
		
		//add text
		statsFonts.add(new TextDisplay(Integer.toString(money), 975, 715, 0, 2, Color.BLACK, -1)); //statsFonts.get(0) //money
		statsFonts.add(new TextDisplay("Students: " + Integer.toString(students), 930, 100, 330, 2, Color.BLACK, 1)); //statsFonts.get(1)
		statsFonts.add(new TextDisplay("Capacity: " + Integer.toString(capacity), 930, 150, 330, 2, Color.BLACK, 1)); //statsFonts.get(2)
		statsFonts.add(new TextDisplay("Happiness: " + Integer.toString(happiness), 930, 50, 330, 2, Color.BLACK, 1)); //statsFonts.get(3)
		statsFonts.add(new TextDisplay(ft.format(date), 10, 710, 330, 1.5f, Color.WHITE, -1)); //statsFonts.get(4)
		
		//menus.add(new Menu(Menu.MenuType.GENERAL, 100, 520));
	}
	
	public void loop(){ //called in USim2k15::render()

		handleInput();
		
		compileMap();
		
		targetStudents = capacity*(50+happiness)/150;
		if(targetStudents < 0) targetStudents = 0;
		
		//update active menu data
		
		money += (tuition*students) / (365/gameSpeed);
		money -= upkeep * gameSpeed; //can multiple if crazy
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		
		statsFonts.get(0).text = nf.format(money);
		statsFonts.get(1).text = "Students: " + Integer.toString(students);
		statsFonts.get(2).text = "Capacity: " + Integer.toString(capacity);
		statsFonts.get(3).text = "Happiness: " + Integer.toString(happiness);
		statsFonts.get(4).text = ft.format(date);
		
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
		
		renderPeople();
		
		dash.updateDash(); //does this need to be called every loop?
		
		date.setTime(date.getTime() + (long)(8.64e+7 * gameSpeed)); 
	}
	
	public List<Sprite> getSprites(){ //called by USim2k15, compiles list of sprites to draw each loop
		List<Sprite> sprites = new ArrayList<Sprite>();
		sprites.add(new Sprite(t_bg,0,0));
		sprites.add(new Sprite(t_overlay,0,0));
		sprites.addAll(buildings);
		sprites.addAll(sliders);
		sprites.addAll(studs);
		
		if(selecting&&shading&&buildingSelector == 1){
			sprites.add(new Sprite(t_shade,shadex,shadey));
			sprites.add(new Sprite(t_shade,shadex+64,shadey));
		}else if(selecting&&shading) sprites.add(new Sprite(t_shade,shadex,shadey));
		
		sprites.addAll(Arrays.asList(dash.navs));
		sprites.addAll(dash.sliders);
		
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
		fonts.addAll(dash.fonts);
		
		return fonts;
	}
	
	public void renderPeople(){
		for(int i = 0; i < studs.size(); i++) 
			if(studs.get(i).runAround()) //makes them run around, returns true if at destination
				studs.remove(i);
		if(buildings.size() > 1)
			if(ThreadLocalRandom.current().nextInt(0, 100) == 0) 
				studs.add(new Student(t_student));
	}
	
	public void compileMap(){ //called each loop
		if(!(oldMapIndex.equals(mapIndex))){ //only does its thing if the map is different
			capacity = 0;
			upkeep = 0;
			
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
					upkeep += 50;
					itx+=64;
				}else if(mapIndex.get(i) == 2){
					buildings.add(new Building(t_admin2, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 3){
					upkeep += 300;
					capacity+=300;
					buildings.add(new Building(t_compSci, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 4){
					upkeep += 200;
					capacity+=200;
					buildings.add(new Building(t_genSci, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 5){
					upkeep += 200;
					capacity+=200;
					buildings.add(new Building(t_engineering, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 6){
					upkeep += 200;
					capacity+=200;
					buildings.add(new Building(t_math, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 7){
					upkeep += 200;
					capacity+=400;
					buildings.add(new Building(t_ass, itx, ity));
					itx+=64;
				}else if(mapIndex.get(i) == 8){
					upkeep += 500;
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
		
		//F -> fullscreen
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
			Gdx.graphics.setDisplayMode(1280, 720, true);
		}
		
		//ESC -> escape fullscreen
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.graphics.setDisplayMode(1280, 720, false);
		}
		
		//S -> save game state
		if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
			saveData();
		}
		
		//R -> restart game (kind of dev tool, delete after alpha)
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
		selecting = true;
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
			selecting = false;
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_9)){
			selecting = false;
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
			selecting = false;
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}else{
			selecting = false;
			buildingSelector = -1;
			buildingSelectorSprite = null;
			info = "Press and hold a number key to build.";
		}
		
		if(selecting){
			shading = false;
			int mousex = Gdx.input.getX();
			int mousey = Gdx.input.getY();
			if( ((mousey >= 214 && mousey < 278) || (mousey >= 424 && mousey < 488)) ){
				int itx = 0, ity = 214;
				for(int i = 0; i < 40; i++){
					if(itx >= 1280){
						itx = 0;
						ity = 424;
					}
					
					if(mousex >= itx && mousex < itx+64 && mousey >= ity && mousey < ity+64){
						if(mapIndex.get(i) == 0){ //if it's an empty plot shade it
							shading = true;
							shadex = itx;
							shadey = 656 - ity; //720 - 64
						}
						break;
					}
					
					itx+=64;
				}
			}
		}
		
		selectorFonts.clear();
		selectorFonts.add(new TextDisplay(info, 226, 105, 160, 1, Color.BLACK, 1));
		
		if(Gdx.input.justTouched()) 
			handleClick(Gdx.input.getX(), Gdx.input.getY());
		
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			int mx = Gdx.input.getX();
			int my = Gdx.input.getY();
			//check and handle sliders
			for(int j = 0; j < dash.sliders.size(); j++){
				int curx = dash.sliders.get(j).getX();
				int cury = 720 - dash.sliders.get(j).getY(); //flip y to compare to mouse
				if(my >= cury-30 && my < cury && mx > curx && mx < curx + 30){
					sliding = j;
					dash.sliders.get(j).slide(mx);
				}
			}
		}else sliding = -1;
		
		if(sliding != -1) dash.sliders.get(sliding).slide(Gdx.input.getX());
	}
	
	public void handleClick(int clickx, int clicky){ //called from handleInput() //coordinates are topleft index!

		if(clickx > dash.OFFSET_X && clickx < dash.OFFSET_X + 487 && clicky > 720 - dash.OFFSET_Y - dash.NAV_HEIGHT && clicky < 720 - dash.OFFSET_Y)
			dash.handleClick(clickx, clicky);
		
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
							if(i == 39 || i == 19 || mapIndex.get(i+1) != 0 || money < COST_ADMIN){ //no room for admin (or not enough $$$)
								//trying to set admin building but can't (show an error or something maybe)
							}else{
								mapIndex.set(i, 1);
								mapIndex.set(i+1, 2);
								
								money -= COST_ADMIN; //admin cost 200k
							}
						}
						else {
							
							//take off proper cost
							if(buildingSelector == 3 && money >= COST_COMPSCI){ //computer science
								money -= COST_COMPSCI;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector >= 4 && buildingSelector <= 7 && money >= COST_GENSCI){ //4-7
								money -= COST_GENSCI;
								mapIndex.set(i, buildingSelector);
							}
							else if(buildingSelector == 8 && money >= COST_RES){ //res
								money -= COST_RES; //student residences are fuckin EXPENSIVE
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
		
		getData(false);
	}
	
	public void getData(boolean local){
		//Creating a SocketClient object
		//IP 142.177.105.129
		//LAN 192.168.2.13
		ServerData client;
		
		if(local)
			client = new ServerData ("192.168.2.13", 1615);
		else
			client = new ServerData ("usim.nickbis.me", 1615);

        try {
            //trying to establish connection to the server
            client.connect();
            //waiting to read response from server
            String response = client.readResponse();
            String[] values = response.split(" ");
            
            //initialize game values
            TUITION_MAX = Double.parseDouble(values[0]);
        	COST_ADMIN = Integer.parseInt(values[1]); 
        	COST_COMPSCI = Integer.parseInt(values[2]);  
        	COST_GENSCI = Integer.parseInt(values[3]);  
        	COST_ENGINEERING = Integer.parseInt(values[4]);  
        	COST_MATH = Integer.parseInt(values[5]);  
        	COST_ASS = Integer.parseInt(values[6]);  
        	COST_RES = Integer.parseInt(values[7]); 
        	
        	//save values to file in case server goes down later
        	File f_server = new File("C:/USim2k15/server.udat");
        	if(!f_server.exists()){
				File directory = new File(f_server.getParentFile().getAbsolutePath());
				directory.mkdirs();
				
				f_server.createNewFile();
			}
        	
        	FileWriter fw = new FileWriter(f_server.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(TUITION_MAX + " ");
			bw.write(COST_ADMIN + " ");
			bw.write(COST_COMPSCI + " ");
			bw.write(COST_GENSCI + " ");
			bw.write(COST_ENGINEERING + " ");
			bw.write(COST_MATH + " ");
			bw.write(COST_ASS + " ");
			bw.write(COST_RES + " ");
			
			bw.close();
            
        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) { //cannot connect
            System.err.println("Cannot establish connection. Server may not be up. "+e.getMessage());
            if(!local) getData(true); //try local connection first
            else{ //both IP and LAN did not work
            	//load from file
            	File f_server = new File("C:/USim2k15/server.udat");
            	if(f_server.exists()){
            		try{
	            		Scanner in = new Scanner(f_server);
	            		String data = in.nextLine();
	            		String[] values = data.split(" ");
	            		
	            		TUITION_MAX = Double.parseDouble(values[0]);
	                	COST_ADMIN = Integer.parseInt(values[1]); 
	                	COST_COMPSCI = Integer.parseInt(values[2]);  
	                	COST_GENSCI = Integer.parseInt(values[3]);  
	                	COST_ENGINEERING = Integer.parseInt(values[4]);  
	                	COST_MATH = Integer.parseInt(values[5]);  
	                	COST_ASS = Integer.parseInt(values[6]);  
	                	COST_RES = Integer.parseInt(values[7]); 
	            		
	            		in.close();
	            		
	            		System.out.println("Data loaded from file instead of server.");
            		}catch(IOException excp){
            			excp.printStackTrace();
            		}
            	}else{ //file does not exist
            		System.out.println("Cannot connect to server and local data cannot be found. Exiting.");
            		System.exit(1);
            	}
            }
        }
	}
}
