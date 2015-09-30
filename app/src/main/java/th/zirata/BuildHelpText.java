package th.zirata;

import android.util.JsonReader;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.math.Rectangle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Matthew on 9/20/2015.
 */
public class BuildHelpText {

    public static ArrayList<String> buildHelpText = new ArrayList<String>();
    public static ArrayList<Rectangle> buildHelpRect = new ArrayList<Rectangle>();
    public static ArrayList<Boolean> buildHelpAction = new ArrayList<Boolean>();

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
        String num = null;
        reader.beginObject();
        while (reader.hasNext()) {
            num = reader.nextName();


            reader.beginObject();
            String levelName = reader.nextName();
            String level = reader.nextString();
            buildHelpText.add(level);
            String xName = reader.nextName();
            int x = reader.nextInt();
            String yName = reader.nextName();
            int y = reader.nextInt();
            String widthname = reader.nextName();
            int width = reader.nextInt();
            String heightName = reader.nextName();
            int height = reader.nextInt();
            buildHelpRect.add(new Rectangle(x, y, width, height));
            String actionName = reader.nextName();
            boolean action = reader.nextBoolean();
            buildHelpAction.add(action);
            reader.endObject();

        }

        reader.endObject();
    }

}
