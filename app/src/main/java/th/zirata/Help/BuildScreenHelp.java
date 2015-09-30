package th.zirata.Help;

import android.os.Build;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.math.OverlapTester;

import java.util.HashMap;
import java.util.List;

import th.zirata.BuildHelpText;
import th.zirata.BuildScreen;


public class BuildScreenHelp extends BuildScreen {


    int tutorialNum;
    public BuildScreenHelp(Game game) {
        super(game);
        tutorialNum = 0;
        String text = BuildHelpText.buildHelpText.get(tutorialNum);
        HashMap newSpriteExtra = createSpriteExtra("sprite", "Rectangle", 160f, 400f, 260f, 50f, 999f, 0f);
        HashMap newTextExtra = createTextExtra("text", text, 160f, 400f, 8f, 8f, 999f, "white", "center");
        UIExtras.add(newSpriteExtra);
        UIExtras.add(newTextExtra);
    }


    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if (event.type == Input.TouchEvent.TOUCH_UP) {


                if(OverlapTester.pointInRectangle(BuildHelpText.buildHelpRect.get(tutorialNum), touchPoint)){
                    String text = BuildHelpText.buildHelpText.get(tutorialNum);
                    //Might need to change this..

                    UIExtras.get(0).put("timeToDisplay", 0f);
                    UIExtras.get(1).put("timeToDisplay", 0f);
                    HashMap newSpriteExtra = createSpriteExtra("sprite", "Rectangle", 160f, 400f, 260f, 50f, 999f, 0f);
                    HashMap newTextExtra = createTextExtra("text", text, 160f, 400f, 8f, 8f, 999f, "white", "center");
                    UIExtras.add(newSpriteExtra);
                    UIExtras.add(newTextExtra);
                    super.update(deltaTime);
                    tutorialNum +=1;
                }
            }
        }

    }

}
