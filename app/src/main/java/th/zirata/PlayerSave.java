package th.zirata;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.math.Vector2;

import android.util.JsonReader;
import android.util.JsonWriter;

public class PlayerSave {
	
	public static ArrayList<Block> playerBlocks = new ArrayList<Block>(){{
		add(new Block(160, 240, 10, 0));
	}};
	public static String file = ".ship";



	public static void load(FileIO files){
		JsonReader reader = null;
		try {
			reader = new JsonReader(new InputStreamReader(files.readFile(file), "UTF-8"));
			readBlocksArray(reader);
		}catch(IOException e){

		}finally{
			try{
				if(reader != null)
					reader.close();
			}catch(IOException e){

			}
		}
	}

	public static void readBlocksArray(JsonReader reader) throws IOException{
		reader.beginArray();
		while(reader.hasNext()){
			readBlock(reader);
		}
        reader.endArray();
	}

	public static void readBlock(JsonReader reader) throws IOException{
		String type = null;
		double[] blockInfo;
        reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals("Type")) {
				type = reader.nextString();


			}
			if(name.equals("Info")) {
				blockInfo = readBlockInfoArray(reader);
				if(type != null) {
					try {
						Class<?> blockType = Class.forName(type);
						Constructor<?> blockConstructor = blockType.getConstructor(String.class);
						Object object = blockConstructor.newInstance(blockInfo);
						playerBlocks.add((Block)object);
					} catch (ClassNotFoundException e) {

					} catch (NoSuchMethodException e) {

					} catch (IllegalAccessException e) {

					} catch (InstantiationException e) {

					} catch (InvocationTargetException e) {

					}
				}
			}
		}

		reader.endObject();
	}

	public static double[] readBlockInfoArray(JsonReader reader) throws IOException{
		double[] blockInfo = new double[10];
		int count = 0;
		reader.beginArray();
		while(reader.hasNext()){
			blockInfo[count] = reader.nextDouble();
			count++;
		}
		reader.endArray();
		return blockInfo;
	}
	/*
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
	}*/

	public static void save(FileIO files){
		JsonWriter writer = null;
		try{
			writer = new JsonWriter(new OutputStreamWriter(files.writeFile(file)));
			writer.setIndent(" ");
			writeBlocksArray(writer);
			writer.close();
		}catch(IOException e){

		}finally{
			try{
				if(writer != null)
					writer.close();
			}catch(IOException e){

			}
		}
	}

	private static void writeBlocksArray(JsonWriter writer) throws IOException {
		writer.beginArray();
		for (Block block : playerBlocks) {
			writeBlock(writer, block);
		}
		writer.endArray();
	}

	private static void writeBlock(JsonWriter writer, Block block) throws IOException{
		writer.beginObject();
		writer.name("Type").value(block.getClass().toString());
		writer.name("Info");
		writeInformationArray(writer, block);

		writer.endObject();
	}

	private static void writeInformationArray(JsonWriter writer, Block block) throws IOException{
		writer.beginArray();
		writer.value(block.position.x);
		writer.value(block.position.y);
		writer.value(block.health);
		writer.value(block.energyCost);
		if(block.getClass().equals(TurretBlock.class)){
			TurretBlock tBlock = (TurretBlock)block;
			writer.value(tBlock.fireAngle);
		}
		writer.endArray();
	}


	/*
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
	}*/
	
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
