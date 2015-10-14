package th.zirata.EnemyShips;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.Bullet;
import th.zirata.Blocks.Mine;
import th.zirata.Game.World;

/**
 * Created by Max Bauer on 10/12/2015.
 */
public class MineEnemy extends Enemy {

    float cooldown;
    float reloadTime;

    public MineEnemy(int enemyLevel) {
        super(enemyLevel);
        ArmorBlock aBlock = new ArmorBlock(160, 400, 10, enemyLevel);
        aBlock.velocity.set(60, -10);
        enemyBlocks.add(aBlock);
        cooldown = 1;
        reloadTime = 6;
    }

    public void update(float deltaTime, World world){
        cooldown += deltaTime;
        if(cooldown >= reloadTime){
            cooldown = 0;
            //TODO: Should probably switch this to action
            world.enemyManager.enemyBullets.add(new Mine(enemyBlocks.get(0).position.x, enemyBlocks.get(0).position.y, 0, 0, 30, 500));
        }
        int xMultiplier = 1;
        int yMultiplier = 1;

        if(enemyBlocks.get(0).position.x < 1 && enemyBlocks.get(0).velocity.x < 0){
            xMultiplier *= -1;
        }
        else if(enemyBlocks.get(0).position.x > 319 && enemyBlocks.get(0).velocity.x > 0){
            xMultiplier *= -1;
        }
        else if(enemyBlocks.get(0).position.y < 1 && enemyBlocks.get(0).velocity.y < 0){
            yMultiplier *= -1;
        }
        else if(enemyBlocks.get(0).position.y > 479 && enemyBlocks.get(0).velocity.y > 0){
            yMultiplier *= -1;
        }

        if(xMultiplier == -1 || yMultiplier == -1){
            for(Block b : enemyBlocks) {
                b.velocity.x *= xMultiplier;
                b.velocity.y *= yMultiplier;
            }
        }


        super.update(deltaTime, world);

    }


}
