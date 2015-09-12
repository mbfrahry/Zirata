package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;

/**
 * Created by Max Bauer on 9/9/2015.
 */
public class BlankBlock extends Block {

    public BlankBlock(Vector2 postion){
        super(postion.x, postion.y, 10, 0);
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
}
