package th.zirata.Help;

import com.badlogic.androidgames.framework.GameObject;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Max Bauer on 9/30/2015.
 */
public class TutorialStep {
    public String content;
    public Vector2 contentLocation;
    public boolean action, hasSprite;
    public Rectangle touch;
    public GameObject spriteInfo;
    public String spriteName;
    public float spriteAngle;

    public TutorialStep(){
        this.content = "";
        this.contentLocation = new Vector2();
        this.action = false;
        this.hasSprite = false;
        this.touch = null;
        this.spriteInfo = null;
        this.spriteName = "";
        this.spriteAngle = 0;
    }

    public TutorialStep(String content, Vector2 contentLocation, boolean action, Rectangle touch, boolean hasSprite, GameObject spriteInfo, String spriteName, float spriteAngle){
        this.content = content;
        this.contentLocation = contentLocation;
        this.action = action;
        this.hasSprite = hasSprite;
        this.touch = touch;
        this.spriteInfo = spriteInfo;
        this.spriteName = spriteName;
        this.spriteAngle = spriteAngle;
    }
}
