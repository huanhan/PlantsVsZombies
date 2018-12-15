package xin.lrvik.plantsvszombies.plant;


import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.util.CGPointUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import xin.lrvik.plantsvszombies.CombatLayer;
import xin.lrvik.plantsvszombies.CombatLine;
import xin.lrvik.plantsvszombies.Plant;
import xin.lrvik.plantsvszombies.Zombie;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class PotatoMine extends Plant {

    private boolean isBig = false;
    private boolean isBoom = false;
    private ArrayList<Zombie> zombies;

    public PotatoMine() {
        super("plant/PotatoMine/mini/Frame%02d.png", 1);
        setPrice(25);
        //十秒就长大
        CCDelayTime ccDelayTime = CCDelayTime.action(10);
        CCCallFunc growup = CCCallFunc.action(this, "growup");
        CCSequence actions = CCSequence.actions(ccDelayTime, growup);
        runAction(actions);
    }

    public void growup() {
        isBig = true;
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "plant/PotatoMine/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.2f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, true);
        CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate);
        runAction(ccRepeatForever);
    }

    public boolean isBig() {
        return isBig;
    }

    public void boom() {
        isBoom = true;
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "plant/PotatoMine/boom/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.4f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, false);

        CCCallFunc removeZombie = CCCallFunc.action(this, "removePotatoMine");
        // CCDelayTime ccDelayTime = CCDelayTime.action(2f);
        CCSequence ccSequence = CCSequence.actions(ccAnimate, removeZombie);
        runAction(ccSequence);
    }

    public void removePotatoMine() {
        int col = (int) (getPosition().x - 320) / 105;
        int row = (int) (getPosition().y - 40) / 120;
        //((CombatLine) this.getParent()).removePlant(col);
        ArrayList<CombatLine> combatLines = ((CombatLayer) getParent().getParent()).getCombatLines();
        CombatLine combatLine = combatLines.get(row);
        combatLine.removePlant(col);
        ArrayList<Zombie> zombies = combatLine.getZombies();
        //杀死周围的僵尸
        Iterator<Zombie> iterator = zombies.iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            if (CGPointUtil.distance(zombie.getPosition(), this.getPosition()) < 50) {
                zombie.die(1);
                ((CombatLayer) zombie.getParent().getParent()).setKillZombiesNum();
                iterator.remove();
            }
        }

    }

    public boolean isBoom() {
        return isBoom;
    }
}
