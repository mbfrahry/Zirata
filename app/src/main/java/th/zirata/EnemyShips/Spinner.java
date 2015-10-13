package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Game.World;
import th.zirata.Settings.Assets;

/**
 * Created by Matthew on 10/12/2015.
 */
public class Spinner extends Enemy {

    float x;
    float y;
    float xVelocity;
    float yVelocity;
    Vector2 angularSpeed;
    float degreesPerSecond;
    Vector2 shipMidPoint;

    public Spinner(int enemyLevel){
        super(enemyLevel);
        angularSpeed = new Vector2();
        degreesPerSecond = .5f;
        setAngularSpeed(degreesPerSecond);

        float[] atts = generateBlockAttributes();
        x = atts[0];
        y = atts[1];
        position = new Vector2(x, y);
        xVelocity = atts[2];
        yVelocity = atts[3];

        shipMidPoint = new Vector2(x+12, y+12);

        Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(velocityNorm, velocityNormX);
    }

    public void setupBlocks(Vector2 velocityNorm, Vector2 velocityNormX){
        Block nextBlock = null;
        if(enemyLevel == 1){
            //Create one armor block

            nextBlock = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
            enemyBlocks.add(nextBlock);
            nextBlock = new ArmorBlock(x, y+36, 10, enemyLevel);
            enemyBlocks.add(nextBlock);
        }

        Block firstBlock = enemyBlocks.get(0);
        firstBlock.velocity.add(xVelocity, yVelocity);
        firstBlock.bounds.rotationAngle.set(velocityNorm.x, velocityNorm.y);
        firstBlock.bounds.lowerLeft.set(firstBlock.position.x - 12f*velocityNorm.x - 12f*velocityNormX.x, firstBlock.position.y - 12f*velocityNorm.y - 12f*velocityNormX.y);
        firstBlock.bounds.setVertices();


    }

    public void setAngularSpeed(float degreesPerSecond){
        this.degreesPerSecond = degreesPerSecond;
        angularSpeed.set((float)Math.cos(Math.toRadians(degreesPerSecond)), (float)Math.sin(Math.toRadians(degreesPerSecond)));
    }

    public void update(float deltaTime, World world){
        for(int i = 0; i < enemyBlocks.size(); i++){
            Block currBlock = enemyBlocks.get(i);
            if(currBlock.getClass().equals(EnemyTurretBlock.class)) {
                shipMidPoint.set(currBlock.position.x +12, currBlock.position.y+12);
                currBlock.position.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);

                for (Vector2 v : currBlock.bounds.vertices) {
                    v.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
                }

                if(currBlock.checkDeath()){
                    if(enemyBlocks.size() == 1) {
                        enemyBlocks.remove(i);
                        Assets.playSound(Assets.explosionSound);
                    }
                    else{
                        currBlock.health = currBlock.maxHealth;
                    }
                }
            }
            else{
                currBlock.rotate(angularSpeed.y, angularSpeed.x, shipMidPoint);
                if(currBlock.checkDeath()){
                    enemyBlocks.remove(i);
                    Assets.playSound(Assets.explosionSound);
                }
            }
            //currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);

            if(currBlock.position.x > 600 || currBlock.position.y > 650 || currBlock.position.x < -180 || currBlock.position.y < -180 && !constantVelocity) {
                enemyBlocks.remove(i);
            }
        }
    }
}
