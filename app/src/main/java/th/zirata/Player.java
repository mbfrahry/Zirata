package th.zirata;

import java.util.ArrayList;

import android.util.Log;

public class Player {

	public ArrayList<Block> playerBlocks = new ArrayList<Block>();
    
	public Player(){
		for(int i = 0; i < PlayerSave.playerBlocks.size(); i++){
			Block currBlock = PlayerSave.playerBlocks.get(i);
			playerBlocks.add(currBlock);
		}
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
