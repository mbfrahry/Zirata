package th.zirata;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;
public class Assets {

	public static Texture backgroundTextures;
	public static TextureRegion backgroundRegion;
	public static TextureRegion nearStarRegion;
	public static TextureRegion farStarRegion;
	
	public static Texture blockTextures;
	public static TextureRegion baseBlockRegion;
	public static TextureRegion armorBlockRegion;
	public static TextureRegion energyBlockRegion;
	public static TextureRegion fullArmorBlockRegion;
	public static TextureRegion midArmorBlockRegion;
	public static TextureRegion lowArmorBlockRegion;
	public static TextureRegion potentialBlockRegion;
	public static TextureRegion bulletRegion;
	public static TextureRegion greenBulletRegion;
	public static TextureRegion yellowBulletRegion;
	public static TextureRegion multiplierBlockRegion;
	public static TextureRegion turretBaseRegion;
	public static TextureRegion turretTopRegion;
	
	
	public static Texture mainMenuTextures;
	public static TextureRegion playRegion;
	public static TextureRegion arrowRegion;
	public static TextureRegion helpRegion;
	public static TextureRegion rectangleRegion;

	public static Font font;

	public static Music song1;

	public static Sound explosionSound;
	public static Sound shootSound;

	
	public static void load(GLGame game){
		backgroundTextures = new Texture(game, "Backgrounds.png");
		backgroundRegion = new TextureRegion(backgroundTextures,  0, 0, 1333, 2000);
		nearStarRegion = new TextureRegion(backgroundTextures,  1334, 0, 1333, 2000);
		farStarRegion = new TextureRegion(backgroundTextures,  2668, 0, 1333, 2000);

		blockTextures = new Texture(game, "Blocks.png");
		baseBlockRegion = new TextureRegion(blockTextures, 0, 77, 25, 25);
		armorBlockRegion = new TextureRegion(blockTextures, 0, 51, 25, 25);
		lowArmorBlockRegion = new TextureRegion(blockTextures, 52, 51, 25, 25);
		midArmorBlockRegion = new TextureRegion(blockTextures, 78, 51, 25, 25);
		fullArmorBlockRegion = new TextureRegion(blockTextures, 26, 77, 25, 25);
		potentialBlockRegion = new TextureRegion(blockTextures, 78, 77, 25, 25);
		multiplierBlockRegion = new TextureRegion(blockTextures, 52, 77, 25, 25);
		bulletRegion = new TextureRegion(blockTextures,  102, 0, 5, 5);
		greenBulletRegion = new TextureRegion(blockTextures, 102, 6, 5, 5);
		yellowBulletRegion = new TextureRegion(blockTextures, 102, 12, 5, 5);
		turretBaseRegion =  new TextureRegion(blockTextures, 0, 0, 50, 50);
		turretTopRegion = new TextureRegion(blockTextures, 51, 0, 50, 50);
		energyBlockRegion = new TextureRegion(blockTextures, 26, 51, 25, 25);
		
		mainMenuTextures = new Texture(game, "MenuItems.png");
		playRegion = new TextureRegion(mainMenuTextures, 65, 121, 64, 37);
		arrowRegion = new TextureRegion(mainMenuTextures, 181, 121, 30, 30);
		font = new Font(mainMenuTextures, 0, 0, 16, 16, 20);
		helpRegion = new TextureRegion(mainMenuTextures, 0, 121, 64, 37);
		rectangleRegion = new TextureRegion(mainMenuTextures, 130, 121, 50, 25);

		song1 = game.getAudio().newMusic("Song1.mp3");
		song1.setLooping(true);
		song1.setVolume(0.5f);

		explosionSound = game.getAudio().newSound("Explosion.wav");
		shootSound = game.getAudio().newSound("Shoot.wav");


		Settings.load(game.getFileIO());
		PlayerSave.load(game.getFileIO());
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
