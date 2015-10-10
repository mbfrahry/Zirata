package th.zirata.EnemyShips;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Game.World;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Matthew on 10/3/2015.
 */
public class Hydra extends Enemy {


    int x;
    int y;
    int blockLevel;
    int[] blocksPerDirection;

    public Hydra(int blockLevel){
        super( blockLevel);
        constantVelocity = true;
        this.x = 150;
        this.y = 380;
        position = new Vector2(x, y);
        this.blockLevel = blockLevel;
        blocksPerDirection = new int[] {1,1,1};
        createHydra();
    }


    public void createHydra(){
        enemyBlocks.add(new ArmorBlock(x, y, blockLevel*10, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x+25, y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x-25, y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x, y-25, blockLevel*3, blockLevel));
        for(int i = 0; i < enemyBlocks.size(); i++){
            enemyBlocks.get(i).velocity.set(0, -10);
        }
    }

    public boolean checkDead(){
        if(enemyBlocks.size() == 0){
            return true;
        }

        return false;
    }

    public void update(float deltaTime, World world){
        for(int i = 0; i < enemyBlocks.size(); i++){
            Block currBlock = enemyBlocks.get(i);
            currBlock.position.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            //currBlock.bounds.lowerLeft.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            for (Vector2 v : currBlock.bounds.vertices){
                v.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            }
            currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);
            if(currBlock.checkDeath()){
                if(currBlock.getClass().equals(EnemyTurretBlock.class)){
                    EnemyTurretBlock eBlock = null;
                    //right
                    if(currBlock.origin.x < enemyBlocks.get(0).origin.x){
                        currBlock.health = currBlock.maxHealth;
                        blocksPerDirection[0] +=1;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x-25f*blocksPerDirection[0]*world.world_x_axis.x,
                                enemyBlocks.get(0).position.y - 25f*blocksPerDirection[0]*world.world_x_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x-25f*blocksPerDirection[0]*world.world_x_axis.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y - 25f*blocksPerDirection[0]*world.world_x_axis.y);
                    }
                    //left
                    else if(currBlock.origin.x > enemyBlocks.get(0).origin.x){
                        blocksPerDirection[1] +=1;
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x+25f*blocksPerDirection[1]*world.world_x_axis.x,
                                enemyBlocks.get(0).position.y + 25f*blocksPerDirection[1]*world.world_x_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x+25f*blocksPerDirection[1]*world.world_x_axis.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y + 25f*blocksPerDirection[1]*world.world_x_axis.y);
                    }
                    //middle
                    else{
                        blocksPerDirection[2] +=1;
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x - 25f*blocksPerDirection[2]*world.world_y_axis.x,
                                enemyBlocks.get(0).position.y-25f*blocksPerDirection[2]*world.world_y_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x-25f*blocksPerDirection[2]*world.world_y_axis.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y - 25f*blocksPerDirection[2]*world.world_y_axis.y);
                    }
                    eBlock.origin.set(currBlock.origin);
                    eBlock.velocity.set(currBlock.velocity);
                    //eBlock.rotateConstantVelocity(world.world_sin, world.world_cos, world.WORLD_MID_POINT);
                    eBlock.bounds.rotationAngle.set(currBlock.bounds.rotationAngle);
                    eBlock.bounds.setVertices();

                    enemyBlocks.add(eBlock);
                }
                else{
                    enemyBlocks.clear();
                }
            }
        }
    }
}