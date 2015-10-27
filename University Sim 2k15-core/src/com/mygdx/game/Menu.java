/*
 * Logic for menu (overlay)
 * 
 */

package com.mygdx.game;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Menu{
	List<String> positive = new ArrayList<String>(),
				 vpositive = new ArrayList<String>(),
				 negative = new ArrayList<String>(),
				 vnegative = new ArrayList<String>();
	
	public Menu(){
		init();
	}
	
	void init(){
		//Initialize the lists of adjectives
		for(int i = 0;i<4;i++){
			loadString(i);
		}
	}
	//Load the different happy/mad/sad messages from file into the lists to be later combined with the messages() method call.
	public void loadString(int flag){
		
		BufferedReader br = null;
		
		if(flag == 0){
			try {
				br = new BufferedReader(new FileReader("vnegative.txt"));
				
				String sCurrentLine;
				while((sCurrentLine = br.readLine()) != null){
					vnegative.add(sCurrentLine);
				}
				/*f = new Scanner (new File("C:/vnegative.txt"));
				while(f.hasNextLine()){
					vnegative.add(f.nextLine());
				}*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try{
					if(br!=null)br.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		if(flag == 1){
			try {
				br = new BufferedReader(new FileReader("negative.txt"));
				
				String sCurrentLine;
				while((sCurrentLine = br.readLine()) != null){
					negative.add(sCurrentLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try{
					if(br!=null)br.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		if(flag == 2){
			try {
				br = new BufferedReader(new FileReader("positive.txt"));
				
				String sCurrentLine;
				while((sCurrentLine = br.readLine()) != null){
					positive.add(sCurrentLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try{
					if(br!=null)br.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		if(flag == 3){
			try {
				br = new BufferedReader(new FileReader("vpositive.txt"));
				
				String sCurrentLine;
				while((sCurrentLine = br.readLine()) != null){
					vpositive.add(sCurrentLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try{
					if(br!=null)br.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
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
				"This school couldn't be any more ","My life in one word? #","Ugh, exams. #","666 hail Stan 666","WHO IS STAN?!"};
		int index;
		index = ThreadLocalRandom.current().nextInt(0,templates.length);
		String template = templates[index];
		return template;
	}
}
