package xin.lrvik.plantsvszombies;

import android.view.MotionEvent;
import android.widget.Toast;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.transitions.CCJumpZoomTransition;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/15.
 */
public class ShopLayer extends CCLayer {

    private final CCSprite ccSprite_store_p;
    private final CCSprite ccSprite_store_n;
    private final CCSprite ccSprite_lawnmower;
    private final CCLabel ccLabel_tip;
    private int diamondsNum;
    private CCSprite ccSprite_diamond;
    private CCSprite ccSprite_tipbg2;
    private CCLabel ccLabel_diamond;

    public ShopLayer() {
        CGSize winSize = CCDirector.sharedDirector().getWinSize();
        CCSprite ccSprite_store_car = CCSprite.sprite("other/store_car.jpg");
        ccSprite_store_car.setAnchorPoint(0, 0);
        ccSprite_store_car.setScale(1.6f);
        ccSprite_store_car.setPosition(winSize.getWidth() / 2 - ccSprite_store_car.getBoundingBox().size.getWidth() / 2, 0);
        addChild(ccSprite_store_car);

        ccSprite_store_n = CCSprite.sprite("other/Store_N.png");
        ccSprite_store_n.setScale(1.6f);
        ccSprite_store_n.setAnchorPoint(0, 0);
        ccSprite_store_n.setPosition(800, 185);
        addChild(ccSprite_store_n);


        ccSprite_store_p = CCSprite.sprite("other/Store_P.png");
        ccSprite_store_p.setScale(1.6f);
        ccSprite_store_p.setAnchorPoint(0, 0);
        ccSprite_store_p.setPosition(240, 195);
        addChild(ccSprite_store_p);

        CCLabel ccLabel = CCLabel.makeLabel("返回", "", 40);
        ccLabel.setColor(ccColor3B.ccGREEN);
        ccLabel.setPosition(ccSprite_store_p.getPosition().x + 70, ccSprite_store_p.getPosition().y + 70);
        addChild(ccLabel);

        ccSprite_lawnmower = CCSprite.sprite("other/lawnmower.png");
        ccSprite_lawnmower.setScale(2f);
        ccSprite_lawnmower.setAnchorPoint(0, 0);
        ccSprite_lawnmower.setPosition(500, 350);
        addChild(ccSprite_lawnmower);

        ccLabel_tip = CCLabel.makeLabel("购买成功", "", 40);
        ccLabel_tip.setColor(ccColor3B.ccRED);
        ccLabel_tip.setPosition(winSize.getWidth() / 2, winSize.getHeight() - 140);
        addChild(ccLabel_tip);
        ccLabel_tip.setVisible(false);


        ccSprite_tipbg2 = CCSprite.sprite("other/tipbg.png");
        ccSprite_tipbg2.setPosition(winSize.getWidth() / 7 * 5 - ccSprite_tipbg2.getBoundingBox().size.getWidth() / 2 + 200,
                winSize.getHeight() - ccSprite_tipbg2.getBoundingBox().size.getHeight() / 2 - 30);
        addChild(ccSprite_tipbg2);
        ccSprite_diamond = CCSprite.sprite("other/diamond.png");
        //ccSprite_diamond.setContentSize(4,4);
        ccSprite_diamond.setScale(0.5);
        ccSprite_diamond.setPosition(ccp(ccSprite_tipbg2.getPosition().x - 30,
                ccSprite_tipbg2.getPosition().y));
        addChild(ccSprite_diamond);

        diamondsNum = (int) SPUtils.get(CCDirector.sharedDirector().getActivity(), "zs", 0);
        ccLabel_diamond = CCLabel.makeLabel(diamondsNum + "", "", 20);
        ccLabel_diamond.setColor(ccColor3B.ccGREEN);
        ccLabel_diamond.setPosition(ccSprite_diamond.getPosition().x + 50, ccSprite_diamond.getPosition().y);
        addChild(ccLabel_diamond);

        setIsTouchEnabled(true);
    }


    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        if (CGRect.containsPoint(ccSprite_lawnmower.getBoundingBox(), convertTouchToNodeSpace(event))) {
            boolean lm = (boolean) SPUtils.get(CCDirector.sharedDirector().getActivity(), "lm", false);
            if (!lm && diamondsNum > 10) {
                diamondsNum -= 10;
                ccLabel_diamond.setString(diamondsNum + "");
                SPUtils.put(CCDirector.sharedDirector().getActivity(), "zs", diamondsNum);
                SPUtils.put(CCDirector.sharedDirector().getActivity(), "lm", true);
                showText("购买成功");
            } else {
                showText("购买失败,钻石不足或已购买该道具！");
            }
        } else if (CGRect.containsPoint(ccSprite_store_p.getBoundingBox(), convertTouchToNodeSpace(event))) {
            CCScene ccScene = CCScene.node();
            ccScene.addChild(new MenuLayer());
            CCFadeTransition ccFadeTransition = CCFadeTransition.transition(2, ccScene);
            CCDirector.sharedDirector().runWithScene(ccFadeTransition);
        }

        return super.ccTouchesBegan(event);
    }

    private void showText(String text) {
        ccLabel_tip.setVisible(true);
        ccLabel_tip.setString(text);

        CCDelayTime ccDelayTime = CCDelayTime.action(2);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "hide");
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccCallFunc);
        runAction(ccSequence);
    }

    public void hide() {
        ccLabel_tip.setVisible(false);
    }
}
