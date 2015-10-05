package th.zirata.Settings;

import android.util.JsonReader;

import com.badlogic.androidgames.framework.FileIO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import th.zirata.Blocks.EnemyBlocks.Enemy;
import th.zirata.Blocks.EnemyBlocks.Hydra;

/**
 * Created by Matthew on 9/20/2015.
 */
public class EnemySettings {

    public static HashMap<String, double[]> enemiesInLevel = new HashMap<String, double[]>();
    public static HashMap<String, double[]> bossInLevel = new HashMap<String, double[]>();

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
            String bossNumName = reader.nextName();
            int bossNum = reader.nextInt();
            double[] levelData = {enemyNum, enemyLevelnum, turretChanceNum, bossNum};
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

    public static Enemy getBoss(String type){
        Enemy enemy = null;
        if(type.equals("Hydra")){
            enemy = new Hydra(1);
        }
        return enemy;
    }


}