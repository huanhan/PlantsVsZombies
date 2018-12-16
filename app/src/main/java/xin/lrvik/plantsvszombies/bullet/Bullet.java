package xin.lrvik.plantsvszombies.bullet;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import xin.lrvik.plantsvszombies.R;
import xin.lrvik.plantsvszombies.Zombie;
import xin.lrvik.plantsvszombies.plant.ShooterPlant;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/2.
 */
public abstract class Bullet extends CCSprite {

    private int speed = 100;
    private int attack = 20;

    private ShooterPlant shooterPlant;

    public Bullet(String filepath, ShooterPlant shooterPlant) {
        super(filepath);
        this.shooterPlant = shooterPlant;
        SoundEngine.sharedEngine().playEffect(CCDirector.theApp, R.raw.shoop, false);
        setPosition(shooterPlant.getPosition().x + 20, shooterPlant.getPosition().y + 50);
        shooterPlant.getParent().addChild(this, 6);
        shooterPlant.getBullets().add(this);
        move();
    }

    private void move() {
        CGPoint end = ccp(1400, getPosition().y);
        float t = CGPointUtil.distance(getPosition(), end) / speed;
        CCMoveTo ccMoveTo = CCMoveTo.action(t, end);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "end");
        CCSequence ccSequence = CCSequence.actions(ccMoveTo, ccCallFunc);
        runAction(ccSequence);
    }

    public void end() {
        removeSelf();
        shooterPlant.getBullets().remove(this);
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public abstract void showBulletBlast(Zombie zombie);
}
