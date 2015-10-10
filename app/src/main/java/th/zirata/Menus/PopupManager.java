package th.zirata.Menus;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;

import java.util.ArrayList;
import java.util.HashMap;

import th.zirata.Settings.Assets;

/**
 * Created by Max Bauer on 10/2/2015.
 */
public class PopupManager {

    private ArrayList<HashMap> popups;
    public SpriteBatcher batcher;
    public Camera2D guiCam;

    public PopupManager(SpriteBatcher batcher, Camera2D guiCam){
        this.popups = new ArrayList<HashMap>();
        this.batcher = batcher;
        this.guiCam = guiCam;
    }

    public void createSpriteExtra(String type, String content, float x, float y, float width, float height, float timeToDisplay, float angle){
        HashMap event = new HashMap();
        event.put("type", type);
        event.put("content", content);
        event.put("x", x);
        event.put("y", y);
        event.put("width", width);
        event.put("height", height);
        event.put("timeToDisplay", timeToDisplay);
        event.put("angle", angle);
        popups.add(event);
    }

    private void drawSpriteExtra(HashMap currEvent){
        String currContent = (String)currEvent.get("content");
        float currX = (Float)currEvent.get("x");
        float currY = (Float)currEvent.get("y");
        float currWidth = (Float)currEvent.get("width");
        float currHeight = (Float)currEvent.get("height");
        float currAngle = (Float)currEvent.get("angle");
        batcher.drawUISprite(guiCam, currX, currY, currWidth, currHeight, currAngle, Assets.textureRegions.get(currContent));
    }

    public void createTextExtra(String type, String content, float x, float y, float width, float height, float timeToDisplay, String color, String justification){
        HashMap event = new HashMap();
        event.put("type", type);
        event.put("content", content);
        event.put("x", x);
        event.put("y", y);
        event.put("width", width);
        event.put("height", height);
        event.put("timeToDisplay", timeToDisplay);
        event.put("color", color);
        event.put("justification", justification);
        popups.add(event);
    }

    private void drawTextExtra(HashMap currEvent){
        Font currFont;
        if(currEvent.get("color").equals("red")){
            currFont = Assets.redFont;
        }
        else{
            currFont = Assets.font;
        }
        String justification = (String)currEvent.get("justification");
        String currContent = (String)currEvent.get("content");
        float currX = (Float)currEvent.get("x");
        float currY = (Float)currEvent.get("y");
        float currWidth = (Float)currEvent.get("width");
        float currHeight = (Float)currEvent.get("height");
        if(justification.equals("right")){
            currFont.drawUITextRightJustified(guiCam, batcher, currContent, currX, currY, currWidth, currHeight);
        }
        else if(justification.equals("center")){
            currFont.drawUITextCentered(guiCam, batcher, currContent, currX, currY, currWidth, currHeight);
        }
        else if(justification.equals("lined")){
            currFont.drawLinedText(batcher, currContent, currX, currY, currWidth, currHeight);
        }
        else{
            currFont.drawUITextCentered(guiCam, batcher, currContent, currX, currY, currWidth, currHeight);
        }
    }

    public void generatePopup(String content, float x, float y, float timeToDisplay){
        int spaces = content.length() - content.replace(" ", "").length();

        createSpriteExtra("sprite", "DarkGrayRectangle", x, y, 300f, (float)(spaces/5 + 1.5)*9, timeToDisplay, 0f);
        createTextExtra("text", content, x, y + (spaces/5)*4, 8f, 8f, timeToDisplay, "white", "lined");

//		HashMap newSpriteExtra = createSpriteExtra("sprite", "DarkGrayRectangle", x, y, 300f, (float)(spaces/5 + 1.5)*9, timeToDisplay, 0f);
//		HashMap newTextExtra = createTextExtra("text", content, x, y + (spaces/5)*4, 8f, 8f, 999f, "white", "lined");
//		UIExtras.add(newSpriteExtra);
//		UIExtras.add(newTextExtra);
    }

    public void drawUIExtras(float deltaTime){
		/*UI Extra format
		string type: <sprite,text>
		string content: <spriteName, textToShow>
		float x: <position x>
		float y: <position y>
		float width: <width>
		float height: <height>
		float timeToDisplay: <timeToDisplay>
		if type == text
		    string color: <red, white>
            string justification: <left,right,center>
		if type == sprite
		    float angle: <rotationAngle>
		 */
        ArrayList<Integer> toDelete = new ArrayList<Integer>();

        for(int i = 0; i < popups.size(); i++){
            HashMap currEvent = popups.get(i);

            if (currEvent.get("type").equals("text")){
                drawTextExtra(currEvent);
            }
            else{
                drawSpriteExtra(currEvent);
            }

            float time = (Float)currEvent.get("timeToDisplay");
            if(time - deltaTime < 0){
                toDelete.add(i);
            }
            else{
                time -= deltaTime;
                currEvent.put("timeToDisplay", time);
            }
        }

        //Might be buggy....
        for(int i = toDelete.size()-1; i >= 0; i--){
            popups.remove((int) toDelete.get(i));
        }
    }

    public void drawUIExtras() {
		/*UI Extra format
		string type: <sprite,text>
		string content: <spriteName, textToShow>
		float x: <position x>
		float y: <position y>
		float width: <width>
		float height: <height>
		float timeToDisplay: <timeToDisplay>
		if type == text
		    string color: <red, white>
            string justification: <left,right,center>
		if type == sprite
		    float angle: <rotationAngle>
		 */

        for (int i = 0; i < popups.size(); i++) {
            HashMap currEvent = popups.get(i);

            if (currEvent.get("type").equals("text")) {
                drawTextExtra(currEvent);
            } else {
                drawSpriteExtra(currEvent);
            }
        }
    }

    public void updatePopups(float deltaTime){
        ArrayList<Integer> toDelete = new ArrayList<Integer>();
        for(int i = 0; i < popups.size(); i++){
            HashMap currEvent = popups.get(i);
            float time = (Float)currEvent.get("timeToDisplay");
            if(time - deltaTime < 0){
                toDelete.add(i);
            }
            else{
                time -= deltaTime;
                currEvent.put("timeToDisplay", time);
            }
        }
        for(int i = toDelete.size()-1; i >= 0; i--){
            popups.remove((int) toDelete.get(i));
        }

    }

    public synchronized void clearUIExtras(){
        for(int i = 0; i < popups.size(); i++){
            popups.get(i).put("timeToDisplay", 0f);
        }
    }

    public int getPopupsSize(){
        return popups.size();
    }
}
