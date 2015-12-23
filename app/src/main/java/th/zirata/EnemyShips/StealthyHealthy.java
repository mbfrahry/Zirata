package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Game.Player;
import th.zirata.Game.World;
import th.zirata.Settings.Assets;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Max Bauer on 12/22/2015.
 */
public class StealthyHealthy extends Enemy{

    float x;
    float y;
    float xVelocity;
    float yVelocity;

    public StealthyHealthy(int enemyLevel){
        super(enemyLevel);
        float[] atts = generateBlockAttributes();
        x = atts[0];
        y = atts[1];
        position = new Vector2(x, y);
        xVelocity = atts[2]*1.5f;
        yVelocity = atts[3]*1.5f;

        Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(x, y, velocityNorm, velocityNormX);

    }

    public StealthyHealthy(int enemyLevel, float x, float y){
        super(enemyLevel);
        float[] atts = generateBlockAttributes(x, y);
        this.x = x;
        this.y = y;
        position = new Vector2(x, y);
        xVelocity = 0;
        yVelocity = -15;

        Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
        Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();
        setupBlocks(x, y, velocityNorm, velocityNormX);
    }

    public void setupBlocks(float x, float y, Vector2 velocityNorm, Vector2 velocityNormX){
            //Create one armor block
        //enemyBlocks.clear();

        EnemyTurretBlock aBlock1 = new EnemyTurretBlock(x, y, enemyLevel*3, enemyLevel);
        enemyBlocks.add(aBlock1);
        EnemyTurretBlock aBlock2 = new EnemyTurretBlock(x - 25f*velocityNormX.x - 20f*velocityNorm.x, y -25f*velocityNormX.y-20f*velocityNorm.y, enemyLevel*3, enemyLevel*3);
        enemyBlocks.add(aBlock2);
        EnemyTurretBlock aBlock3 = new EnemyTurretBlock(x + 25f*velocityNormX.x - 20f*velocityNorm.x, y+25f*velocityNormX.y-20f*velocityNorm.y, enemyLevel*3, enemyLevel*3);
        enemyBlocks.add(aBlock3);
        EnemyTurretBlock aBlock4 = new EnemyTurretBlock(x - 50f*velocityNormX.x - 40f*velocityNorm.x, y-50f*velocityNormX.y-40f*velocityNorm.y, enemyLevel*3, enemyLevel*3);
        enemyBlocks.add(aBlock4);
        EnemyTurretBlock aBlock5 = new EnemyTurretBlock(x + 50f*velocityNormX.x - 40f*velocityNorm.x, y+50f*velocityNormX.y-40f*velocityNorm.y, enemyLevel*3, enemyLevel*3);
        enemyBlocks.add(aBlock5);


        for (Block b : enemyBlocks){
            b.velocity.add(xVelocity, yVelocity);
            b.bounds.rotationAngle.set(velocityNorm.x, velocityNorm.y);
            b.bounds.lowerLeft.set(b.position.x - 12f*velocityNorm.x - 12f*velocityNormX.x, b.position.y - 12f*velocityNorm.y - 12f*velocityNormX.y);
            b.bounds.setVertices();
        }
    }

    public boolean checkCollision(Block pBlock, Block eBlock){
        if(OverlapTester.overlapPolygons(pBlock.bounds, eBlock.bounds)){
            return true;
        }
        return false;
    }

    public void update(float deltaTime, World world){


        for(int i = 0; i < enemyBlocks.size(); i++){
            Block currBlock = enemyBlocks.get(i);
            currBlock.position.add(currBlock.velocity.x* deltaTime, currBlock.velocity.y* deltaTime);

            for (Vector2 v : currBlock.bounds.vertices){
                v.add(currBlock.velocity.x* deltaTime, currBlock.velocity.y* deltaTime);
            }
            //currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);
            if(currBlock.checkDeath()){
                World.popupManager.createExplosion(currBlock.position.x, currBlock.position.y, 50);
                enemyBlocks.remove(i);
                Assets.playSound(Assets.explosionSound);
            }
            else if(currBlock.position.x > 600 || currBlock.position.y > 650 || currBlock.position.x < -180 || currBlock.position.y < -180 && !constantVelocity){
                enemyBlocks.remove(i);
            }
            for(int j = 0; j < world.player.playerBlocks.size(); j++){
                Block currPBlock = world.player.playerBlocks.get(j);
                if (checkCollision(currPBlock, currBlock)){

                    // Need to come up with new values
                    float[] atts = generateBlockAttributes();
                    x = atts[0];
                    y = atts[1];
                    position = new Vector2(x, y);
                    xVelocity = atts[2]*1.5f;
                    yVelocity = atts[3]*1.5f;

                    Vector2 velocityNorm = new Vector2(xVelocity, yVelocity).nor();
                    Vector2 velocityNormX =  new Vector2(xVelocity, yVelocity).rotate(90).nor();

                    for(Block b : enemyBlocks){
                        World.popupManager.createSpriteExtra("sprite", "PotentialBlock", b.position.x, b.position.y, 25f, 25f,.3f, 0f);
                        World.popupManager.createSpriteExtra("sprite", "BaseBlock", b.position.x, b.position.y, 35f,35f,.2f, 0f);
                        World.popupManager.createSpriteExtra("sprite", "PotentialBlock", b.position.x, b.position.y, 45f, 45f, .1f, 0f);
                    }


                    enemyBlocks.clear();
                    setupBlocks(x, y, velocityNorm, velocityNormX);

                }
            }
        }
    }
}
