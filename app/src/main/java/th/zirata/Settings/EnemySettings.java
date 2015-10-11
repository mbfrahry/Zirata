package th.zirata.Settings;

import android.util.JsonReader;

import com.badlogic.androidgames.framework.FileIO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import th.zirata.EnemyShips.Enemy;
import th.zirata.EnemyShips.Hydra;
import th.zirata.Game.Level;

/**
 * Created by Matthew on 9/20/2015.
 */
public class EnemySettings {

    public static HashMap<String, double[]> enemiesInLevel = new HashMap<String, double[]>();
    public static HashMap<String, double[]> bossInLevel = new HashMap<String, double[]>();
    public static int ENEMYTYPES = 2;

    public static int STANDARD_ENEMY = 0;
    public static int TELEPORTER_ENEMY = 1;



    public static Level loadLevel(FileIO files, String levelName){
        JsonReader reader = null;
        Level level = null;
        try {
            reader = new JsonReader(new InputStreamReader(files.readAsset("EnemiesInLevel"), "UTF-8"));
            level = readLevel(reader, levelName);
        }catch(IOException e){

        }finally{
            try{
                if(reader != null)
                    reader.close();
            }catch(IOException e){

            }
        }
        return level;
    }

    public static Level readLevel(JsonReader reader, String levelName) throws IOException{
        Level level = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();


            reader.beginObject();
            String enemyNumName = reader.nextName();
            int enemyNum = reader.nextInt();

            String enemyLevelName = reader.nextName();
            int[] enemyLevel = new int[ENEMYTYPES];
            reader.beginArray();
            for (int i = 0; i < ENEMYTYPES; i++) {
                enemyLevel[i] = reader.nextInt();
            }
            reader.endArray();

            String turretChance = reader.nextName();
            double[] enemyChance = new double[ENEMYTYPES];
            reader.beginArray();
            for (int i = 0; i < ENEMYTYPES; i++) {
                enemyChance[i] = reader.nextDouble();
            }
            reader.endArray();

            String bossNumName = reader.nextName();
            int bossNum = reader.nextInt();

            if(name.equals(levelName)) {
                level = new Level(enemyNum, enemyLevel, enemyChance, bossNum);
                break;
            }
            reader.endObject();

        }

        return level;
    }

//    public static Enemy getEnemy(int level){
//        Random rand = new Random();
//        String levelName = "level"+level;
//        double[] enemyLevelSettings = enemiesInLevel.get(levelName);
//        double turretChance = enemyLevelSettings[2];
//        int enemyType;
//        if(rand.nextFloat() > turretChance){
//          enemyType = 1;
//        }
//        else{
//          enemyType = 2;
//        }
//       // return new Enemy(enemyType, Settings.currLevel);
//        return null; //yo mama
//    }

//    public static Enemy getBoss(String type){
//        Enemy enemy = null;
//        if(type.equals("Hydra")){
//            enemy = new Hydra(1);
//        }
//        return enemy;
//    }


}