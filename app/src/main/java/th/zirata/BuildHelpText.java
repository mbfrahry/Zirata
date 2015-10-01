package th.zirata;

import android.util.JsonReader;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.GameObject;
import com.badlogic.androidgames.framework.math.Rectangle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import th.zirata.Help.TutorialStep;

/**
 * Created by Matthew on 9/20/2015.
 */
public class BuildHelpText {

//    public static ArrayList<String> buildHelpText = new ArrayList<String>();
//    public static ArrayList<Rectangle> buildHelpRect = new ArrayList<Rectangle>();
//    public static ArrayList<Boolean> buildHelpAction = new ArrayList<Boolean>();
    public static ArrayList<TutorialStep> tutorialSteps = new ArrayList<TutorialStep>();

    public static void load(FileIO files){
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(files.readAsset("BuildScreenHelpText"), "UTF-8"));
            readBuildHelp(reader);
        }catch(IOException e){

        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch(IOException e){

            }
        }
    }

    public static void readBuildHelp(JsonReader reader) throws IOException{

//        String num = null;
        reader.beginObject();
        while (reader.hasNext()) {
            TutorialStep newTutStep = new TutorialStep();
            reader.nextName();
            reader.beginObject();

            //Define content and content location
            reader.nextName();
            String content = reader.nextString();
            newTutStep.content = content;
            reader.nextName();
            reader.beginObject();
            reader.nextName();
            int contentX = reader.nextInt();
            reader.nextName();
            int contentY = reader.nextInt();
            newTutStep.contentLocation.set(contentX, contentY);
            reader.endObject();

            //Define touch point to continue
            reader.nextName();
            boolean action = reader.nextBoolean();
            newTutStep.action = action;
            reader.nextName();
            reader.beginObject();
            reader.nextName();
            int touchX = reader.nextInt();
            reader.nextName();
            int touchY = reader.nextInt();
            reader.nextName();
            int touchWidth = reader.nextInt();
            reader.nextName();
            int touchHeight = reader.nextInt();
            newTutStep.touch = new Rectangle(touchX, touchY, touchWidth, touchHeight);
            reader.endObject();

            //Define sprite if there is one
            reader.nextName();
            boolean hasSprite = reader.nextBoolean();
            newTutStep.hasSprite = hasSprite;
            if(hasSprite){
                reader.nextName();
                reader.beginObject();
                reader.nextName();
                int spriteX = reader.nextInt();
                reader.nextName();
                int spriteY = reader.nextInt();
                reader.nextName();
                int spriteWidth = reader.nextInt();
                reader.nextName();
                int spriteHeight = reader.nextInt();
                reader.nextName();
                int spriteAngle = reader.nextInt();
                reader.nextName();
                String spriteName = reader.nextString();
                newTutStep.spriteInfo = new GameObject(spriteX, spriteY, spriteWidth, spriteHeight);
                newTutStep.spriteName = spriteName;
                newTutStep.spriteAngle = spriteAngle;

                reader.endObject();
            }
            reader.endObject();
            tutorialSteps.add(newTutStep);
        }

        reader.endObject();
    }

}
