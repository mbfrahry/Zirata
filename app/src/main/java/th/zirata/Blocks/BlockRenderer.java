package th.zirata.Blocks;

import android.util.Log;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Settings.Assets;

/**
 * Created by Max Bauer on 9/26/2015.
 */
public class BlockRenderer {

    public BlockRenderer(){

    }
/*
    public void renderSimpleBlock(String blockType, SpriteBatcher batcher, float x, float y, float width, float height){
        if(blockType.equals("Turret")){
            float angle = 0;
            renderSimpleTurret(batcher, new TurretBlock(new Vector2(0,0) ,0), x, y, width, height, angle);
        }
        else if(blockType.equals("Energy")){
            renderSimpleEnergyBlock(batcher, new EnergyBlock(new Vector2(0,0)), x, y, width, height);
        } else if(blockType.equals("Armor")){
            renderSimpleArmorBlock(batcher, new ArmorBlock(new Vector2(0,0)),  x, y, width, height);
        } else if (blockType.equals("Multiplier")){
            renderSimpleMultiplierBlock(batcher, new MultiplierBlock(new Vector2(0,0)), x, y, width, height);
        }
        else{
            renderSimpleBaseBlock(batcher, x, y, width, height);
        }
    }*/

    public void renderSimpleBlock(Block b, SpriteBatcher batcher, float x, float y, float width, float height){
        if(b.getClass().equals(TurretBlock.class)){
            TurretBlock tBlock = (TurretBlock) b;
            float angle = 0;
            Vector2 rotate = getRotationVector(tBlock.fireAngle);
            angle = rotate.sub(new Vector2(0,0)).angle()-90;
            renderSimpleTurret(batcher, b,  x, y, width, height, angle);
        }
        else if(b.getClass().equals(EnergyBlock.class)){
            renderSimpleEnergyBlock(batcher, b, x, y, width, height);
        }
        else if(b.getClass().equals(ArmorBlock.class)){
            renderSimpleArmorBlock(batcher, b, x, y, width, height);
        }
        else if (b.getClass().equals(MultiplierBlock.class)){
            renderSimpleMultiplierBlock(batcher, b, x, y, width, height);
        }
        else{
            renderSimpleBaseBlock(batcher, x, y, width, height);
        }
    }

    public void renderSimpleUIBlock(Camera2D guiCam, Block b, SpriteBatcher batcher, float x, float y, float width, float height){
        x = guiCam.position.x + ((x -160)*guiCam.zoom );
        y = guiCam.position.y + ((y -240)*guiCam.zoom );
        width *= guiCam.zoom;
        height *= guiCam.zoom;
        if(b.getClass().equals(TurretBlock.class)){
            TurretBlock tBlock = (TurretBlock) b;
            float angle = 0;
            Vector2 rotate = getRotationVector(tBlock.fireAngle);
            angle = rotate.sub(new Vector2(0,0)).angle()-90;
            renderSimpleTurret(batcher, b, x, y, width, height, angle);
        }
        else if(b.getClass().equals(EnergyBlock.class)){
            renderSimpleEnergyBlock(batcher, b, x, y, width, height);
        }
        else if(b.getClass().equals(ArmorBlock.class)){
            renderSimpleArmorBlock(batcher, b, x, y, width, height);
        }
        else if (b.getClass().equals(MultiplierBlock.class)){
            //Log.d("Stuff3", b.imageNums[0] + " ");
            renderSimpleMultiplierBlock(batcher, b, x, y, width, height);
        }
        else{
            renderSimpleBaseBlock(batcher, x, y, width, height);
        }
    }



