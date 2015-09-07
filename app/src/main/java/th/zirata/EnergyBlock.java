package th.zirata;

/**
 * Created by Matthew on 6/6/2015.
 */
public class EnergyBlock extends Block{

    public int energy;

    public EnergyBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2], (int)info[3]);
    }

    public EnergyBlock(float x, float y, int health, int energy){
        super(x, y, health, 0);
        this.energy = energy;
    }

}
