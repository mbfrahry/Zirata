package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Block extends DynamicGameObject {

	public static final float BLOCK_WIDTH = 24;
	public static final float BLOCK_HEIGHT = 24;
	int health;
	int maxHealth;
	int energyCost;
	Vector2 lastTouch;
	boolean active;

	int constructorArgLength;

	ArrayList<String> attributes;
	int maxAttributeNum;

	public Block(float x, float y, int health, int energyCost){
		super(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
		this.health = health;
		this.maxHealth = health;
		this.energyCost = energyCost;
		lastTouch = new Vector2(x, y + BLOCK_HEIGHT/2);
		active = false;

		constructorArgLength = 4;

		attributes = new ArrayList<String>();
		attributes.add("health");
		maxAttributeNum = 5;
	}

	public abstract void action(World world);
	
	public abstract void update(float deltaTime);

	public abstract void writeExtraInfo(JsonWriter writer) throws IOException;

	public abstract String[] getUpgradableAttributes();

	public abstract float[] getAttributeVals();

	public abstract float[] getUpgradeValues();

	public abstract void updateAttribute(int attributeIndex, float upgradeNum);

	public abstract int getAttributeLevel(int attributeIndex);

	public boolean checkDeath(){
		if(health <= 0){
			return true;
		}
		return false;
	}

	public ArrayList<Block> grabAdjacentBlocks(ArrayList<Block> blocks){
		ArrayList<Block> adjacentBlocks = new ArrayList<Block>();
		for(int i = 0; i < blocks.size(); i++){
			Block currBlock = blocks.get(i);
			boolean toAdd = false;
			if(position.y + 25 == currBlock.position.y && position.x == currBlock.position.x){
				toAdd = true;
			}

			else if(position.x + 25 == currBlock.position.x && position.y == currBlock.position.y){
				toAdd = true;
			}

			else if(position.y - 25 == currBlock.position.y && position.x == currBlock.position.x){
				toAdd = true;
			}

			else if(position.x - 25 == currBlock.position.x && position.y == currBlock.position.y){
				toAdd = true;
			}
			if (toAdd){
				adjacentBlocks.add(currBlock);
			}

		}
		return adjacentBlocks;
	}

	public void multiply(float multiplier){

	}
}
