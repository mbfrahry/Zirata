package th.zirata.Game;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.EnemyShips.BNM;
import th.zirata.EnemyShips.BlockNest;
import th.zirata.EnemyShips.Enemy;
import th.zirata.EnemyShips.EnemyTeleporter;
import th.zirata.EnemyShips.Hydra;
import th.zirata.EnemyShips.MineEnemy;
import th.zirata.EnemyShips.Spinner;
import th.zirata.EnemyShips.StandardEnemy;
import th.zirata.EnemyShips.StealthyHealthy;
import th.zirata.EnemyShips.SwirlyWhirly;
import th.zirata.EnemyShips.TEP;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Max Bauer on 12/23/2015.
 */
public class InfiniteEnemyManager extends EnemyManager {

    double[] enemyRates;
    int[] enemyLevels;
    int enemiesToBoss;
    int enemiesToLevelUp;
    Random rand = new Random();

    public InfiniteEnemyManager(World world){
        super(world);
        enemyRates = new double[EnemySettings.ENEMYTYPES];
        Arrays.fill(enemyRates, .2);
        enemyLevels = new int[EnemySettings.ENEMYTYPES];
        Arrays.fill(enemyLevels, 1);
        enemiesToBoss = 20;
        enemiesToLevelUp = 50;
    }

    public void generateEnemy(){
        double newRand = rand.nextDouble();
        int type = 0;
        Enemy newEnemy = null;
        if(enemiesToBoss <= 0){
            Log.d("EnemiesKilled", world.enemiesKilled +" rand is " + newRand);
            for(int i = 0; i < 5; i++){
                newRand -= .2;
                if(newRand <= 0){
                    type = i;
                    break;
                }
            }
            Log.d("TYPE: ", type + " " );
            World.popupManager.createTextExtra("text", "The", 160, 350, 25, 25, 2f, "red", "center");
            String bossName = "";
            if(type == 0) {
                bossName = "Hydra";
                newEnemy = new Hydra(1, world.world_cos, world.world_sin);
            }
            else if(type == 1){
                bossName = "Orbiter";
                newEnemy = new SwirlyWhirly(5);
            }
            else if(type == 2){
                bossName = "T.E.P.";
                newEnemy =new TEP(1, world.world_cos, world.world_sin);
            }
            else if(type == 3){
                bossName = "Rewinder";
                newEnemy = new StealthyHealthy(2);
            }
            else if(type == 4){
                bossName = "Loch Ness";
                newEnemy = new BNM(1, world.world_cos, world.world_sin);
            }
            World.popupManager.createTextExtra("text", bossName, 160, 300, 25, 25, 2f, "red", "center");
            World.popupManager.createTextExtra("text", "approaches", 160, 250, 25, 25, 2f, "red", "center");
            enemiesToBoss = 20;
        }
        else{
            for(int i = 0; i < enemyRates.length; i++){
                newRand -= enemyRates[i];
                if (newRand <= 0){
                    type = i;
                    break;
                }
            }
            Log.d("Level", enemyLevels[type] + " " );
            if (type == EnemySettings.STANDARD_ENEMY){
                newEnemy = new StandardEnemy(enemyLevels[type]);
            }
            else if(type == EnemySettings.TELEPORTER_ENEMY){
                newEnemy = new EnemyTeleporter(enemyLevels[type]);
            }
            else if(type == EnemySettings.SPINNER_ENEMY){
                newEnemy = new Spinner(enemyLevels[type]);
            }
            else if (type == EnemySettings.MINE_ENEMY){
                newEnemy = new MineEnemy(enemyLevels[type]);
            }
            else if (type == EnemySettings.BLOCK_NEST_ENEMY){
                newEnemy = new BlockNest(enemyLevels[type]);
            }
            else{

            }
            enemiesToBoss -= 1;
            enemiesToLevelUp -= 1;
        }
        enemies.add(newEnemy);

    }

    public void update(float deltaTime){
        super.update(deltaTime);
        if(enemiesToLevelUp < 0){
            Arrays.fill(enemyLevels, enemyLevels[0] +=1);
            enemiesToLevelUp = 50;
        }
    }



}
