package xin.lrvik.plantsvszombies;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/12.
 */
public class Diamond extends CCSprite {

    public Diamond() {
        super("other/diamond.png");
    }

    public void collect(CGPoint position) {
        float t = CGPointUtil.distance(getPosition(), position) / 1000;
        CCMoveTo ccMoveTo = CCMoveTo.action(t, position);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "addDiamondNumber");
        CCSequence ccSequence = CCSequence.actions(ccMoveTo, ccCallFunc);
        runAction(ccSequence);
    }

    public void addDiamondNumber() {
        ((CombatLayer) getParent()).addDiamondNumber();
        removeSelf();
    }
}
