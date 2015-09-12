package th.zirata;

import java.util.ArrayList;

public class Player {

	public ArrayList<Block> playerBlocks = new ArrayList<Block>();
    public int energy;

	public Player(){
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			Block currBlock = PlayerSave.activeBlocks.get(i);
			playerBlocks.add(currBlock);
		}
		energy = 3;
	}

	public void getEnergy(){
		int currEnergy = 0;
		for(int i = 0; i < playerBlocks.size(); i++){
			if(playerBlocks.get(i).getClass().equals(EnergyBlock.class)){
				EnergyBlock currBlock = (EnergyBlock) playerBlocks.get(i);
				currEnergy += currBlock.energy;
			}
			if(playerBlocks.get(i).active){
				currEnergy -= playerBlocks.get(i).energyCost;
			}
		}
		energy = currEnergy;
	}
	
	public void update(float deltaTime){

		getEnergy();
		Block currBlock;
		for(int i = 0; i < playerBlocks.size(); i++){			
			currBlock = playerBlocks.get(i);
			currBlock.bounds.lowerLeft.set(currBlock.position).sub(currBlock.BLOCK_WIDTH/2, currBlock.BLOCK_HEIGHT/2);
			currBlock.update(deltaTime);
			if(currBlock.checkDeath()){
				playerBlocks.remove(i);
			}
		}
	}
}
