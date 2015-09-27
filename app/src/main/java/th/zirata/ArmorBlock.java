package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;

public class ArmorBlock extends Block{


	public static String[] UpgradeAttributes = new String[]{"Health"};
	public float[] defaultValueArray = {20};
	public float[] upgradeValueArray = {20};
	public float[] maxValueLevelArray = {9999};

	int constructorArgLength = 4;

	public ArmorBlock(Vector2 position){
		this(position.x, position.y, 20, 1);
	}

	public ArmorBlock(double[] info){
		this((float) info[0], (float) info[1], (int) info[2], (int) info[4]);
	}

	public ArmorBlock(float x, float y, int health, int blockLevel){
		super(x, y, health, 0, blockLevel);
	}

	@Override
	public void action(World world) {

	}

	@Override
	public void update(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
	}

	@Override
	public void writeExtraInfo(JsonWriter writer) throws IOException {

	}

	@Override
	public String[] getUpgradableAttributes() {
		return UpgradeAttributes;
	}

	@Override
	public float[] getAttributeVals() {
		return new float[]{this.health};
	}

	@Override
	public float[] getUpgradeValues() {
		return upgradeValueArray;
	}

	@Override
	public void updateAttribute(int attributeIndex, float upgradeNum) {
		if(attributeIndex == 0){
			this.health += upgradeNum;
			this.maxHealth += upgradeNum;
		}
	}

	public boolean checkMaxAttributeLevel(int attributeIndex){
		int currLevel = getAttributeLevel(attributeIndex);
		if(currLevel < maxValueLevelArray[attributeIndex]){
			return false;
		}
		return true;
	}

	@Override
	public int getAttributeLevel(int attributeIndex) {
		float defaultVal = defaultValueArray[attributeIndex];
		float currVal = 0;
		if(attributeIndex == 0){
			currVal = this.maxHealth;
		}
		float delta = currVal - defaultVal;
		return (int) Math.abs((delta/upgradeValueArray[attributeIndex]));
	}

	public void multiply(float multiplier){
		this.health *= multiplier;
	}

	@Override
	public void fuseWith(Block b) {
		for(int i = 0; i < 1; i++){
			fuseLevels(i, b.getAttributeLevel(i));
		}
		blockLevel ++;
	}

	public void fuseLevels(int attIndex, int levelsAdded){
		while (!checkMaxAttributeLevel(attIndex) && levelsAdded > 0){
			if(attIndex == 0){
				health += upgradeValueArray[attIndex];
			}
			levelsAdded --;
		}
		health += levelsAdded*upgradeValueArray[0];
		maxHealth = health;
	}
}
