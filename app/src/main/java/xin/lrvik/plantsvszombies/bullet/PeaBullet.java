package xin.lrvik.plantsvszombies.bullet;


import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;

import xin.lrvik.plantsvszombies.Zombie;
import xin.lrvik.plantsvszombies.plant.ShooterPlant;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/2.
 */
public class PeaBullet extends Bullet {
    public PeaBullet(ShooterPlant shooterPlant) {
        super("bullet/bullet1.png", shooterPlant);
    }

    @Override
    public void showBulletBlast(Zombie zombie) {
        CCSprite ccSprite_bulletBlast = CCSprite.sprite("bullet/bulletBlast1.png");
        ccSprite_bulletBlast.setPosition(ccp(zombie.getPosition().x, zombie.getPosition().y + 60));
        getParent().addChild(ccSprite_bulletBlast,6);
        CCDelayTime ccDelayTime = CCDelayTime.action(0.1f);
        CCHide ccHide = CCHide.action();
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccHide);
        ccSprite_bulletBlast.runAction(ccSequence);
    }
}
