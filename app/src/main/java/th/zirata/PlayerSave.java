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


	public static ArrayList<Block> bankedBlocks = new ArrayList<Block>(){{
		add(new BlankBlock(new Vector2(160, 240)));
	}};
	public static ArrayList<Block> activeBlocks = new ArrayList<Block>(){{
		add(new BlankBlock(new Vector2(160, 240)));
	}};
	public static String file = ".ship25";



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

		ArrayList<Block> potentialActiveBlocks = new ArrayList<Block>();
		reader.beginArray();
		reader.beginArray();
		while(reader.hasNext()){
			readBlock(reader, potentialActiveBlocks);
		}
		reader.endArray();

		if ( potentialActiveBlocks.size() > 0){
			for(int i = 0; i < potentialActiveBlocks.size(); i++){
				activeBlocks.add(potentialActiveBlocks.get(i));
				bankedBlocks.add(potentialActiveBlocks.get(i));
			}
		}

		ArrayList<Block> potentialBankBlocks = new ArrayList<Block>();

		reader.beginArray();
		while(reader.hasNext()){
			readBlock(reader, potentialBankBlocks);
		}
		reader.endArray();

		if ( potentialBankBlocks.size() > 0){
			bankedBlocks.addAll(potentialBankBlocks);
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

		writer.beginArray();
		for (Block block : activeBlocks) {
			writeBlock(writer, block);
		}
		writer.endArray();


		writer.beginArray();
		for (Block block : bankedBlocks) {
			if(!activeBlocks.contains(block)) {
				writeBlock(writer, block);
			}
		}
		writer.endArray();


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

	public static void reset(){
		activeBlocks = new ArrayList<Block>(){{
			add(new BlankBlock(new Vector2(160, 240)));
		}};
		bankedBlocks = new ArrayList<Block>(){{
			add(new BlankBlock(new Vector2(160, 240)));
		}};
	}
}
