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
import java.util.Random;

/**
 * Created by Matthew on 9/20/2015.
 */
public class EnemySettings {

    public static HashMap<String, double[]> enemiesInLevel = new HashMap<String, double[]>();

//    public EnemySettings(){
//        enemiesInLevel = new HashMap<String, int[]>();
//    }

    public static void load(FileIO files){
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(files.readAsset("EnemiesInLevel"), "UTF-8"));
            readEnemy(reader);
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
            String enemyLevel = reader.nextName();
            int enemyLevelnum = reader.nextInt();
            String turretChance = reader.nextName();
            double turretChanceNum = reader.nextDouble();
            double[] levelData = {enemyNum, enemyLevelnum, turretChanceNum};
            enemiesInLevel.put(name, levelData);
            reader.endObject();

        }

        reader.endObject();
    }

    public static Enemy getEnemy(int level){
        Random rand = new Random();
        String levelName = "level"+level;
        double[] enemyLevelSettings = enemiesInLevel.get(levelName);
        double turretChance = enemyLevelSettings[2];
        int enemyType;
        if(rand.nextFloat() > turretChance){
          enemyType = 1;
        }
        else{
          enemyType = 2;
        }
        return new Enemy(enemyType, Settings.currLevel);
    }

/*
    public static ArrayList<Enemy> getEnemies(int level){
        Random rand = new Random();
        String levelName = "level"+level;
        double[] enemyLevelSettings = enemiesInLevel.get(levelName);
        int numEnemies = (int)enemyLevelSettings[0];
        int enemyLevel = (int)enemyLevelSettings[1];
        double turretChance = enemyLevelSettings[2];

        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for(int i =0; i < numEnemies; i++){
            int enemyType;
            if(rand.nextFloat() > turretChance){
                enemyType = 1;
            }
            else{
                enemyType = 2;
            }
            enemies.add(new Enemy(enemyType, enemyLevel));
        }

        return enemies;

    }*/

}