package th.zirata;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.badlogic.androidgames.framework.FileIO;

public class PlayerSave {
	
	public static ArrayList<Block> playerBlocks = new ArrayList<Block>(){{
		add(new Block(160, 240, 10, 0));
	}};
	public static String file = ".player";
	
	public static void load(FileIO files){
		playerBlocks = new ArrayList<Block>(){{
			add(new Block(160, 240, 10, 0));
		}};
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			int numBlocks = Integer.parseInt(in.readLine());
			if(numBlocks > 0){
				playerBlocks.remove(0);
			}
			for(int i = 0; i < numBlocks; i++){
				int blockType = Integer.parseInt(in.readLine());
				float x = Float.parseFloat(in.readLine());
				float y = Float.parseFloat(in.readLine());
				if(blockType == 1){
					float angle = Float.parseFloat(in.readLine());
					createBlock(blockType, x, y, angle);
				}
				else {
					createBlock(blockType, x, y);
				}
			}
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
			out.write(playerBlocks.size() + "\n");
			for(int i = 0; i < playerBlocks.size(); i++){
				Block currBlock = playerBlocks.get(i);
				if(currBlock.getClass().equals(TurretBlock.class)){
					out.write(1 + "\n");
				}

				else if(currBlock.getClass().equals(ArmorBlock.class)){
					out.write(2 + "\n");
				}

				else if (currBlock.getClass().equals(MachineGunBlock.class)){
					out.write(3 + "\n");
				}
				else if (currBlock.getClass().equals(EnergyBlock.class)){
					out.write(4 + "\n");
				}
				
				else{
					out.write(0 + "\n");
				}
				if (currBlock.getClass().equals(TurretBlock.class)){
					TurretBlock turBlock = (TurretBlock) currBlock;
					Log.d("Velocity", turBlock.fireAngle + " ");
					out.write(currBlock.position.x + "\n" + currBlock.position.y + "\n" + turBlock.fireAngle +"\n");
				}
				else {
					out.write(currBlock.position.x + "\n" + currBlock.position.y + "\n");
				}

			}
		}catch(IOException e){

		}finally{
			try{
				if(out != null)
					out.close();
			}catch(IOException e){

			}
		}
	}
	
	public static void createBlock(int blockType, float x, float y){
		if(blockType == 0){
			playerBlocks.add(new Block(x, y, 10, 0));
		}

		if(blockType == 2){
			playerBlocks.add(new ArmorBlock(x, y, 20));
		}
		if(blockType == 3){
			playerBlocks.add(new MachineGunBlock(x, y, 10, 3));
		}
		if(blockType == 4){
			playerBlocks.add(new EnergyBlock(x, y, 10, 10));
		}

	}

	public static void createBlock(int blockType, float x, float y, float angle){
		if(blockType == 1){
			playerBlocks.add(new TurretBlock(x, y, 10, 3, angle));
		}
	}

	public static void reset(){
		playerBlocks = new ArrayList<Block>(){{
			add(new Block(160, 240, 10, 0));
		}};
	}
}
