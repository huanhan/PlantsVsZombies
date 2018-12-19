package xin.lrvik.plantsvszombies;

import android.view.MotionEvent;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/12/16.
 */
public class PauseLayer extends CCLayer {

    private final CCSprite ccSprite_button1;
    private final CCSprite ccSprite_button11;
    private final CCSprite ccSprite_button12;

    public PauseLayer() {

        CGSize winSize = CCDirector.sharedDirector().getWinSize();
        CCSprite ccSprite_menuback = CCSprite.sprite("other/menuback.png");
        ccSprite_menuback.setPosition(winSize.getWidth() / 2, winSize.getHeight() / 2);
        addChild(ccSprite_menuback);

        ccSprite_button1 = CCSprite.sprite("other/button.png");
        ccSprite_button1.setPosition(ccSprite_menuback.getPosition().x, ccSprite_menuback.getPosition().y - 190);
        addChild(ccSprite_button1);

        CCLabel ccLabel_back = CCLabel.makeLabel("返回游戏", "", 20);
        ccLabel_back.setColor(ccColor3B.ccGREEN);
        ccLabel_back.setPosition(ccSprite_button1.getPosition().x, ccSprite_button1.getPosition().y);
        addChild(ccLabel_back);

        ccSprite_button11 = CCSprite.sprite("other/button.png");
        ccSprite_button11.setScale(0.6f);
        ccSprite_button11.setPosition(ccSprite_menuback.getPosition().x, ccSprite_menuback.getPosition().y - 100);
        addChild(ccSprite_button11);

        CCLabel ccLabel_restart = CCLabel.makeLabel("重新开始", "", 20);
        ccLabel_restart.setColor(ccColor3B.ccGREEN);
        ccLabel_restart.setPosition(ccSprite_button11.getPosition().x, ccSprite_button11.getPosition().y);
        addChild(ccLabel_restart);

        ccSprite_button12 = CCSprite.sprite("other/button.png");
        ccSprite_button12.setScale(0.6f);
        ccSprite_button12.setPosition(ccSprite_menuback.getPosition().x, ccSprite_menuback.getPosition().y - 50);
        addChild(ccSprite_button12);

        CCLabel ccLabel_menu = CCLabel.makeLabel("返回主菜单", "", 20);
        ccLabel_menu.setColor(ccColor3B.ccGREEN);
        ccLabel_menu.setPosition(ccSprite_button12.getPosition().x, ccSprite_button12.getPosition().y);
        addChild(ccLabel_menu);

        setIsTouchEnabled(true);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        if (CGRect.containsPoint(ccSprite_button1.getBoundingBox(), convertTouchToNodeSpace(event))) {
            SoundEngine.sharedEngine().resumeSound();
            CCDirector.sharedDirector().popScene();
        } else if (CGRect.containsPoint(ccSprite_button11.getBoundingBox(), convertTouchToNodeSpace(event))) {
            SoundEngine.sharedEngine().playSound(CCDirector.theApp, R.raw.faster, true);
            CCScene ccScene = CCScene.node();
            ccScene.addChild(new CombatLayer());
            CCFadeTransition ccFadeTransition = CCFadeTransition.transition(2, ccScene);
            CCDirector.sharedDirector().runWithScene(ccFadeTransition);
        } else if (CGRect.containsPoint(ccSprite_button12.getBoundingBox(), convertTouchToNodeSpace(event))) {
            CCScene ccScene = CCScene.node();
            ccScene.addChild(new MenuLayer());
            CCFadeTransition ccFadeTransition = CCFadeTransition.transition(2, ccScene);
            CCDirector.sharedDirector().runWithScene(ccFadeTransition);
        } else {

        }
        return super.ccTouchesBegan(event);
    }
}
