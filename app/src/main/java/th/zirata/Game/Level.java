package th.zirata.Game;

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
}
