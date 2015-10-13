package th.zirata.Game;

import th.zirata.EnemyShips.Enemy;
import th.zirata.EnemyShips.EnemyTeleporter;
import th.zirata.EnemyShips.Spinner;
import th.zirata.EnemyShips.StandardEnemy;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Matthew on 10/7/2015.
 */
public class Level {

    int[] enemyLevels;
    double[] enemyChance;
    int enemyNum;
    int bossType;

    public Level(int enemyNum, int[] enemyLevels, double[] enemyChance, int bossType){
        this.enemyNum = enemyNum;
        this.enemyLevels = enemyLevels;
        this.enemyChance = enemyChance;
        this.bossType = bossType;
    }

    public Enemy generateEnemy(){
        //Decide which type of enemy to produce
        double rand = Math.random();
        int type = 0;
        //Does this work?
        for(int i = 0; i < enemyChance.length; i++){
            rand -= enemyChance[i];
            if (rand <= 0){
                type = i;
                break;
            }
        }
        Enemy newEnemy = null;
        if (type == EnemySettings.STANDARD_ENEMY){
            newEnemy = new StandardEnemy(enemyLevels[type]);
        }
        else if(type == EnemySettings.TELEPORTER_ENEMY){
            newEnemy = new EnemyTeleporter(enemyLevels[type]);
        }
        else if(type == EnemySettings.SPINNER_ENEMY){
            newEnemy = new Spinner(enemyLevels[type]);
        }
        else{

        }
        //Make call to new enemy creation
        enemyNum--;
        return newEnemy;
    }
}
