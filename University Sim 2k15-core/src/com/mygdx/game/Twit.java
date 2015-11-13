/*
 * Twit messages
 * 
 */

package com.mygdx.game;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Twit{
	List<String> positive = new ArrayList<String>(),
				 vpositive = new ArrayList<String>(),
				 negative = new ArrayList<String>(),
				 vnegative = new ArrayList<String>();
	
	List<TextDisplay> twitFonts = new ArrayList<TextDisplay>();
	String twitMessage;
	
	public Twit(){
		for(int i = 0;i<4;i++){
			loadString(i);
		}
	}
	
	//Load the different happy/mad/sad messages from file into the lists to be later combined with the messages() method call.
	public void loadString(int flag){
		
		if(flag == 0){
			FileHandle file = Gdx.files.internal("data/vnegative.txt");
			String data = file.readString();
			String[] tokens = data.split("\n");
			
			for(int i = 0; i < tokens.length; i++){
				vnegative.add(tokens[i]);
			}
		}
		else if(flag == 1){
			FileHandle file = Gdx.files.internal("data/negative.txt");
			String data = file.readString();
			String[] tokens = data.split("\n");
			
			for(int i = 0; i < tokens.length; i++){
				negative.add(tokens[i]);
			}
		}
		else if(flag == 2){
			FileHandle file = Gdx.files.internal("data/positive.txt");
			String data = file.readString();
			String[] tokens = data.split("\n");
			
			for(int i = 0; i < tokens.length; i++){
				positive.add(tokens[i]);
			}
		}
		else if(flag == 3){
			
			FileHandle file = Gdx.files.internal("data/vpositive.txt");
			String data = file.readString();
			String[] tokens = data.split("\n");
			
			for(int i = 0; i < tokens.length; i++){
				vpositive.add(tokens[i]);
			}
		}
	}

	//Call this method to get a new TwitMessage in the form of a String
	public String getNewTwitMessage(int happy){
		String message = "";
		if(happy>80){
			message = messages()+(vpositive.get(ThreadLocalRandom.current().nextInt(0,vpositive.size()-1)));
		}
		else if(happy>50){
			message = messages()+(positive.get(ThreadLocalRandom.current().nextInt(0,positive.size()-1)));
		}
		else if(happy>25){
			message = messages()+(negative.get(ThreadLocalRandom.current().nextInt(0,negative.size()-1)));
		}
		else{
			message = messages()+(vnegative.get(ThreadLocalRandom.current().nextInt(0,vnegative.size()-1)));
		}
		
		return message;
	}
	//This method returns the first part of a TwitMessage((c)2k15)
	private String messages(){
		String[] templates = {"This school is ","I chose this school because it is so ","The people here are ","#ThisUniversity ","I wish this school wasn't so damn ","Free pizza is ",
				"This school couldn't be any more ","My life in one word? #","Ugh, exams. #","666 hail Stan 666 ","WHO IS STAN?! "};
		int index;
		index = ThreadLocalRandom.current().nextInt(0,templates.length);
		String template = templates[index];
		return template;
	}
	
	public void addTwitMessage(int happiness){
		twitMessage = (getNewTwitMessage(happiness));
		for(int i = 0; i < twitFonts.size(); i++){
			twitFonts.get(i).y-=33;
		}
		twitFonts.add(0, new TextDisplay(twitMessage, 15, 170, 180, 1, Color.BLACK, 1));
		if(twitFonts.size() > 5){
			twitFonts.remove(5);
		}
	}
}
