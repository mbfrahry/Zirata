package th.zirata;

import java.util.ArrayList;

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
		super(x, y, health, energyCost);
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
	
	public void action(){
		if(state == TURRET_READY && timeToNextBullet < 0){
			World.PLAYER_BULLETS.add(new Bullet(position.x, position.y, lastTouch.x, lastTouch.y, bulletDamage));
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
		for(int i = 0; i < World.PLAYER_BULLETS.size(); i++){
			World.PLAYER_BULLETS.get(i).update(deltaTime);
			if(World.PLAYER_BULLETS.get(i).outOfBounds()){
				World.PLAYER_BULLETS.remove(i);
			}
		}
	}
}
