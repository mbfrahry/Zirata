package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;

public class ArmorBlock extends Block{

	public static String[] UpgradeAttributes = new String[]{"Health", "Shield", "Macho"};

	int constructorArgLength = 4;

	public ArmorBlock(Vector2 position){
		this(position.x, position.y, 20);
	}

	public ArmorBlock(double[] info){
		this((float)info[0], (float)info[1], (int)info[2]);
	}

	public ArmorBlock(float x, float y, int health){
		super(x, y, health, 0);
	}

	@Override
	public void action(World world) {

	}

	@Override
	public void update(float deltaTime) {

	}

	@Override
	public void writeExtraInfo(JsonWriter writer) throws IOException {

	}

	@Override
	public String[] getUpgradableAttributes() {
		return UpgradeAttributes;
	}

	public void multiply(float multiplier){
		this.health *= multiplier;
	}
}
