package th.zirata;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matthew on 10/3/2015.
 */
public class Hydra extends Enemy{


    int x;
    int y;
    int blockLevel;
    int[] blocksPerDirection;

    public Hydra(int blockLevel){
        super(-1, blockLevel);
        this.x = 100;
        this.y = 100;
        this.blockLevel = blockLevel;
        blocksPerDirection = new int[] {1,1,1};
        createHydra();
    }


    public void createHydra(){
        enemyBlocks.add(new ArmorBlock(x, y, blockLevel*10, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x+25, y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x-25, y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(x, y+25, blockLevel*3, blockLevel));
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
            currBlock.bounds.lowerLeft.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);
            if(currBlock.checkDeath()){
                if(currBlock.getClass().equals(EnemyTurretBlock.class)){
                    EnemyTurretBlock eBlock = null;
                    if(currBlock.origin.x < enemyBlocks.get(0).origin.x){
                        currBlock.health = currBlock.maxHealth;
                        blocksPerDirection[0] +=1;
                        eBlock = new EnemyTurretBlock(currBlock.position.x-12.5f*blocksPerDirection[0]*world.world_x_axis.x,
                                currBlock.position.y - 12.5f*blocksPerDirection[0]*world.world_x_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(currBlock.bounds.lowerLeft.x-12.5f*blocksPerDirection[0]*world.world_x_axis.x,
                                currBlock.bounds.lowerLeft.y - 12.5f*blocksPerDirection[0]*world.world_x_axis.y);
                    }
                    else if(currBlock.origin.x > enemyBlocks.get(0).origin.x){
                        blocksPerDirection[1] +=1;
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(currBlock.position.x+12.5f*blocksPerDirection[1]*world.world_x_axis.x,
                                currBlock.position.y + 12.5f*blocksPerDirection[1]*world.world_x_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(currBlock.bounds.lowerLeft.x+12.5f*blocksPerDirection[1]*world.world_x_axis.x,
                                currBlock.bounds.lowerLeft.y + 12.5f*blocksPerDirection[1]*world.world_x_axis.y);
                    }
                    else{
                        blocksPerDirection[2] +=1;
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(currBlock.position.x + 12.5f*blocksPerDirection[2]*world.world_y_axis.x,
                                currBlock.position.y+12.5f*blocksPerDirection[2]*world.world_y_axis.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(currBlock.bounds.lowerLeft.x+12.5f*blocksPerDirection[2]*world.world_y_axis.x,
                                currBlock.bounds.lowerLeft.y + 12.5f*blocksPerDirection[2]*world.world_y_axis.y);
                    }
                    eBlock.velocity.set(currBlock.velocity);
                    //eBlock.rotateConstantVelocity(world.world_sin, world.world_cos, world.WORLD_MID_POINT);
                    eBlock.bounds.rotationAngle.set(currBlock.bounds.rotationAngle);

                    enemyBlocks.add(eBlock);
                }
                else{
                    enemyBlocks.clear();
                }
            }
        }
    }
}
