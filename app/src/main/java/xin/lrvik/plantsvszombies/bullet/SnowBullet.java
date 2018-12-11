package xin.lrvik.plantsvszombies.bullet;


import xin.lrvik.plantsvszombies.Zombie;
import xin.lrvik.plantsvszombies.plant.ShooterPlant;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/2.
 */
public class SnowBullet extends Bullet {
    public SnowBullet(ShooterPlant shooterPlant) {
        super("bullet/bullet2.png", shooterPlant);
    }

    @Override
    public void showBulletBlast(Zombie zombie) {
        zombie.slow();
    }
}
