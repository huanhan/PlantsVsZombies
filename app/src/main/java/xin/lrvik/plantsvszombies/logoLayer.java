package xin.lrvik.plantsvszombies;

import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCJumpZoomTransition;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/5.
 */
class logoLayer extends CCLayer {

    public logoLayer() {
        logo1();
    }

    private void logo1() {
        CCSprite ccSprite_logo1 = CCSprite.sprite("logo/logo1.png");//创建精灵
        ccSprite_logo1.setAnchorPoint(0,0);//设置精灵锚点
        addChild(ccSprite_logo1);//将精灵增加到图层里

        CCDelayTime ccDelayTime = CCDelayTime.action(2);//设置延时2秒
        CCHide ccHide = CCHide.action();//设置隐藏图层
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "logo2");//设置下一阶段运行的方法
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccHide, ccCallFunc);//顺序调用
        ccSprite_logo1.runAction(ccSequence);//运行顺序调用
    }

    public void logo2(){

        CCSprite ccSprite_logo2 = CCSprite.sprite("logo/logo2.png");
        CGSize winSize = CCDirector.sharedDirector().winSize();
        ccSprite_logo2.setPosition(winSize.getWidth()/2,winSize.getHeight()/2);
        addChild(ccSprite_logo2);

        CCDelayTime ccDelayTime = CCDelayTime.action(2);
        CCHide ccHide = CCHide.action();
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "cg");
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccHide, ccCallFunc);
        ccSprite_logo2.runAction(ccSequence);
    }

    public void cg(){
        CCSprite ccSprite_cg = CCSprite.sprite("cg/cg00.png");
        ccSprite_cg.setAnchorPoint(0, 0);
        addChild(ccSprite_cg);
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();//创建图片贞
        for (int i = 0; i < 19; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "cg/cg%02d.png", i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }

        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.2f);//创建动画，0.2秒每贞
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, false);//将动画变为action，不循环
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "setTouch");
        CCSequence ccSequence = CCSequence.actions(ccAnimate, ccCallFunc);
        ccSprite_cg.runAction(ccSequence);
    }

    public void setTouch(){
        setIsTouchEnabled(true);//设置点击事件
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGRect cgRect = CGRect.make(390, 30, 490, 60);
        if (CGRect.containsPoint(cgRect,convertTouchToNodeSpace(event))) {
            CCScene ccScene = CCScene.node();//创建场景
            ccScene.addChild(new MenuLayer());//创建图层
            CCJumpZoomTransition ccJumpZoomTransition = CCJumpZoomTransition.transition(2, ccScene);//创建场景转换动画
            CCDirector.sharedDirector().runWithScene(ccJumpZoomTransition);//利用导演转换场景
        }
        return super.ccTouchesBegan(event);
    }
}
