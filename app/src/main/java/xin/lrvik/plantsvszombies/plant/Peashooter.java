package xin.lrvik.plantsvszombies.plant;


import xin.lrvik.plantsvszombies.bullet.PeaBullet;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class Peashooter extends ShooterPlant {
    public Peashooter() {
        super("plant/Peashooter/Frame%02d.png", 13);
        setPrice(100);
    }

    @Override
    public void createBullet(float t) {
        new PeaBullet(this);
    }
}
