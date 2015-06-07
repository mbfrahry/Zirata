package th.zirata;

public class EnemyTurretBlock extends TurretBlock{

	float timeToFire;
	float initialTimeToFire;
	public static final int TURRET_WAITING = 2;
	
	
	public EnemyTurretBlock(float x, float y, int health){
		super(x, y, health, 0);
		initialTimeToFire = 6;
		timeToFire = initialTimeToFire;
		super.state = TURRET_WAITING;
	}
	
	public void resetBlock(){
		super.state = TURRET_WAITING;
		timeToFire = initialTimeToFire;
	}
	
	public void update(float deltaTime){
		super.update(deltaTime);
		timeToFire -= deltaTime;
		if(timeToFire < 0){
			super.state = TURRET_READY;
		}
	}
	
}
