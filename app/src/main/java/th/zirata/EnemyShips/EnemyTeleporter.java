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
        xVelocity = 0;
        yVelocity = -30;
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
            b.bounds.rotationAngle.set(0, 1);
            b.bounds.lowerLeft.set(b.position.x + 12f, b.position.y - 12f);
            b.bounds.setVertices();
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
        int teleX;
        int teleY;

        teleX = -99 + rand.nextInt(200);
        teleY = -99 + rand.nextInt(200);

        float newX = enemyBlocks.get(0).position.x + teleX;
        float newY = enemyBlocks.get(0).position.y + teleY;

        if(newX > 120 && newX < 200){
            if(newY > 200 && newY < 280){
                teleX += 225;
                teleY += 225;
            }
        }

        for(Block b : enemyBlocks){
            b.position.x += teleX;
            b.position.y += teleY;
            for (Vector2 v : b.bounds.vertices){
                v.x += teleX;
                v.y += teleY;
            }
            state = TELEPORT_COOLING;
        }
    }

    public void update(float deltaTime, World world){
        super.update(deltaTime, world);
        if(state == TELEPORT_READY) {
            if (bulletInRange(World.playerBullets)) {
                world.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 25f, 25f,.3f, 0f);
                world.popupManager.createSpriteExtra("sprite", "BaseBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 35f,35f,.2f, 0f);
                world.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 45f, 45f, .1f, 0f);
                teleportBlocks();
                world.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 25f, 25f,.3f, 0f);
                world.popupManager.createSpriteExtra("sprite", "BaseBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 35f,35f,.2f, 0f);
                world.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 45f,45f,.1f, 0f);
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
