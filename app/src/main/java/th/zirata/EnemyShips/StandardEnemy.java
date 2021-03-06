package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;

/**
 * Created by Max Bauer on 10/7/2015.
 */
public class StandardEnemy extends Enemy {

    float x;
    float y;
    float xVelocity;
    float yVelocity;

    public StandardEnemy(int enemyLevel){
        super(enemyLevel);
        float[] atts = generateBlockAttributes();
        x = atts[0];
        y = atts[1];
        position = new Vector2(x, y);
        xVelocity = atts[2];
        yVelocity = atts[3];

        Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(velocityNorm, velocityNormX);

    }

    public StandardEnemy(int enemyLevel, float x, float y){
        super(enemyLevel);
        float[] atts = generateBlockAttributes(x, y);
        this.x = x;
        this.y = y;
        position = new Vector2(x, y);
        xVelocity = atts[2];
        yVelocity = atts[3];

        Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(velocityNorm, velocityNormX);
    }

    public void setupBlocks(Vector2 velocityNorm, Vector2 velocityNormX){
        if(enemyLevel == 1){
            //Create one armor block
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }
        else if(enemyLevel == 2){
            //Create armor or turret
            Block nextBlock;
            if(Math.random() < .75){
                nextBlock = new ArmorBlock(x, y, 10, enemyLevel);
            }
            else{
                nextBlock = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
            }
            enemyBlocks.add(nextBlock);
        }
        else if(enemyLevel == 3){
            //Create 2
            Block nextBlock;
            if(Math.random() < .5){
                nextBlock = new ArmorBlock(x, y, 10, enemyLevel);
            }
            else{
                nextBlock = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
            }
            enemyBlocks.add(nextBlock);
        }
        else if(enemyLevel == 4){
            //create 3
            Block nextBlock;
            if(Math.random() < .25){
                nextBlock = new ArmorBlock(x, y, 10, enemyLevel);
            }
            else{
                nextBlock = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
            }
            enemyBlocks.add(nextBlock);
        }
        else{
            //create 4
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }

        for (Block b : enemyBlocks){
            b.velocity.add(xVelocity, yVelocity);
            b.bounds.rotationAngle.set(velocityNorm.x, velocityNorm.y);
            b.bounds.lowerLeft.set(b.position.x - 12f*velocityNorm.x - 12f*velocityNormX.x, b.position.y - 12f*velocityNorm.y - 12f*velocityNormX.y);
            b.bounds.setVertices();
        }
    }
}
