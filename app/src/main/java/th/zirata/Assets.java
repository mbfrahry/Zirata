package th.zirata;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Assets {

	public static Texture backgroundTextures;
	
	public static Texture blockTextures;
	
	public static Texture mainMenuTextures;
//	public static TextureRegion playRegion;
//	public static TextureRegion arrowRegion;
//	public static TextureRegion helpRegion;
//	public static TextureRegion rectangleRegion;
//	public static TextureRegion darkGrayRectangleRegion;
//	public static TextureRegion addIcon;

	public static Font font;

	public static Music song1;

	public static Sound explosionSound;
	public static Sound shootSound;

	public static HashMap<String, TextureRegion> textureRegions;

	
	public static void load(GLGame game){
		textureRegions = new HashMap<String, TextureRegion>();

		backgroundTextures = new Texture(game, "Backgrounds.png");
		addTextures(game.getFileIO(), backgroundTextures, "Backgrounds.txt");

		blockTextures = new Texture(game, "Blocks.png");
		addTextures(game.getFileIO(), blockTextures, "Blocks.txt");

		mainMenuTextures = new Texture(game, "MenuItems.png");
		addTextures(game.getFileIO(), mainMenuTextures, "MenuItems.txt");
		font = new Font(mainMenuTextures, 0, 0, 16, 16, 20);

		song1 = game.getAudio().newMusic("Song1.mp3");
		song1.setLooping(true);
		song1.setVolume(0.5f);

		explosionSound = game.getAudio().newSound("Explosion.wav");
		shootSound = game.getAudio().newSound("Shoot.wav");


		Settings.load(game.getFileIO());
		PlayerSave.load(game.getFileIO());
	}

	private static void addTextures(FileIO files, Texture texture, String filename){
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(files.readAsset(filename)));
			String line = "";
			while ((line = in.readLine()) != null) {
				//Parse each line and read numbers into array
				String[] lineVals = line.split("=");
				String[] coords = lineVals[1].trim().split(" ");
				float x1 = Float.parseFloat(coords[0]);
				float y1 = Float.parseFloat(coords[1]);
				float width = Float.parseFloat(coords[2]);
				float height = Float.parseFloat(coords[3]);
				textureRegions.put(lineVals[0].trim(), new TextureRegion(texture, x1, y1, width, height));
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


		//		//Open file and read it line by line
//		for (int i = 0; i < 5; i++){
//

	}

	public static void reload(){
		backgroundTextures.reload();
		blockTextures.reload();
		mainMenuTextures.reload();
	}

	public static void playSound(Sound sound) {
		if(Settings.soundEnabled)
			sound.play(1);
	}
}
