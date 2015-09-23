package th.zirata;

public class EnemyTurretBlock extends TurretBlock{

	float timeToFire;
	float initialTimeToFire;
	public static final int TURRET_WAITING = 2;
	public int bulletDamage;

	public EnemyTurretBlock(double[] info){
		this((float)info[0], (float)info[1], (int)info[2], (int)info[3]);
	}

	public EnemyTurretBlock(float x, float y, int health, int bulletDamage){
		super(x, y, health, 0, 1, 0);
		initialTimeToFire = 6;
		timeToFire = initialTimeToFire;
		super.state = TURRET_WAITING;
		this.bulletDamage = bulletDamage;
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
