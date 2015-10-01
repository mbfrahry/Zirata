package th.zirata.Help;

import android.os.Build;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.HashMap;
import java.util.List;

import th.zirata.ArmorBlock;
import th.zirata.Assets;
import th.zirata.BlankBlock;
import th.zirata.Block;
import th.zirata.BuildHelpText;
import th.zirata.BuildScreen;
import th.zirata.EnergyBlock;
import th.zirata.GameScreen;
import th.zirata.MapScreen;
import th.zirata.MultiplierBlock;
import th.zirata.PlayerSave;
import th.zirata.Settings;
import th.zirata.TurretBlock;


public class BuildScreenHelp extends BuildScreen {

    int tutorialNum;
    TutorialStep currStep;

    public BuildScreenHelp(Game game) {
        super(game);
        tutorialNum = 0;
        currStep = BuildHelpText.tutorialSteps.get(tutorialNum);
    }


    public void update(float deltaTime){
        if(UIExtras.size() < 3) {
            String text = currStep.content;
            generatePopup(text, currStep.contentLocation.x, currStep.contentLocation.y, 999f);

            if(currStep.hasSprite){
                HashMap newSprite = createSpriteExtra("sprite", currStep.spriteName, currStep.spriteInfo.position.x, currStep.spriteInfo.position.y, currStep.spriteInfo.bounds.width, currStep.spriteInfo.bounds.height, 999, currStep.spriteAngle);
                UIExtras.add(newSprite);
            }

        }
        if(currStep.hasSprite){
            batcher.drawSprite(currStep.spriteInfo.position.x, currStep.spriteInfo.position.y, 200, 200, Assets.textureRegions.get(currStep.spriteName));
        }
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if (event.type == Input.TouchEvent.TOUCH_UP) {


                if(OverlapTester.pointInRectangle(currStep.touch, touchPoint)){
                    clearUIExtras();
//                    UIExtras.get(0).put("timeToDisplay", 0f);
//                    UIExtras.get(1).put("timeToDisplay", 0f);
//                    if(currStep.hasSprite){
//                        UIExtras.get(2).put("timeToDisplay", 0f);
//                    }

                    if(currStep.action) {
                        checkTouchEvent(deltaTime, touchPoint);
                    }
                    if(tutorialNum < 15){
                        tutorialNum +=1;
                        currStep = BuildHelpText.tutorialSteps.get(tutorialNum);
                    }
                }
            }
        }

    }

    public void checkTouchEvent(float deltaTime, Vector2 touchPoint) {

        if (selectedBankBlock != null && selectedActiveBlock == null) {
            selectedBankBlock = null;
            showBlockBank = false;
        }

        if (!showBlockBank) {

            if (OverlapTester.pointInRectangle(forwardBounds, touchPoint)) {
                if (checkForBlankBlocks()) {
                    HashMap newSpriteExtra = createSpriteExtra("sprite", "Rectangle", 160f, 260f, 260f, 50f, 1f, 0f);
                    HashMap newTextExtra = createTextExtra("text", "Can't launch with blank blocks!", 160f, 260f, 8f, 8f, 1f, "white", "center");
                    UIExtras.add(newSpriteExtra);
                    UIExtras.add(newTextExtra);
                } else {

                    setTurretDirections();
                    Settings.firstTime = false;
                    Settings.save(game.getFileIO());
                    PlayerSave.save(game.getFileIO());
                    game.setScreen(new GameScreen(game));
                    return;
                }

            }


            Rectangle pBlockBounds;
            for (int j = 0; j < potentialBlocks.size(); j++) {
                Block currBlock = potentialBlocks.get(j);
                pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
                if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
                    if (Settings.spaceBucks >= Settings.nextBlockCost) {
                        PlayerSave.activeBlocks.add(currBlock);
                        PlayerSave.save(game.getFileIO());
                        potentialBlocks.remove(j);
                        getPotentialBlocks();
                        Settings.spaceBucks -= Settings.nextBlockCost;
                        Settings.nextBlockCost = PlayerSave.activeBlocks.size();
                        Settings.save(game.getFileIO());
                        showBlockBank = true;
                        selectedActiveBlock = currBlock;
                        resetBlockBankBounds();
                        showUpgrades = true;
                        showFuse = false;
                    } else {
                        HashMap newExtra = createTextExtra("text", "Bank", 217f, 395f, 12f, 12f, .25f, "red", "right");
                        UIExtras.add(newExtra);
                    }
                    return;
                }
            }

            for (int j = 0; j < PlayerSave.activeBlocks.size(); j++) {
                Block currBlock = PlayerSave.activeBlocks.get(j);
                pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
                if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {//&& currBlock.getClass() != BlankBlock.class) {
                    selectedActiveBlock = currBlock;
                    resetBlockBankBounds();
                    if (testShowFuse()) {
                        showFuse = true;
                        showUpgrades = false;
                    } else {
                        showUpgrades = true;
                        showFuse = false;
                    }

                    showBlockBank = true;
                    if (selectedActiveBlock.getClass() == TurretBlock.class || selectedActiveBlock.getClass() == BlankBlock.class) {
                        blockBankOption = BLOCK_BANK_TURRET;
                        ownedBlocksByType = getBlocksFromType(TurretBlock.class);
                        return;
                    } else if (selectedActiveBlock.getClass() == ArmorBlock.class) {
                        blockBankOption = BLOCK_BANK_ARMOR;
                        ownedBlocksByType = getBlocksFromType(ArmorBlock.class);
                        return;
                    } else if (selectedActiveBlock.getClass() == EnergyBlock.class) {
                        blockBankOption = BLOCK_BANK_ENERGY;
                        ownedBlocksByType = getBlocksFromType(EnergyBlock.class);
                        return;
                    } else if (selectedActiveBlock.getClass() == MultiplierBlock.class) {
                        blockBankOption = BLOCK_BANK_MULTIPLIER;
                        ownedBlocksByType = getBlocksFromType(MultiplierBlock.class);
                        return;
                    }
                    return;
                }
            }
        } else {
            checkBankBlocks(touchPoint);
            if (showUpgrades && showSubmenu) {
                checkUpgradeBounds(touchPoint);
            } else if (showFuse && showSubmenu) {
                checkFuseBounds();
            }
            if (OverlapTester.pointInRectangle(closeBankBounds, touchPoint)) {
                guiCam.panToPosition(160, 240, 1);
                showBlockBank = false;
                return;
            }
        }


        guiCam.update(deltaTime);
        if (showBlockBank) {
            resetBlockBankBounds();
        }
    }

}
