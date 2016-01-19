package th.zirata.Settings;

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

import th.zirata.Game.Level;
import th.zirata.Help.BuildHelpText;
import th.zirata.Help.GameHelpText;

public class Assets {

	public static Texture imageTextures;

	public static Font font;
	public static Font redFont;

	public static Sound explosionSound;
	public static Sound shootSound;

	public static Music menuMusic;
    public static Music gameMusic;

	public static HashMap<String, TextureRegion> textureRegions;


	public static void load(GLGame game){
		textureRegions = new HashMap<String, TextureRegion>();

		imageTextures = new Texture(game, "Images.png");
		addTextures(game.getFileIO(), imageTextures, "Images.txt");
		font = new Font(imageTextures, 301, 2001, 16, 16, 20);
		redFont = new Font(imageTextures, 558, 2001, 16, 16, 20);

		explosionSound = game.getAudio().newSound("Explosion.wav");
		shootSound = game.getAudio().newSound("Shoot.wav");
        gameMusic = game.getAudio().newMusic("MenuMusic.ogg");
		menuMusic = game.getAudio().newMusic("Menu.mp3");
		menuMusic.setLooping(true);
		menuMusic.play();

		Settings.load(game.getFileIO());
		PlayerSave.load(game.getFileIO());
		BuildHelpText.load(game.getFileIO());
        GameHelpText.load(game.getFileIO());
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
		imageTextures.reload();
		menuMusic.play();
	}

	public static void playSound(Sound sound) {
		if(Settings.soundEnabled)
			sound.play(1);
	}
}
