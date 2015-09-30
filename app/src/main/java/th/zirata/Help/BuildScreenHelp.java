package th.zirata.Help;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;

import java.util.HashMap;
import java.util.List;

import th.zirata.BuildScreen;

/**
 * Created by Matthew on 9/8/2015.
 */
public class BuildScreenHelp extends BuildScreen {


    public BuildScreenHelp(Game game) {
        super(game);
    }


    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if (event.type == Input.TouchEvent.TOUCH_UP) {

                //TODO Create a JSON structure for this stuff. And read it in.
                HashMap newSpriteExtra = createSpriteExtra("sprite", "Rectangle", 160f, 260f, 260f, 50f, 1f, 0f);
                HashMap newTextExtra = createTextExtra("text", "Can't launch with blank blocks!", 160f, 260f, 8f, 8f, 1f, "white", "center");
                UIExtras.add(newSpriteExtra);
                UIExtras.add(newTextExtra);
            }
        }
        super.update(deltaTime);
    }

}
