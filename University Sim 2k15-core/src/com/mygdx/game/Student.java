package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;

public class Student extends Sprite {
	
	int xdest, ydest;
	
	public Student(Texture t){
		super(t, 0, 0); //x and y will be decided internally
		init();
	}
	
	
	private void init(){
		//set initial position
		List<Integer> spots = new ArrayList<Integer>();
		for(int i = 0; i < ULogic.mapIndex.size(); i++){
			if(ULogic.mapIndex.get(i) != 0) spots.add(i);
		}
		int start = spots.get((int)Math.floor(Math.random()*spots.size()));
		
		if(start < 20){
			y = 433;
			x = 64*start + 32;
		}
		else{
			y = 300;
			x = 64*(start-20) + 32;
		}
		
		int end = start;
		while(end == start) end = spots.get((int)Math.floor(Math.random()*spots.size()));
		
		if(end < 20){
			ydest = 433;
			xdest = 64*end + 32;
		}
		else{
			ydest = 300;
			xdest = 64*(end-20) + 32;
		}
	}
	
	public boolean runAround(){ //returns true if at destination
		if(x > xdest) x--; else if(x < xdest) x++;
		if(y > ydest) y--; else if(y < ydest) y++;
		if(x == xdest && y == ydest) return true;
		
		return false;
	}
}
