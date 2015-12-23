package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnergyBlock;
import th.zirata.Game.Player;
import th.zirata.Game.World;

/**
 * Created by Max Bauer on 12/23/2015.
 */
public class BNM extends Enemy {

    float x;
    float y;
    int blockLevel;
    float cooldown;
    Vector2 left;
    ArrayList<BlockNest> blockNests = new ArrayList<BlockNest>();

    public BNM(int blockLevel, float world_cos, float world_sin){
        super(blockLevel);
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

        createBNM(x_axis, y_axis);
    }

    public void createBNM(Vector2 world_x_axis, Vector2 world_y_axis){
        blockNests.add(new BlockNest(blockLevel, x, y));
        blockNests.add(new BlockNest(blockLevel, x+50, y + 50));
        blockNests.add(new BlockNest(blockLevel, x - 50, y + 50));
        blockNests.add(new BlockNest(blockLevel, x+50, y - 50));
        blockNests.add(new BlockNest(blockLevel, x - 50, y - 50));
        for(int i = 0; i < blockNests.size(); i++){
            for(int j = 0; j < blockNests.get(i).enemyBlocks.size(); j++){
                enemyBlocks.add(blockNests.get(i).enemyBlocks.get(j));
            }
        }
    }

    public void update(float deltaTime, World world){

        for(int i = 0; i < blockNests.size(); i++){
            if(blockNests.get(i).enemyBlocks.size() == 0 ){
                blockNests.remove(blockNests.get(i));
                continue;
            }
            for(int j = 0; j < blockNests.get(i).enemyBlocks.size(); j++){
                Block currBlock = blockNests.get(i).enemyBlocks.get(j);
                if(blockNests.size() > 1 && blockNests.get(0).enemyBlocks.contains(currBlock)){
                    currBlock.health = currBlock.maxHealth;
                }
            }
            blockNests.get(i).update(deltaTime, world);
        }
        super.update(deltaTime, world);

    }

}
