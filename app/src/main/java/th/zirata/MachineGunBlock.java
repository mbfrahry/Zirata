package th.zirata;

import java.util.ArrayList;

public class MachineGunBlock extends Block {

	ArrayList<Bullet> bullets;
	int maxBullets;
	int numBullets;
	float reloadTime;
	float currTime;
	float timeToNextBullet;
	float initialTimeToNextBullet;
	
	public static final int TURRET_READY = 0;
	public static final int TURRET_RELOADING = 1;
	
	int state;
	
	public MachineGunBlock(float x, float y, int health){
		super(x, y, health);
		bullets = new ArrayList<Bullet>();
		maxBullets = 10;
		numBullets = 0;
		
		initialTimeToNextBullet = 0.25f;
		timeToNextBullet = initialTimeToNextBullet;
		reloadTime = 3;
		currTime = 0;
		
		state = TURRET_READY;
	}
	
	public void action(){
		if(state == TURRET_READY && timeToNextBullet < 0){
			bullets.add(new Bullet(position.x, position.y, lastTouch.x, lastTouch.y));
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
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).update(deltaTime);
			if(bullets.get(i).outOfBounds()){
				bullets.remove(i);
			}
		}
	}
}
