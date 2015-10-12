package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.gl.Vertices;
import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Game.World;
import th.zirata.Settings.Assets;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Max Bauer on 10/11/2015.
 */
public class SwirlyWhirly extends Enemy {

    Vector2 angularSpeed;
    float degreesPerSecond;
    float radius;
    Vector2 worldMidpoint;
    int loops;
    boolean movingIn;
    boolean movingOut;
    boolean isPaused;
    float timeInLoop;
    int enemiesCreated;

    public SwirlyWhirly(int enemyLevel) {
        super(enemyLevel);
        angularSpeed = new Vector2();
        degreesPerSecond = .5f;
        setAngularSpeed(degreesPerSecond);
        position = new Vector2(160,  390);
        enemyBlocks.add(new ArmorBlock(position.x, position.y, 10, 1));
        worldMidpoint = new Vector2(160, 240);
        loops = 0;
        movingOut = false;
        movingIn = false;
        isPaused = false;
        timeInLoop = 0;
        enemiesCreated = 0;
    }

    public void update(float deltaTime, World world){
        timeInLoop += deltaTime;
        for (Block b : enemyBlocks){
            b.rotate(angularSpeed.y, angularSpeed.x, worldMidpoint);
            if(b.checkDeath()){
                enemyBlocks.remove(b);
                Assets.playSound(Assets.explosionSound);
            }
        }
        if(movingIn){
            adjust(.5f, deltaTime);
            if(timeInLoop > 1){
                movingIn = false;
                isPaused = true;
                timeInLoop = 0;
            }
        }
        else if(movingOut){
            adjust(-.5f, deltaTime);
            if(timeInLoop > 1){
                movingOut = false;
                timeInLoop = 0;
                setAngularSpeed(.5f);
            }
        }
        else if(isPaused){
            if(timeInLoop > .75){
                //world.enemyManager.generateEnemy(EnemySettings.STANDARD_ENEMY, enemyLevel, enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y);
                movingOut = true;
                isPaused = false;
                timeInLoop = 0;
            }
        }
        else{
            if(timeInLoop > 2){
                //setAngularSpeed(degreesPerSecond + .4f);
                timeInLoop = 0;
                loops +=1;
            }
            if(loops % 4 == 0 && loops != 0){
                world.enemyManager.generateEnemy(EnemySettings.STANDARD_ENEMY, enemyLevel, enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y);
                enemiesCreated += 1;
                loops = 0;
            }
            if(enemiesCreated == 2){
                movingIn = true;
                setAngularSpeed(.001f);
                timeInLoop = 0;
                loops = 0;
                enemiesCreated = 0;
            }
        }


    }

    public void adjust(float multipler, float deltaTime){
        for(Block b : enemyBlocks){
            b.position.add(multipler*deltaTime*(160-b.position.x), multipler*deltaTime*(240-b.position.y));
            for( Vector2 v : b.bounds.vertices){
                v.add(multipler*deltaTime*(160-b.position.x), multipler*deltaTime*(240-b.position.y));
            }
        }

    }

    public void setAngularSpeed(float degreesPerSecond){
        this.degreesPerSecond = degreesPerSecond;
        angularSpeed.set((float)Math.cos(Math.toRadians(degreesPerSecond)), (float)Math.sin(Math.toRadians(degreesPerSecond)));
    }
}
