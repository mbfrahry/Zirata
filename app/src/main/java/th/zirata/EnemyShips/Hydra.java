package th.zirata.EnemyShips;

import android.util.Log;

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
    float cooldown;
    int[] blocksPerDirection;
    Vector2 left;

    public Hydra(int blockLevel, float world_cos, float world_sin){
        super( blockLevel);
        constantVelocity = true;
        this.x = 160;
        this.y = 500;
        position = new Vector2(x, y);
        this.blockLevel = blockLevel;
        blocksPerDirection = new int[] {1,1,1};
        cooldown = .25f;
        Vector2 y_axis = new Vector2(160 - position.x, 240-position.y).nor();
        Vector2 x_axis = new Vector2(160 - position.x, 240-position.y).rotate(90).nor();
        left = x_axis;

        Log.d("cos/sin", world_cos + " " + world_sin);
        Log.d("xaxis", x_axis.x + " " + x_axis.y);
        Log.d("yaxis", y_axis.x + " " + y_axis.y);
        createHydra(x_axis, y_axis);
    }


//    public Hydra(int blockLevel, Vector2 world_x_axis, Vector2 world_y_axis, float world_cos, float world_sin){
//        super( blockLevel);
//        constantVelocity = true;
//        this.x = 150;
//        this.y = 450;
//        position = new Vector2(x, y);
//        this.blockLevel = blockLevel;
//        blocksPerDirection = new int[] {1,1,1};
//        createHydra(world_x_axis, world_y_axis, world_cos, world_sin);
//    }

    public void createHydra(Vector2 world_x_axis, Vector2 world_y_axis){
        enemyBlocks.add(new ArmorBlock(x, y, blockLevel*10, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(enemyBlocks.get(0).position.x+25f*world_x_axis.x,
                enemyBlocks.get(0).position.y + 25f*world_x_axis.y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(enemyBlocks.get(0).position.x-25f*world_x_axis.x,
                enemyBlocks.get(0).position.y - 25f*world_x_axis.y, blockLevel*3, blockLevel));
        enemyBlocks.add(new EnemyTurretBlock(enemyBlocks.get(0).position.x + 25f * world_y_axis.x,
                enemyBlocks.get(0).position.y + 25f * world_y_axis.y, blockLevel * 3, blockLevel));
        for(int i = 0; i < enemyBlocks.size(); i++){
            Block e = enemyBlocks.get(i);
            e.velocity.set(0, -10);
            e.bounds.rotationAngle.set(world_y_axis.x, world_y_axis.y);
            e.bounds.lowerLeft.set(e.position.x - 12f * world_x_axis.x - 12f * world_y_axis.x,
                    e.position.y - 12f*world_x_axis.y - 12f*world_y_axis.y);
            e.bounds.setVertices();
        }
    }

    public boolean checkDead(){
        return enemyBlocks.size() == 0;
    }

    public void update(float deltaTime, World world){
        cooldown += deltaTime;

        for(int i = 0; i < enemyBlocks.size(); i++){
            Block currBlock = enemyBlocks.get(i);
            currBlock.position.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            //currBlock.bounds.lowerLeft.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            for (Vector2 v : currBlock.bounds.vertices){
                v.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
            }
            //currBlock.bounds.rotationAngle.rotate(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);
            if(i == 1){
                left.set(enemyBlocks.get(1).position.x - enemyBlocks.get(0).position.x, enemyBlocks.get(1).position.y - enemyBlocks.get(0).position.y);
                left.nor();
            }
            if(currBlock.checkDeath() && cooldown > .1){
                cooldown = 0;
                if(currBlock.getClass().equals(EnemyTurretBlock.class)){
                    EnemyTurretBlock eBlock;
                    //right
                    float dir = checkDirection(currBlock);
                    if(dir < -1){
                        currBlock.health = currBlock.maxHealth;
                        blocksPerDirection[0] +=1;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x-25f*blocksPerDirection[0]*left.x,
                                enemyBlocks.get(0).position.y - 25f*blocksPerDirection[0]*left.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x-25f*blocksPerDirection[0]*left.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y - 25f*blocksPerDirection[0]*left.y);
                    }
                    //left
                    else if(dir > 1){
                        blocksPerDirection[1] +=1;
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x+25f*blocksPerDirection[1]*left.x,
                                enemyBlocks.get(0).position.y + 25f*blocksPerDirection[1]*left.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x+25f*blocksPerDirection[1]*left.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y + 25f*blocksPerDirection[1]*left.y);
                    }
                    //middle
                    else{
                        blocksPerDirection[2] +=1;
                        left.rotate(0, 1);
                        currBlock.health = currBlock.maxHealth;
                        eBlock = new EnemyTurretBlock(enemyBlocks.get(0).position.x - 25f*blocksPerDirection[2]*left.x,
                                enemyBlocks.get(0).position.y-25f*blocksPerDirection[2]*left.y, blockLevel*3, blockLevel);
                        eBlock.bounds.lowerLeft.set(enemyBlocks.get(0).bounds.lowerLeft.x-25f*blocksPerDirection[2]*left.x,
                                enemyBlocks.get(0).bounds.lowerLeft.y - 25f*blocksPerDirection[2]*left.y);
                        left.rotate(0, -1);
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

    public float checkDirection(Block hitBlock){

        float xdir = hitBlock.position.x - enemyBlocks.get(0).position.x;
        float ydir = hitBlock.position.y - enemyBlocks.get(0).position.y;

        return xdir*left.x + ydir*left.y;


    }
}