    //Eventually we will get the asset information from the block to put all the sprites together
    private void renderSimpleTurret(SpriteBatcher batcher, Block tBlock, float x, float y, float width, float height, float angle){

        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[0] + "Base"));
        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[1] + "Damage"));
        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[2] + "FireRate"));
        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[3] + "Range"));
        batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("TurrBase" + tBlock.imageNums[0]));
        batcher.drawSprite(x, y, width, height, angle, Assets.textureRegions.get("TurrConnector" + tBlock.imageNums[3]));
        batcher.drawSprite(x, y, width, height, angle, Assets.textureRegions.get("TurrGuns" + tBlock.imageNums[1]));
        batcher.drawSprite(x, y, width, height, angle, Assets.textureRegions.get("TurrMag" + tBlock.imageNums[2]));

        //need to draw activity marker here too

    }

    private void renderSimpleEnergyBlock(SpriteBatcher batcher, Block eBlock, float x, float y, float width, float height){

        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("EnergyLevel" + eBlock.imageNums[0] + "Base"));
        //batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("EnergyLevel" + eBlock.imageNums[1] + "Energy"));
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("EnergyBase" + eBlock.imageNums[0]));
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("EnergyStrut" + eBlock.imageNums[1]));
    }

    private void renderSimpleArmorBlock(SpriteBatcher batcher, Block aBlock, float x, float y, float width, float height){
        //batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("ArmorLevel" + aBlock.imageNums[0] + "Base"));
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("Armor3"));
    }

    private void renderSimpleMultiplierBlock(SpriteBatcher batcher, Block mBlock, float x, float y, float width, float height){
        //Log.d("Stuff2", mBlock.imageNums[0] + " ");
        batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("Level" + mBlock.imageNums[0] + "Base"));
        batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("Level" + mBlock.imageNums[2] + "Towers"));
        batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("Level" + mBlock.imageNums[3] + "Round"));
        batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("Level" + mBlock.imageNums[1] + "Bar"));
