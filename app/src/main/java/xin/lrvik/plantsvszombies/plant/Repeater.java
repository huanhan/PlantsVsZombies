package xin.lrvik.plantsvszombies.plant;


import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;

import xin.lrvik.plantsvszombies.bullet.PeaBullet;


/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class Repeater extends ShooterPlant {
    public Repeater() {
        super("plant/Repeater/Frame%02d.png", 15);
        setPrice(200);
    }


    @Override
    public void createBullet(float t) {
        new PeaBullet(this);
        CCDelayTime ccDelayTime = CCDelayTime.action(0.5f);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "createBulletTwo");
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccCallFunc);
        runAction(ccSequence);
    }

    public void createBulletTwo(){
        new PeaBullet(this);
    }
}
