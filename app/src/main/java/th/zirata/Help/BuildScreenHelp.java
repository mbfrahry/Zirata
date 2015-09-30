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
    }


    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if (event.type == Input.TouchEvent.TOUCH_UP) {

                String text = BuildHelpText.buildHelpText.get(tutorialNum);
                if(OverlapTester.pointInRectangle(BuildHelpText.buildHelpRect.get(tutorialNum), touchPoint)){
                    super.update(deltaTime);
                    tutorialNum +=1;
                }
            }
        }

    }

}
