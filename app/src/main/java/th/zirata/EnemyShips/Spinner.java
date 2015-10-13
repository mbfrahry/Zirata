package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.gl.Vertices;
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
    float radius;
    float degreesPerSecond;
    public Vector2 shipMidPoint;
    Vector2 velocityNorm;

    public Spinner(int enemyLevel){
        super(enemyLevel);
        angularSpeed = new Vector2();
        degreesPerSecond = .7f;
        setAngularSpeed(degreesPerSecond);

        float[] atts = generateBlockAttributes();
        x = atts[0];
        y = atts[1];
        position = new Vector2(x, y);
        xVelocity = atts[2]/2;
        yVelocity = atts[3]/2;

        shipMidPoint = new Vector2(x, y);

        radius = 25;

        velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(velocityNorm, velocityNormX);
    }

    public void setupBlocks(Vector2 velocityNorm, Vector2 velocityNormX){
        Block nextBlock = null;
        if(enemyLevel == 1){
            //Create one armor block

            nextBlock = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
            enemyBlocks.add(nextBlock);
            //nextBlock = new ArmorBlock(x +10f*velocityNormX.x +10f*velocityNorm.x, y + 10f*velocityNormX.y + 10f*velocityNorm.y, 10, enemyLevel);
            nextBlock = new ArmorBlock(x +radius*velocityNorm.x, y + radius*velocityNorm.y, 10, enemyLevel);
            enemyBlocks.add(nextBlock);
        }

        for(int i = 0; i < enemyBlocks.size(); i++){
            Block firstBlock = enemyBlocks.get(0);
            firstBlock.velocity.add(xVelocity, yVelocity);
            firstBlock.bounds.rotationAngle.set(velocityNorm.x, velocityNorm.y);
            firstBlock.bounds.lowerLeft.set(firstBlock.position.x - 12f*velocityNorm.x - 12f*velocityNormX.x, firstBlock.position.y - 12f*velocityNorm.y - 12f*velocityNormX.y);
            firstBlock.bounds.setVertices();
        }



    }

    public void setAngularSpeed(float degreesPerSecond){
        this.degreesPerSecond = degreesPerSecond;
        angularSpeed.set((float)Math.cos(Math.toRadians(degreesPerSecond)), (float)Math.sin(Math.toRadians(degreesPerSecond)));
    }

    public void update(float deltaTime, World world){
        //TODO: Might need to take into account player rotation as well
        velocityNorm.rotate(angularSpeed.x, angularSpeed.y);
        if(world.moveLeft){
            velocityNorm.rotate(World.POS_COS_ANGLE, World.POS_SIN_ANGLE);
        }
        else if(world.moveRight){
            velocityNorm.rotate(World.POS_COS_ANGLE, World.NEG_SIN_ANGLE);
        }
        for(int i = 0; i < enemyBlocks.size(); i++){
            Block currBlock = enemyBlocks.get(i);
            currBlock.update(deltaTime);
            if(currBlock.getClass().equals(EnemyTurretBlock.class)) {
                shipMidPoint.set(currBlock.position.x, currBlock.position.y);
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
                //currBlock.rotateConstantVelocity(angularSpeed.y, angularSpeed.x, shipMidPoint);

                float xDiff = radius*velocityNorm.x + shipMidPoint.x - currBlock.position.x;
                float yDiff = radius*velocityNorm.y + shipMidPoint.y - currBlock.position.y;
                currBlock.position.add(xDiff, yDiff);
                for (Vector2 v : currBlock.bounds.vertices){
                    v.add(xDiff, yDiff);
                }

                if(currBlock.checkDeath()){
                    enemyBlocks.remove(i);
                    Assets.playSound(Assets.explosionSound);
                }
            }
            //currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);


            if(currBlock.position.x > 600 || currBlock.position.y > 650 || currBlock.position.x < -180 || currBlock.position.y < -180 && !constantVelocity) {
                enemyBlocks.remove(i);
            }
        }
    }
}
