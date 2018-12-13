package xin.lrvik.plantsvszombies.plant;


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

public class CherryBomb extends Plant {
    public CherryBomb() {
        super("plant/CherryBomb/Frame%02d.png", 7);
        setPrice(150);
        //setHP(200);

        CCDelayTime ccDelayTime = CCDelayTime.action(1);
        CCCallFunc boom = CCCallFunc.action(this, "boom");
        CCSequence actions = CCSequence.actions(ccDelayTime, boom);
        runAction(actions);
    }

    //爆炸
    public void boom() {
        stopAllActions();
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "plant/CherryBomb/boom/Frame%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.17f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, false);
        //runAction(ccAnimate);

        CCCallFunc removeZombie = CCCallFunc.action(this, "removeCherryBomb");
       // CCDelayTime ccDelayTime = CCDelayTime.action(2f);
        CCSequence ccSequence = CCSequence.actions(ccAnimate, removeZombie);
        runAction(ccSequence);
    }

    public void removeCherryBomb(){
        int col = (int) (getPosition().x - 320) / 105;
        int row = (int) (getPosition().y - 40) / 120;
        //((CombatLine) this.getParent()).removePlant(col);
        ArrayList<CombatLine> combatLines = ((CombatLayer) getParent().getParent()).getCombatLines();
        CombatLine combatLine = combatLines.get(row);
        combatLine.removePlant(col);

        //杀死周围的僵尸
        for (CombatLine line : combatLines) {
            Iterator<Zombie> iterator = line.getZombies().iterator();
            while (iterator.hasNext()) {
                Zombie zombie = iterator.next();
                if(CGPointUtil.distance(zombie.getPosition(),this.getPosition())<150){
                    zombie.die(1);
                    iterator.remove();
                }
            }
        }
    }
}
