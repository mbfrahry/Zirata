package th.zirata;

public class ArmorBlock extends Block{

	int constructorArgLength = 4;

	public ArmorBlock(double[] info){
		this((float)info[0], (float)info[1], (int)info[2]);
	}

	public ArmorBlock(float x, float y, int health){
		super(x, y, health, 0);
	}

	public void multiply(float multiplier){
		this.health *= multiplier;
	}
}
