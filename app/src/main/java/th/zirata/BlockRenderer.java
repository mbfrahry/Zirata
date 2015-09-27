package th.zirata;

import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Max Bauer on 9/26/2015.
 */
public class BlockRenderer {

    public BlockRenderer(){

    }


    public void renderBlock(Block b, SpriteBatcher batcher, float x, float y, float width, float height, float angle){
        if(b.getClass().equals(TurretBlock.class)){
            renderTurret(batcher, x, y, width, height, angle);
        }
        else if(b.getClass().equals(EnergyBlock.class)){
            renderEnergyBlock(batcher, x, y, width, height);
        }
        else if(b.getClass().equals(ArmorBlock.class)){
            renderArmorBlock(batcher, x, y, width, height);
        }
        else if (b.getClass().equals(ArmorBlock.class)){
            renderMultiplierBlock(batcher, x, y, width, height);
        }
        else{
            renderBaseBlock(batcher, x, y, width, height);
        }
    }

    //Eventually we will get the asset information from the block to put all the sprites together
    private void renderTurret(SpriteBatcher batcher, float x, float y, float width, float height, float angle){
        //batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("NearStarBG"));
        batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurretBase"));
        batcher.drawSprite(x, y, width, height, angle, Assets.textureRegions.get("TurretTop"));
        //need to draw activity marker here too

    }

    private void renderEnergyBlock(SpriteBatcher batcher, float x, float y, float width, float height){
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("EnergyBlock"));
    }

    private void renderArmorBlock(SpriteBatcher batcher, float x, float y, float width, float height){
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("ArmorBlock"));
    }

    private void renderMultiplierBlock(SpriteBatcher batcher, float x, float y, float width, float height){
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("MultiplierBlock"));
        //need to draw activity marker here too
    }

    private void renderBaseBlock(SpriteBatcher batcher, float x, float y, float width, float height){
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("BaseBlock"));
    }
}
