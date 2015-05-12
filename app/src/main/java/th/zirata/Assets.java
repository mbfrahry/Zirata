package th.zirata;

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
	public static TextureRegion fullArmorBlockRegion;
	public static TextureRegion midArmorBlockRegion;
	public static TextureRegion lowArmorBlockRegion;
	public static TextureRegion turretBlockRegion;
	public static TextureRegion emptyTurretBlockRegion;
	public static TextureRegion potentialBlockRegion;
	public static TextureRegion bulletRegion;
	public static TextureRegion machineGunBlockRegion;
	public static TextureRegion turretBaseRegion;
	public static TextureRegion turretTopRegion;
	
	
	public static Texture mainMenuTextures;
	public static TextureRegion playRegion;
	public static TextureRegion arrowRegion;
	
	public static Font font;
	
	
	
	public static void load(GLGame game){
		backgroundTextures = new Texture(game, "Backgrounds.png");
		backgroundRegion = new TextureRegion(backgroundTextures,  0, 0, 1333, 2000);
		nearStarRegion = new TextureRegion(backgroundTextures,  1334, 0, 1333, 2000);
		farStarRegion = new TextureRegion(backgroundTextures,  2668, 0, 1333, 2000);
		
		blockTextures = new Texture(game, "Blocks.png");
		baseBlockRegion = new TextureRegion(blockTextures, 102, 26, 25, 25);		
		armorBlockRegion = new TextureRegion(blockTextures, 102, 0, 25, 25);		
		lowArmorBlockRegion = new TextureRegion(blockTextures, 26, 51, 25, 25);
		midArmorBlockRegion = new TextureRegion(blockTextures, 26, 77, 25, 25);
		fullArmorBlockRegion = new TextureRegion(blockTextures, 0, 77, 25, 25);
		turretBlockRegion = new TextureRegion(blockTextures, 78, 52, 25, 25);
		emptyTurretBlockRegion = new TextureRegion(blockTextures, 0, 51, 25, 25);
		potentialBlockRegion = new TextureRegion(blockTextures, 52, 77, 25, 25);
		machineGunBlockRegion = new TextureRegion(blockTextures, 52, 51, 25, 25);
		bulletRegion = new TextureRegion(blockTextures, 78, 78, 5, 5);
		turretBaseRegion =  new TextureRegion(blockTextures, 0, 0, 50, 50);
		turretTopRegion = new TextureRegion(blockTextures, 51, 0, 50, 50);
		
		mainMenuTextures = new Texture(game, "MenuItems.png");
		playRegion = new TextureRegion(mainMenuTextures, 0, 121, 64, 37);
		arrowRegion = new TextureRegion(mainMenuTextures, 65, 121, 30, 30);
		font = new Font(mainMenuTextures, 0, 0, 16, 16, 20);
		
		Settings.load(game.getFileIO());
		PlayerSave.load(game.getFileIO());
	}
	
	public static void reload(){
		backgroundTextures.reload();
		blockTextures.reload();
		mainMenuTextures.reload();
	}
}
