package xin.lrvik.plantsvszombies;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/15.
 */
public class LawnMower extends CCSprite {

    private float speed = 340;
    private final CGPoint end;
    private State state = State.WAIT;

    public enum State{
        WAIT,RUNNING,END
    }

    public LawnMower(CGPoint start, CGPoint end) {
        super("other/lawnmower.png");
        setPosition(start);
        setAnchorPoint(0.4f, 0f);
        this.end = end;
    }

    public void move() {
        this.state = State.RUNNING;
        float t = CGPointUtil.distance(getPosition(), end) / speed;
        CCMoveTo ccMoveTo = CCMoveTo.action(t, end);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "remove");
        CCSequence ccSequence = CCSequence.actions(ccMoveTo, ccCallFunc);
        runAction(ccSequence);
    }

    public void remove() {
        removeSelf();
        this.state = State.END;
    }

    public State getState() {
        return state;
    }
}
