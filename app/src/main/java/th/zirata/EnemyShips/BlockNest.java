package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.Bullet;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Game.Player;
import th.zirata.Game.World;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Max Bauer on 12/23/2015.
 */
public class BlockNest extends Enemy {

    float x;
    float y;
    float xVelocity;
    float yVelocity;
    float cooldown;
    float cooldownTime;

    int state;

    public static final int SPAWN_READY = 0;
    public static final int SPAWN_COOLING = 1;

    public BlockNest(int blockLevel){
        super( blockLevel);
        constantVelocity = true;

        //float[] atts = generateBlockAttributes();
        x = 12 + rand.nextFloat()*300;
        y = 12 + rand.nextFloat()*450;
        if(x > 140 && x < 180){
            if(y > 220 && y < 260){
                x += 75;
                y += 75;
            }
        }
        position = new Vector2(x, y);
        xVelocity = Player.playerSpeed.x;
        yVelocity = Player.playerSpeed.y;
        cooldown = 10/blockLevel*2;
        cooldownTime = 3;
        state = SPAWN_COOLING;

        if(enemyLevel == 1){
            //Create one armor block
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x+25, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x, y+25, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x+25, y+25, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }
        for (Block b : enemyBlocks){
            b.velocity.add(xVelocity, yVelocity);
            b.bounds.rotationAngle.set(0, 1);
            b.bounds.lowerLeft.set(b.position.x + 12f, b.position.y - 12f);
            b.bounds.setVertices();
        }
    }

    public BlockNest(int blockLevel, float x, float y){
        super( blockLevel);
        constantVelocity = true;

        //float[] atts = generateBlockAttributes();
        this.x = x;
        this.y = y;
        position = new Vector2(x, y);
        xVelocity = Player.playerSpeed.x;
        yVelocity = Player.playerSpeed.y;
        cooldown = 10/blockLevel*2;
        cooldownTime = 0;
        state = SPAWN_READY;

        if(enemyLevel == 1){
            //Create one armor block
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x+25, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x, y+25, 10, enemyLevel);
            enemyBlocks.add(aBlock);
            aBlock = new ArmorBlock(x+25, y+25, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }
        for (Block b : enemyBlocks){
            b.velocity.add(xVelocity, yVelocity);
            b.bounds.rotationAngle.set(0, 1);
            b.bounds.lowerLeft.set(b.position.x + 12f, b.position.y - 12f);
            b.bounds.setVertices();
        }
    }

    public void update(float deltaTime, World world){
        super.update(deltaTime, world);
        if(state == SPAWN_READY && enemyBlocks.size() > 0) {
            world.enemyManager.generateEnemy(EnemySettings.STANDARD_ENEMY, 2, enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y);
            state = SPAWN_COOLING;
        }
        else {
            cooldownTime += deltaTime;
            if(cooldownTime >= cooldown){
                state = SPAWN_READY;
                cooldownTime = 0;
            }
        }
    }

}
