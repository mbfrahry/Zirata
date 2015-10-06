package th.zirata.Help;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.List;

import th.zirata.Game.GameScreen;
import th.zirata.Menus.PopupManager;
import th.zirata.Settings.Assets;

/**
 * Created by Matthew on 10/5/2015.
 */
public class GameScreenHelp extends GameScreen {

    int tutorialNum;
    TutorialStep currStep;


    public GameScreenHelp(Game game){
        super(game);
        tutorialNum = 0;
        currStep = BuildHelpText.tutorialSteps.get(tutorialNum);

        state = GAME_RUNNING;
    }

    public void update(float deltaTime){
        if(popupManager.getPopupsSize() < 3) {
            String text = currStep.content;
            popupManager.generatePopup(text, currStep.contentLocation.x, currStep.contentLocation.y, 999f);

            if(currStep.hasSprite){
                popupManager.createSpriteExtra("sprite", currStep.spriteName, currStep.spriteInfo.position.x, currStep.spriteInfo.position.y, currStep.spriteInfo.bounds.width, currStep.spriteInfo.bounds.height, 999, currStep.spriteAngle);
//                HashMap newSprite = createSpriteExtra("sprite", currStep.spriteName, currStep.spriteInfo.position.x, currStep.spriteInfo.position.y, currStep.spriteInfo.bounds.width, currStep.spriteInfo.bounds.height, 999, currStep.spriteAngle);
//                UIExtras.add(newSprite);
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
                    popupManager.clearUIExtras();
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


    }
}
