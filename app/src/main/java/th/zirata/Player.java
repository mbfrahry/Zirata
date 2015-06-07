package th.zirata;

import java.util.ArrayList;

import android.util.Log;

import com.badlogic.androidgames.framework.math.Vector2;

public class Player {

	public ArrayList<Block> playerBlocks = new ArrayList<Block>();
    public int energy;

	public Player(){
		for(int i = 0; i < PlayerSave.playerBlocks.size(); i++){
			Block currBlock = PlayerSave.playerBlocks.get(i);
			playerBlocks.add(currBlock);
		}
		energy = 3;
	}

	public void getEnergy(){
		int currEnergy = 0;
		for(int i = 0; i < playerBlocks.size(); i++){
			if(playerBlocks.get(i).getClass().equals(EnergyBlock.class)){
				EnergyBlock currBlock = (EnergyBlock) playerBlocks.get(i);

				Log.d("Velocity", currBlock.energy + " ");
				currEnergy += currBlock.energy;
			}
		}
		energy = currEnergy;
	}
	
	public void update(float deltaTime){
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
