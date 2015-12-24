package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.Bullet;
import th.zirata.Blocks.Mine;
import th.zirata.Game.Player;
import th.zirata.Game.World;

/**
 * Created by Max Bauer on 10/12/2015.
 */
public class MineEnemy extends Enemy {

    float cooldown;
    float reloadTime;
    float jumpDelay;

    public MineEnemy(int enemyLevel) {
        super(enemyLevel);
        ArmorBlock aBlock = new ArmorBlock(160, 400, 10, enemyLevel);
        aBlock.velocity.set(Player.playerSpeed);
        enemyBlocks.add(aBlock);
        cooldown = 2;
        reloadTime = 0;
        jumpDelay = 0;
        constantVelocity = true;
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
        }
    }

    public void update(float deltaTime, World world){
        if(cooldown > 0){
            //TODO: Should probably switch this to action
            jumpDelay += deltaTime;
            if(jumpDelay > .75f){
                jumpDelay = 0;
                cooldown--;
                world.enemyManager.enemyBullets.add(new Mine(enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 0, -10, 30, 500));

                //Teleport
                World.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 25f, 25f,.3f, 0f);
                World.popupManager.createSpriteExtra("sprite", "BaseBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 35f,35f,.2f, 0f);
                World.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 45f, 45f, .1f, 0f);
                teleportBlocks();
                World.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 25f, 25f,.3f, 0f);
                World.popupManager.createSpriteExtra("sprite", "BaseBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 35f, 35f, .2f, 0f);
                World.popupManager.createSpriteExtra("sprite", "PotentialBlock", enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 45f, 45f, .1f, 0f);
            }

        }
        else{
            reloadTime += deltaTime;
            if(reloadTime > 10){
                cooldown = 2;
                reloadTime = 0;
            }
        }

        super.update(deltaTime, world);


//        int xMultiplier = 1;
//        int yMultiplier = 1;
//
//        if(enemyBlocks.get(0).position.x < 1 && enemyBlocks.get(0).velocity.x < 0){
//            xMultiplier *= -1;
//        }
//        else if(enemyBlocks.get(0).position.x > 319 && enemyBlocks.get(0).velocity.x > 0){
//            xMultiplier *= -1;
//        }
//        else if(enemyBlocks.get(0).position.y < 1 && enemyBlocks.get(0).velocity.y < 0){
//            yMultiplier *= -1;
//        }
//        else if(enemyBlocks.get(0).position.y > 479 && enemyBlocks.get(0).velocity.y > 0){
//            yMultiplier *= -1;
//        }
//
//        if(xMultiplier == -1 || yMultiplier == -1){
//            for(Block b : enemyBlocks) {
//                b.velocity.x *= xMultiplier;
//                b.velocity.y *= yMultiplier;
//            }
//        }




    }


}
