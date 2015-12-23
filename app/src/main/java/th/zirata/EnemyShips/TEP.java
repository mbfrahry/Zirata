package th.zirata.EnemyShips;

import android.util.Log;

import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Blocks.EnergyBlock;
import th.zirata.Game.Player;
import th.zirata.Game.World;

/**
 * Created by Max Bauer on 12/21/2015.
 */
public class TEP extends Enemy {
    float x;
    float y;
    int blockLevel;
    float cooldown;
    Vector2 left;

    public TEP(int blockLevel, float world_cos, float world_sin){
        super( blockLevel);
        constantVelocity = true;
        float[] atts = generateBlockAttributes();
        this.x = atts[0];
        this.y = atts[1];
        position = new Vector2(x, y);
        this.blockLevel = blockLevel;
        cooldown = .25f;
        Vector2 y_axis = new Vector2(160 - position.x, 240-position.y).nor();
        Vector2 x_axis = new Vector2(160 - position.x, 240-position.y).rotate(90).nor();
        left = x_axis;

        createTEP(x_axis, y_axis);
    }

    public void createTEP(Vector2 world_x_axis, Vector2 world_y_axis){
        for(int i = 0; i < 6; i++){
            enemyBlocks.add(new ArmorBlock(x + i * 25f*world_x_axis.x, y + i*25f*world_x_axis.y, blockLevel * 10, blockLevel));
            enemyBlocks.add(new ArmorBlock(x + i * 25f*world_x_axis.x + 52f*world_y_axis.x, y + i * 25f*world_x_axis.y + 52f*world_y_axis.y, blockLevel * 10, blockLevel));
        }
        enemyBlocks.add(new ArmorBlock(x  + 26f*world_y_axis.x, y + 26f*world_y_axis.y, blockLevel * 10, blockLevel));
        enemyBlocks.add(new EnergyBlock(x + 25f*world_x_axis.x  + 26f*world_y_axis.x, y + 25f*world_x_axis.y + 26f*world_y_axis.y, 10,  blockLevel * 10, blockLevel, 10));

        for(int i = 0; i < enemyBlocks.size(); i++){
            Block e = enemyBlocks.get(i);
            e.velocity.set(Player.playerSpeed.x, Player.playerSpeed.y);
            e.bounds.rotationAngle.set(world_y_axis.x, world_y_axis.y);
            e.bounds.lowerLeft.set(e.position.x - 12f * world_x_axis.x - 12f * world_y_axis.x,
                    e.position.y - 12f * world_x_axis.y - 12f * world_y_axis.y);
            e.bounds.setVertices();
        }
    }

    public boolean checkDead(){
        return enemyBlocks.size() == 0;
    }

    public void update(float deltaTime, World world) {

        for (int i = 0; i < enemyBlocks.size(); i++) {
                Block currBlock = enemyBlocks.get(i);
                currBlock.position.add(Player.playerSpeed.x * deltaTime, Player.playerSpeed.y * deltaTime);
                for (Vector2 v : currBlock.bounds.vertices) {
                    v.add(Player.playerSpeed.x * deltaTime, Player.playerSpeed.y * deltaTime);
                }
                currBlock.update(deltaTime);

                if (i == 1) {
                left.set(enemyBlocks.get(1).position.x - enemyBlocks.get(0).position.x, enemyBlocks.get(1).position.y - enemyBlocks.get(0).position.y);
                left.nor();
            }
            if(currBlock.checkDeath()) {
                if(currBlock.getClass().equals(EnergyBlock.class)){
                    for(int j =0; j < enemyBlocks.size(); j++){
                        World.popupManager.createExplosion(enemyBlocks.get(j).position.x,
                                enemyBlocks.get(j).position.y, 50, 0);
                    }
                    enemyBlocks.clear();
                }
                else{
                    currBlock.health = currBlock.maxHealth;
                }

            }
        }
    }
}
