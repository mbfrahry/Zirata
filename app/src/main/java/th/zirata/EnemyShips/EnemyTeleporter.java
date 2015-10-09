package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.Bullet;
import th.zirata.Game.World;

/**
 * Created by Matthew on 10/7/2015.
 */
public class EnemyTeleporter extends Enemy {

    float x;
    float y;
    float xVelocity;
    float yVelocity;
    float cooldown;
    float cooldownTime;

    int state;

    public static final int TELEPORT_READY = 0;
    public static final int TELEPORT_COOLING = 1;

    public EnemyTeleporter(int blockLevel){
        super( blockLevel);
        constantVelocity = true;

        float[] atts = generateBlockAttributes();
        x = 150;
        y = 380;
        xVelocity = 0;
        yVelocity = -10;
        cooldown = 10/blockLevel*2;
        cooldownTime = 0;
        state = TELEPORT_READY;

        if(enemyLevel == 1){
            //Create one armor block
            ArmorBlock aBlock = new ArmorBlock(x, y, 10, enemyLevel);
            enemyBlocks.add(aBlock);
        }
        for (Block b : enemyBlocks){
            b.velocity.add(xVelocity, yVelocity);
        }
    }

    public boolean bulletInRange(ArrayList<Bullet> bullets){
        for(Bullet bullet : bullets){
            for(Block block : enemyBlocks){
                if (block.position.dist(bullet.position) < 50) {
                    return true;
                }
            }
        }
        return false;
    }

    public void teleportBlocks(){
        for(Block b : enemyBlocks){
            b.position.x += 20;
            for (Vector2 v : b.bounds.vertices){
                v.x += 20;
            }
            state = TELEPORT_COOLING;
        }
    }

    public void update(float deltaTime, World world){
        super.update(deltaTime, world);
        if(state == TELEPORT_READY) {
            if (bulletInRange(world.playerBullets)) {
                teleportBlocks();
            }
        }
        else {
            cooldownTime += deltaTime;
            if(cooldownTime >= cooldown){
                state = TELEPORT_READY;
                cooldownTime = 0;
            }
        }
    }
}
