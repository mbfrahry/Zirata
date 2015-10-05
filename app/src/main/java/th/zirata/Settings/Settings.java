package th.zirata.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.badlogic.androidgames.framework.FileIO;

public class Settings {

	
	public static String file = ".zirata";
	public static boolean soundEnabled = true;
	public static int spaceBucks = 1;
	public static int nextBlockCost = 0;
	public static int enemyHealth = 10;
	public static int numEnemies = 10;
	public static int currLevel = 1;
	public static int maxLevel = 1;
	public static int totalLevels = 5;
	public static boolean firstTime = true;
	
	public static void load(FileIO files){
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			soundEnabled = Boolean.parseBoolean(in.readLine());
			spaceBucks = Integer.parseInt(in.readLine());
			nextBlockCost = Integer.parseInt(in.readLine());
			enemyHealth = Integer.parseInt(in.readLine());
			numEnemies = Integer.parseInt(in.readLine());
			currLevel = Integer.parseInt(in.readLine());
			maxLevel = Integer.parseInt(in.readLine());
			firstTime = Boolean.parseBoolean(in.readLine());
		}catch(IOException e){
			
		}catch(NumberFormatException e){
			
		}finally{
			try{
				if(in != null)
					in.close();
			}catch(IOException e){
				
			}
		}
	}
	
	public static void save(FileIO files){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(files.writeFile(file)));
			out.write(Boolean.toString(soundEnabled));
			out.write("\n");
			out.write(spaceBucks + "\n");
			out.write(nextBlockCost + "\n");
			out.write(enemyHealth + "\n");
			out.write(numEnemies + "\n");
			out.write(currLevel + "\n");
			out.write(maxLevel + "\n");
			out.write(firstTime + "\n");
		}catch(IOException e){

		}finally{
			try{
				if(out != null)
					out.close();
			}catch(IOException e){

			}
		}
	}
	
	public static void reset(){
		spaceBucks = 1;
		nextBlockCost = 0;
		enemyHealth = 10;
		numEnemies = 10;
		currLevel = 1;
		maxLevel = 1;
		firstTime = true;
	}

}
