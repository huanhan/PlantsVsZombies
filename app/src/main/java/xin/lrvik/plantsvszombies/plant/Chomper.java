package xin.lrvik.plantsvszombies.plant;


import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;

import java.util.ArrayList;
import java.util.Locale;

import xin.lrvik.plantsvszombies.Plant;
import xin.lrvik.plantsvszombies.Zombie;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class Chomper extends Plant {
    private Zombie zombie;

    public Chomper() {
        super("plant/Chomper/Frame%02d.png", 13);
        setPrice(150);
    }

    private boolean isEat = false;

    public boolean isEat() {
        return isEat;
    }

    public void eat(Zombie zombie) {
        this.zombie = zombie;
        isEat = true;
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "plant/Chomper/attack/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation1 = CCAnimation.animationWithFrames(frames, 0.15f);
        CCAnimate ccAnimate1 = CCAnimate.action(ccAnimation1, false);
        CCCallFunc eatZombie = CCCallFunc.action(this, "eatZombie");
        CCSequence actions = CCSequence.actions(ccAnimate1, eatZombie);
        runAction(actions);
    }

    public void eatZombie() {
        this.zombie.removeSelf();
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "plant/Chomper/eat/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation2 = CCAnimation.animationWithFrames(frames, 0.15f);
        CCAnimate ccAnimate2 = CCAnimate.action(ccAnimation2, true);
        CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate2);
        runAction(ccRepeatForever);
        //食人花吃东西时间
        CCDelayTime ccDelayTime = CCDelayTime.action(10);
        CCCallFunc setNormal = CCCallFunc.action(this, "setNormal");
        CCSequence actions = CCSequence.actions(ccDelayTime, setNormal);
        runAction(actions);
    }


    public void setNormal() {
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "plant/Chomper/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.2f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, true);
        CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate);
        runAction(ccRepeatForever);
        isEat = false;
        this.zombie = null;
    }


}
