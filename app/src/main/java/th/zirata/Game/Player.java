package th.zirata.Game;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;

import th.zirata.Blocks.Block;
import th.zirata.Blocks.EnergyBlock;
import th.zirata.Blocks.TurretBlock;
import th.zirata.Settings.Assets;
import th.zirata.Settings.PlayerSave;
import th.zirata.Settings.Settings;

public class Player {

	public ArrayList<Block> playerBlocks = new ArrayList<Block>();
    public int energy;
	public boolean power;
	public ArrayList<Block> poweredBlocks;
	Vector2 velocity;
	public static Vector2 playerSpeed;

	public Player(){
		for(Block b : PlayerSave.activeBlocks){
			playerBlocks.add(b);
		}
		energy = 0;

		power = true;
		poweredBlocks = new ArrayList<Block>();
		playerSpeed = new Vector2(0, -10);
	}

	public void getEnergy(){
		int currEnergy = 0;
		for(int i = 0; i < playerBlocks.size(); i++){
			if(playerBlocks.get(i).getClass().equals(EnergyBlock.class)){
				EnergyBlock currBlock = (EnergyBlock) playerBlocks.get(i);
				currEnergy += currBlock.energy;
			}
			else if(playerBlocks.get(i).active){
				currEnergy -= playerBlocks.get(i).energyCost;
			}
		}
		//Need to figure out how to account for speed
//		currEnergy += Math.floor(playerSpeed.y) + 10;

		energy = currEnergy;
	}

	public void checkPower(){
		if(!power){
			for(int i = 0; i < poweredBlocks.size(); i++){
				poweredBlocks.get(i).active = false;
			}
		}
		if(power){
			for(int i = 0; i < poweredBlocks.size(); i++){
				//if(energy - poweredBlocks.get(i).energyCost >= 0){
					poweredBlocks.get(i).active = true;
				//	energy -= poweredBlocks.get(i).energyCost;
				//}
			//	else{
			//		poweredBlocks.remove(i);
			//	}
			}
		}
	}
	
	public void update(float deltaTime){

		getEnergy();
		Block currBlock;
		for(int i = 0; i < playerBlocks.size(); i++){			
			currBlock = playerBlocks.get(i);
			//currBlock.bounds.lowerLeft.set(currBlock.position).sub(currBlock.BLOCK_WIDTH/2, currBlock.BLOCK_HEIGHT/2);
			currBlock.update(deltaTime);
			if(currBlock.checkDeath()){
				World.popupManager.createExplosion(currBlock.position.x, currBlock.position.y, 50);
				Assets.playSound(Assets.explosionSound);
//				if(currBlock.getClass().equals(EnergyBlock.class)){
//					EnergyBlock e = (EnergyBlock) currBlock;
//					playerSpeed.y += e.energy;
//					if (playerSpeed.y > -10){
//						playerSpeed.y = -10;
//					}
//				}
				playerBlocks.remove(i);
			}
		}
		checkPower();
	}

	public void turnOnTurrets(){
		int energySum = 0;
		for (int i = 0; i < playerBlocks.size(); i++){
			Block currBlock = playerBlocks.get(i);
			if (currBlock.getClass().equals(TurretBlock.class)){
				if (energySum + currBlock.energyCost <= energy){
					currBlock.active = true;
					poweredBlocks.add(currBlock);
					energySum += currBlock.energyCost;
				}
			}

		}
	}
}
