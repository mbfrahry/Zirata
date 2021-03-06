package th.zirata.Blocks;

import android.util.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;

import th.zirata.Game.World;

//TODO
//This whole block needs to be redone.
public class MachineGunBlock extends Block {

	ArrayList<Bullet> bullets;
	int maxBullets;
	int numBullets;
	float reloadTime;
	float currTime;
	float timeToNextBullet;
	float initialTimeToNextBullet;
	int bulletDamage;

	public static final int TURRET_READY = 0;
	public static final int TURRET_RELOADING = 1;
	
	int state;

	public MachineGunBlock(double[] info){
		this((float)info[0], (float)info[1], (int)info[2], (int)info[3]);
	}

	public MachineGunBlock(float x, float y, int health, int energyCost){
		super(x, y, health, energyCost, 1);
		bullets = new ArrayList<Bullet>();
		maxBullets = 10;
		numBullets = 0;
		bulletDamage = 10;
		
		initialTimeToNextBullet = 0.25f;
		timeToNextBullet = initialTimeToNextBullet;
		reloadTime = 3;
		currTime = 0;
		
		state = TURRET_READY;
	}

	@Override
	public void action(World world) {

	}

	public void action(){
		if(state == TURRET_READY && timeToNextBullet < 0){
			World.playerBullets.add(new Bullet(position.x, position.y, lastTouch.x, lastTouch.y, bulletDamage, 1f));
			numBullets++;
			if(numBullets >= maxBullets){
				state = TURRET_RELOADING;
			}
			timeToNextBullet = initialTimeToNextBullet;
		}
	}
	
	public void update(float deltaTime){
		timeToNextBullet -= deltaTime;
		if(state == TURRET_RELOADING){
			currTime+= deltaTime;
			if(currTime >= reloadTime){
				state = TURRET_READY;
				currTime = 0;
				numBullets = 0;
			}
		}
		for(int i = 0; i < World.playerBullets.size(); i++){
			World.playerBullets.get(i).update(deltaTime);
			if(World.playerBullets.get(i).outOfBounds()){
				World.playerBullets.remove(i);
			}
		}
	}

	@Override
	public void writeExtraInfo(JsonWriter writer) throws IOException {

	}

	@Override
	public String[] getUpgradableAttributes() {
		return new String[0];
	}

	@Override
	public float[] getAttributeVals() {
		return new float[0];
	}

	@Override
	public float[] getUpgradeValues() {
		return new float[0];
	}

	@Override
	public void updateAttribute(int attributeIndex, float upgradeNum) {

	}
	public int getAttributeLevel(int attributeIndex) {
		return 0;
	}

	@Override
	public void fuseWith(Block b) {

	}

	@Override
	public void fuseLevels(int attIndex, int levelsAdded) {

	}

	public boolean checkMaxAttributeLevel(int attributeIndex){
		return true;
	}
}
