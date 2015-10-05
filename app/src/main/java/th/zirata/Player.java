package th.zirata;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;

public class Player {

	public ArrayList<Block> playerBlocks = new ArrayList<Block>();
    public int energy;
	public boolean power;
	ArrayList<Block> poweredBlocks;
	Vector2 velocity;

	public Player(){
		for(Block b : PlayerSave.activeBlocks){
			playerBlocks.add(b);
		}
		energy = 0;

		power = true;
		poweredBlocks = new ArrayList<Block>();
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
		energy = currEnergy;
	}

	public void checkPower(){
		if(power == false){
			for(int i = 0; i < poweredBlocks.size(); i++){
				poweredBlocks.get(i).active = false;
			}
		}
		if(power == true){
			for(int i = 0; i < poweredBlocks.size(); i++){
				poweredBlocks.get(i).active = true;
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
