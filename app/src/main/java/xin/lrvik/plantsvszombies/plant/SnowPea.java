package xin.lrvik.plantsvszombies.plant;


import xin.lrvik.plantsvszombies.bullet.SnowBullet;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class SnowPea extends ShooterPlant {
    public SnowPea() {
        super("plant/SnowPea/Frame%02d.png", 15);
        setPrice(175);
    }

    @Override
    public void createBullet(float t) {
        new SnowBullet(this);
    }
}
