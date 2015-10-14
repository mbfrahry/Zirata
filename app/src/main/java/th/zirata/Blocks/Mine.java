package th.zirata.Blocks;

/**
 * Created by Max Bauer on 10/13/2015.
 */
public class Mine extends Bullet {

    public static final int MINE_WIDTH = 10;
    public static final int MINE_HEITH = 10;

    public Mine(float x, float y, float targX, float targY, int damage, float range) {
        super(x, y, MINE_WIDTH, MINE_HEITH, targX, targY, damage, range);
        velocity.set(0, -10);
    }
}
