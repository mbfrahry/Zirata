package th.zirata;

/**
 * Created by Matthew on 6/6/2015.
 */
public class EnergyBlock extends Block{

    public int energy;

    public EnergyBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2], (int)info[3], (int)info[4]);
    }

    public EnergyBlock(float x, float y, int health, int energyCost, int energy){
        super(x, y, health, energyCost);
        this.energy = energy;
    }

}
