package th.zirata.Game;

import android.util.Log;

import java.util.ArrayList;

import th.zirata.Blocks.Block;
import th.zirata.Blocks.Bullet;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Blocks.Mine;
import th.zirata.Blocks.TurretBlock;
import th.zirata.EnemyShips.BNM;
import th.zirata.EnemyShips.BlockNest;
import th.zirata.EnemyShips.Enemy;
import th.zirata.EnemyShips.Hydra;
import th.zirata.EnemyShips.StandardEnemy;
import th.zirata.EnemyShips.StealthyHealthy;
import th.zirata.EnemyShips.SwirlyWhirly;
import th.zirata.EnemyShips.TEP;
import th.zirata.Settings.EnemySettings;

/**
 * Created by Max Bauer on 10/10/2015.
 */
public class EnemyManager {

    World world;
    public ArrayList<Enemy> enemies;
    public ArrayList<Bullet> enemyBullets;

    public EnemyManager(World world){
        this.world = world;
        this.enemies = new ArrayList<Enemy>();
        this.enemyBullets = new ArrayList<Bullet>();
    }

    public void update(float deltaTime){
        updateEnemies(deltaTime);
        updateEnemyBullets(deltaTime);
        checkEnemyBullets();
    }

    public void generateEnemy(){
        Enemy e = null;
        if(world.level.enemyNum > 0) {
            e = world.level.generateEnemy();
            World.popupManager.createSpriteExtra("sprite", "PotentialBlock", e.enemyBlocks.get(0).position.x, e.enemyBlocks.get(0).position.y, 25f, 25f,.3f, 0f);
            World.popupManager.createSpriteExtra("sprite", "BaseBlock", e.enemyBlocks.get(0).position.x, e.enemyBlocks.get(0).position.y, 35f,35f,.2f, 0f);
            World.popupManager.createSpriteExtra("sprite", "PotentialBlock", e.enemyBlocks.get(0).position.x, e.enemyBlocks.get(0).position.y, 45f,45f,.1f, 0f);


            if(world.level.enemyNum == 0){
                //TODO: NEED TO CHANGE THIS TO ADD SPECIFIC ANIMATION FOR A BOSS FIGHT
                if(world.level.bossType >= 0){
                    World.popupManager.createTextExtra("text", "The", 160, 350, 25, 25, 2f, "red", "center");
                    String bossName = "";
                    if(world.level.bossType == 1) {
                        bossName = "Hydra";
                    }
                    else if(world.level.bossType == 2){
                        bossName = "Orbiter";
                    }
                    else if(world.level.bossType == 3){
                        bossName = "T.E.P.";
                    }
                    else if(world.level.bossType == 4){
                        bossName = "Rewinder";
                    }
                    else if(world.level.bossType == 5){
                        bossName = "Loch Ness";
                    }
                    World.popupManager.createTextExtra("text", bossName, 160, 300, 25, 25, 2f, "red", "center");
                    World.popupManager.createTextExtra("text", "approaches", 160, 250, 25, 25, 2f, "red", "center");
                }

            }
        }
        else{
            world.state = World.WORLD_STATE_LAST_ENEMY;
            if(world.level.bossType >= 0 && world.lastEnemyTime > 2) {
                if(world.level.bossType == 1) {
                    e = new Hydra(1, world.world_cos, world.world_sin);
                }
                else if(world.level.bossType == 2){
                    e = new SwirlyWhirly(5);
                }
                else if (world.level.bossType == 3){
                    e = new TEP(1, world.world_cos, world.world_sin);
                }
                else if (world.level.bossType == 4){
                    e = new StealthyHealthy(2);
                }
                else if (world.level.bossType == 5){
                    e = new BNM(1, world.world_cos, world.world_sin);
                }
                else{
                    Log.d("this", "is a bad thing");
                }
            }
        }
        if(e != null){
//            for (Block b : e.enemyBlocks) {
//                if(e.constantVelocity){
//                    //b.rotateConstantVelocity(world.world_sin, world.world_cos, world.WORLD_MID_POINT);
//                }
//                else{
//                    //b.rotate(world.world_sin, world.world_cos, world.WORLD_MID_POINT);
//                }
//            }
            enemies.add(e);
        }

    }

    public void generateEnemy(int type, int level, float x, float y){
        //Make this generate an enemy at a specific location
        if(type == EnemySettings.STANDARD_ENEMY){
            enemies.add(new StandardEnemy(level, x, y));
        }
    }

    public void generateEnemyBullet(EnemyTurretBlock tBlock){
        if(tBlock.state == TurretBlock.TURRET_READY && tBlock.bullets.size() < 1){
            if(world.player.playerBlocks.size() > 0){
                Block randBlock = world.player.playerBlocks.get(Math.abs(world.rand.nextInt()) % world.player.playerBlocks.size());
                enemyBullets.add(new Bullet(tBlock.position.x, tBlock.position.y,randBlock.position.x, randBlock.position.y, tBlock.bulletDamage, tBlock.fireRange));
                tBlock.resetBlock();
            }
        }
    }

    public void updateEnemies(float deltaTime){
        world.lastEnemyTime += deltaTime;
        if(world.lastEnemyTime > world.timeToNextEnemy && world.state != World.WORLD_STATE_LAST_ENEMY){
            world.enemyManager.generateEnemy();

            if(world.enemiesKilled % 4 == 0 && world.timeToNextEnemy > 2){
                world.timeToNextEnemy -= 0.5;
            }

            world.lastEnemyTime = 0;
        }

        for(int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            for(int j = 0; j < enemy.enemyBlocks.size(); j++){
                Block currBlock = enemy.enemyBlocks.get(j);

                if(world.moveLeft || world.moveRight) {
                    if(enemy.constantVelocity){
                        currBlock.rotateConstantVelocity(world.enemyAngle, World.POS_COS_ANGLE, world.WORLD_MID_POINT);
                    }
                    else {
                        currBlock.rotate(world.enemyAngle, World.POS_COS_ANGLE, world.WORLD_MID_POINT);
                    }
                }

                if(currBlock.getClass().equals(EnemyTurretBlock.class)){
                    EnemyTurretBlock tBlock = (EnemyTurretBlock) currBlock;
                    generateEnemyBullet(tBlock);
                }
            }
            enemy.update(deltaTime, world);

            if(enemy.checkDead()){
                enemies.remove(i);
                world.enemiesKilled += 1;
                world.createCurrency();
            }
        }
    }

    public void checkEnemyBullets(){
        Bullet b;
        for(int i = 0; i < enemyBullets.size(); i++){

            b = enemyBullets.get(i);
            if(world.checkPlayerCollision(b)){
                enemyBullets.remove(i);
            }
        }
    }

    public void updateEnemyBullets(float deltaTime){
        for(int i = 0; i < enemyBullets.size(); i++){
            Bullet b = enemyBullets.get(i);
            if (world.moveLeft || world.moveRight) {
                if(b.getClass().equals(Mine.class)){
                    b.rotateConstantVelocity(world.enemyAngle, World.POS_COS_ANGLE, world.WORLD_MID_POINT);
                }
                else{
                    b.rotate(world.enemyAngle, World.POS_COS_ANGLE, world.WORLD_MID_POINT);
                }

            }
            b.update(deltaTime);
            if(b.outOfBounds()){
                enemyBullets.remove(i);
            }
        }
    }
}