//      batcher.drawSprite(x, y, width, height, Assets.textureRegions.get("MultiplierLevel" + mBlock.imageNums[0] + "Base"));
//      batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("MultiplierLevel" + mBlock.imageNums[2] + "Towers"));
//      batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("MultiplierLevel" + mBlock.imageNums[3] + "Round"));
//      batcher.drawSprite(x  , y, width, height, Assets.textureRegions.get("MultiplierLevel" + mBlock.imageNums[1] + "Bar"));
        //need to draw activity marker here too
    }

    private void renderSimpleBaseBlock(SpriteBatcher batcher, float x, float y, float width, float height){
        batcher.drawSprite(x , y, width , height, Assets.textureRegions.get("BaseBlock"));
    }

    private Vector2 getRotationVector(float fireAngle){
        Vector2 rotate;
        if (fireAngle == 0){
            rotate = new Vector2(1,0);
        }
        else if (fireAngle == 90){
            rotate = new Vector2(0,1);
        }
        else if (fireAngle == 180){
            rotate = new Vector2(-1,0);
        }
        else{
            rotate = new Vector2(0,-1);
        }
        return rotate;
    }

    public void renderGameBlock(Block b, SpriteBatcher batcher){
        if(b.getClass().equals(TurretBlock.class)){
            TurretBlock tBlock = (TurretBlock) b;
            renderGameTurret(batcher, tBlock);
        }
        else if(b.getClass().equals(EnergyBlock.class)){
            EnergyBlock eBlock = (EnergyBlock) b;
            renderGameEnergyBlock(batcher, eBlock);
        }
        else if(b.getClass().equals(ArmorBlock.class)){
            ArmorBlock aBlock = (ArmorBlock) b;
            renderGameArmorBlock(batcher, aBlock);
        }
        else if (b.getClass().equals(MultiplierBlock.class)){
            MultiplierBlock mBlock = (MultiplierBlock) b;
            renderGameMultiplierBlock(batcher, mBlock);
        }
    }

    private void renderGameTurret(SpriteBatcher batcher, TurretBlock tBlock){
        Vector2 rotate = new Vector2(tBlock.lastTouch);
        //batcher.drawSprite(tBlock.position.x, tBlock.position.y, 24, 24, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[0] + "Base"));
        //batcher.drawSprite(tBlock.position.x, tBlock.position.y, 24, 24, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[1] + "Damage"));
        //batcher.drawSprite(tBlock.position.x, tBlock.position.y, 24, 24, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[2] + "FireRate"));
        //batcher.drawSprite(tBlock.position.x, tBlock.position.y, 24, 24, Assets.textureRegions.get("TurretLevel" + tBlock.imageNums[3] + "Range"));
        batcher.drawSprite(tBlock.position.x, tBlock.position.y, 24, 24, Assets.textureRegions.get("TurrBase" + tBlock.imageNums[0]));
        batcher.drawSprite(tBlock.position.x, tBlock.position.y, 36, 36, rotate.sub(tBlock.position).angle() - 90, Assets.textureRegions.get("TurrConnector" + tBlock.imageNums[3]));
        batcher.drawSprite(tBlock.position.x, tBlock.position.y, 36, 36, rotate.sub(tBlock.position).angle() - 90, Assets.textureRegions.get("TurrGuns" + tBlock.imageNums[1]));
        batcher.drawSprite(tBlock.position.x, tBlock.position.y, 36, 36, rotate.sub(tBlock.position).angle() - 90, Assets.textureRegions.get("TurrMag" + tBlock.imageNums[2]));



        if(tBlock.active){
            batcher.drawSprite(tBlock.position.x - 8 + 3, tBlock.position.y - 8, 5, 5, Assets.textureRegions.get("GreenBullet"));
            //There is a bug here with drawing the fire arcs. They need to respect the upgraded range. Added a temporary fix because we can't figure out the real problem
            batcher.drawSprite(tBlock.coneX1, tBlock.coneY1, tBlock.fireRange + tBlock.getAttributeLevel(3)*3.5f, 1, (tBlock.fireAngle + tBlock.fireArcAngle), Assets.textureRegions.get("Bullet"));
            batcher.drawSprite(tBlock.coneX2, tBlock.coneY2, tBlock.fireRange + tBlock.getAttributeLevel(3)*3.5f, 1, (tBlock.fireAngle - tBlock.fireArcAngle), Assets.textureRegions.get("Bullet"));
        }
        else{
            batcher.drawSprite(tBlock.position.x - 8 + 3, tBlock.position.y - 8, 5, 5, Assets.textureRegions.get("Bullet"));
        }
    }

    private void renderGameEnergyBlock(SpriteBatcher batcher, EnergyBlock eBlock){
        //batcher.drawSprite(eBlock.position.x  , eBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyLevel" + eBlock.imageNums[0] + "Base"));
        //batcher.drawSprite(eBlock.position.x  , eBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyLevel" + eBlock.imageNums[1] + "Energy"));
        batcher.drawSprite(eBlock.position.x  , eBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyBase" + eBlock.imageNums[0]));
        batcher.drawSprite(eBlock.position.x  , eBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyStrut" + eBlock.imageNums[1]));
    }

    private void renderGameArmorBlock(SpriteBatcher batcher, ArmorBlock aBlock){
        //batcher.drawSprite(aBlock.position.x , aBlock.position.y, 24 , 24, Assets.textureRegions.get("ArmorLevel" + aBlock.imageNums[0] + "Base"));
        batcher.drawSprite(aBlock.position.x , aBlock.position.y, 24 , 24, Assets.textureRegions.get("Armor3"));
    }

    private void renderGameMultiplierBlock(SpriteBatcher batcher, MultiplierBlock mBlock){
        batcher.drawSprite(mBlock.position.x  , mBlock.position.y, 24, 24, Assets.textureRegions.get("Level" + mBlock.imageNums[0] + "Base"));
        batcher.drawSprite(mBlock.position.x  , mBlock.position.y, 24, 24, Assets.textureRegions.get("Level" + mBlock.imageNums[1] + "Bar"));
        batcher.drawSprite(mBlock.position.x  , mBlock.position.y, 24, 24, Assets.textureRegions.get("Level" + mBlock.imageNums[2] + "Towers"));
        batcher.drawSprite(mBlock.position.x  , mBlock.position.y, 24, 24, Assets.textureRegions.get("Level" + mBlock.imageNums[3] + "Round"));
        //batcher.drawSprite(mBlock.position.x  , mBlock.position.y, 24, 24, Assets.textureRegions.get("Multiplier3"));
        if(mBlock.state == MultiplierBlock.MULTIPLIER_READY){
            batcher.drawSprite(mBlock.position.x , mBlock.position.y + 2, 10, 10, Assets.textureRegions.get("Bullet"));
        }
        else if(mBlock.state == MultiplierBlock.MULTIPLIER_MULTIPLYING){
            batcher.drawSprite(mBlock.position.x, mBlock.position.y +2, 10, 10, Assets.textureRegions.get("GreenBullet"));
        }
        else if(mBlock.state == MultiplierBlock.MULTIPLIER_COOLING){
            batcher.drawSprite(mBlock.position.x, mBlock.position.y + 2, 10, 10, Assets.textureRegions.get("YellowBullet"));
        }
    }

}
