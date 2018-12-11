package xin.lrvik.plantsvszombies.plant;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;

import xin.lrvik.plantsvszombies.Plant;
import xin.lrvik.plantsvszombies.bullet.Bullet;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/2.
 */
public abstract class ShooterPlant extends Plant {

    private final ArrayList<Bullet> bullets;
    private boolean isAttack;

    public ShooterPlant(String format, int number) {
        super(format, number);
        bullets = new ArrayList<>();
    }

    public void attackZombie() {
        if (!isAttack) {
            CCScheduler.sharedScheduler().schedule("createBullet", this, 5, false);
            isAttack = true;
        }
    }

    public void stopAttackZombie() {
        if (isAttack) {
            CCScheduler.sharedScheduler().unschedule("createBullet", this);
            isAttack = false;
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }


    public abstract void createBullet(float t);

}
