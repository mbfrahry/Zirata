package th.zirata.EnemyShips;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyBlocks.Enemy;

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
        xVelocity = atts[2];
        yVelocity = atts[3];

        if(enemyLevel == 1){
            //Create one armor block
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }
        else if(enemyLevel == 2){
            //Create armor or turret
        }
        else if(enemyLevel == 3){
            //Create 2
        }
        else if(enemyLevel == 4){
            //create 3
        }
        else{
            //create 4
        }

        for (Block b : enemyBlocks){
            b.velocity.set(xVelocity, yVelocity);
        }
    }




}
