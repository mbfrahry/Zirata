package th.zirata;

import android.util.JsonReader;

import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.gl.TextureRegion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matthew on 9/20/2015.
 */
public class EnemySettings {

    public HashMap<String, int[]> enemiesInLevel;

    public static void load(FileIO files){
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(files.readAsset("EnemiesInLevel"), "UTF-8"));
            readEnemiesArray(reader);
        }catch(IOException e){

        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch(IOException e){

            }
        }
    }

    public static void readEnemiesArray(JsonReader reader) throws IOException {

        while (reader.hasNext()) {
            readEnemy(reader);
        }
    }

    public static void readEnemy(JsonReader reader) throws IOException{
        String level = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            reader.beginObject();
            String enemyNumName = reader.nextName();
            int enemyNum = reader.nextInt();
            reader.endObject();

        }

        reader.endObject();
    }


    public static ArrayList<Block> getEnemies(int level){
        ArrayList<Block> enemies = new ArrayList<Block>();

        return enemies;

    }

}
