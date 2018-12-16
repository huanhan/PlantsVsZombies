package xin.lrvik.plantsvszombies;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.base.CCSpeed;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCTintTo;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/19.
 */
public class Zombie extends CCSprite {
    private final int row;
    private float speed = 15;
    private final CGPoint end;
    private final CombatLayer combatLayer;
    private int attack = 10;
    private State state;
    private boolean isSlow;
    private int HP = 100;

    public void slow() {
        isSlow = true;
        setAttack(2);
        stopAllActions();
        switch (getState()) {
            case MOVE:
                move();
                break;
            case ATTACK:
                attack();
                break;
        }
        CCTintTo ccTintTo1 = CCTintTo.action(0.1f, ccc3(150, 150, 255));
        CCDelayTime ccDelayTime = CCDelayTime.action(3);
        CCTintTo ccTintTo2 = CCTintTo.action(0.1f, ccc3(255, 255, 255));
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "normal");
        CCSequence ccSequence = CCSequence.actions(ccTintTo1, ccDelayTime, ccTintTo2, ccCallFunc);
        runAction(ccSequence);
    }

    public void normal() {
        isSlow = false;
        setAttack(10);
        stopAllActions();
        switch (getState()) {
            case MOVE:
                move();
                break;
            case ATTACK:
                attack();
                break;
        }
    }

    public void die(int mode) {
        stopAllActions();

        ArrayList<CCSpriteFrame> frames = new ArrayList<>();

        if (mode == 1) {
            for (int i = 0; i < 19; i++) {
                CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                        "zombies/zombies_1/ashes/Frame%02d.png", i)).displayedFrame();

                frames.add(ccSpriteFrame);
            }
        } else {
            for (int i = 0; i < 9; i++) {
                CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                        "zombies/zombies_1/die/Frame%02d.png", i)).displayedFrame();
                frames.add(ccSpriteFrame);
            }

        }

        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.15f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, false);

        CCCallFunc removeZombie = CCCallFunc.action(this, "removeZombie");
        CCSequence ccSequence = CCSequence.actions(ccAnimate, removeZombie);

        runAction(ccSequence);
    }

    public void removeZombie() {
        removeSelf();
    }

    public void hurtCompute(int attack) {
        HP -= attack;
        if (HP < 0) {
            HP = 0;
            //die();
        }
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public enum State {
        MOVE, ATTACK, DIE
    }

    public Zombie(CombatLayer combatLayer, CGPoint start, CGPoint end,int row) {
        super("zombies/zombies_1/walk/Frame00.png");
        setAnchorPoint(0.5f, 0);
        setPosition(start);
        this.combatLayer = combatLayer;
        this.end = end;
        this.row = row;
        SoundEngine.sharedEngine().playEffect(CCDirector.theApp, R.raw.groan4, false);
        move();
    }

    public void move() {
        float t = CGPointUtil.distance(getPosition(), end) / speed;
        CCMoveTo ccMoveTo = CCMoveTo.action(t, end);
        CCCallFunc ccCallFunc = CCCallFuncND.action(combatLayer, "end",row);
        CCSequence ccSequence = CCSequence.actions(ccMoveTo, ccCallFunc);
        if (isSlow) {
            CCSpeed ccSpeed = CCSpeed.action(ccSequence, 0.2f);
            runAction(ccSpeed);
        } else {
            runAction(ccSequence);
        }
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "zombies/zombies_1/walk/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }

        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.1f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, true);
        CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate);
        if (isSlow) {
            CCSpeed ccSpeed = CCSpeed.action(ccRepeatForever, 0.2f);
            runAction(ccSpeed);
        } else {
            runAction(ccRepeatForever);
        }
        setState(State.MOVE);
    }

    public void attack() {
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "zombies/zombies_1/attack/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }

        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.1f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, true);
        CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate);
        if (isSlow) {
            CCSpeed ccSpeed = CCSpeed.action(ccRepeatForever, 0.2f);
            runAction(ccSpeed);
        } else {
            runAction(ccRepeatForever);
        }
        setState(State.ATTACK);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
