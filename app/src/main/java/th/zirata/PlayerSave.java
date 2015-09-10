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
	public static String file = ".ship3";



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

		Log.d("Velocity", playerBlocks.get(0).health + " ");
	}

	public static void readBlocksArray(JsonReader reader) throws IOException{
		ArrayList<Block> newBlocks = new ArrayList<Block>();
		reader.beginArray();
		while(reader.hasNext()){
			readBlock(reader, newBlocks);
		}
        reader.endArray();

		if ( newBlocks.size() > 0){
			playerBlocks = newBlocks;
		}
	}

	public static void readBlock(JsonReader reader, ArrayList<Block> newBlocks) throws IOException{
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
						Constructor<?> blockConstructor = blockType.getConstructor(double[].class);
						Object object = blockConstructor.newInstance(blockInfo);
						newBlocks.add((Block) object);
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
		double[] blockInfo = null;
		reader.beginArray();
		if(reader.hasNext()) {
			blockInfo = new double[reader.nextInt()];
		}
		int count = 0;
		while(reader.hasNext()){
			blockInfo[count] = reader.nextDouble();
			count++;
		}
		reader.endArray();
		return blockInfo;
	}

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
		writer.name("Type").value(block.getClass().toString().replace("class ", ""));
		writer.name("Info");
		writeInformationArray(writer, block);

		writer.endObject();
	}

	private static void writeInformationArray(JsonWriter writer, Block block) throws IOException{
		writer.beginArray();
		writer.value(block.constructorArgLength);
		writer.value(block.position.x);
		writer.value(block.position.y);
		writer.value(block.maxHealth);
		writer.value(block.energyCost);
		block.writeExtraInfo(writer);
		writer.endArray();
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
			playerBlocks.add(new EnergyBlock(x, y, 10, 0, 10));
		}

	}

	public static void reset(){
		playerBlocks = new ArrayList<Block>(){{
			add(new Block(160, 240, 10, 0));
		}};
	}
}
