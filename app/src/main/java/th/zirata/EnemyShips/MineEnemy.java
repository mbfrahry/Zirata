package th.zirata.EnemyShips;

import th.zirata.Blocks.Block;
import th.zirata.Game.World;

/**
 * Created by Max Bauer on 10/12/2015.
 */
public class MineEnemy extends Enemy {
    public MineEnemy(int enemyLevel) {
        super(enemyLevel);
    }

    public void update(float deltaTime, World world){
        super.update(deltaTime, world);


        if(enemyBlocks.get(0).position.x < 1){
            for(Block b : enemyBlocks) {
                if (b.velocity.x < 0) {
                    b.velocity.x *= -1;
                }
            }
        }
        else if(enemyBlocks.get(0).position.x > 319){

        }




    }


}
