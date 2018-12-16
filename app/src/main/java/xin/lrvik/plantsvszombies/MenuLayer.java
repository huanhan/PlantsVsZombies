package xin.lrvik.plantsvszombies;

import android.view.MotionEvent;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/5.
 */
class MenuLayer extends CCLayer {

    private final CGSize winSize;
    private final SoundEngine engine;

    public MenuLayer() {
        engine = SoundEngine.sharedEngine();
        engine.preloadEffect(CCDirector.theApp,R.raw.chompsoft);
        engine.preloadEffect(CCDirector.theApp,R.raw.explosion);
        engine.preloadEffect(CCDirector.theApp,R.raw.lawnmower);
        engine.preloadEffect(CCDirector.theApp,R.raw.losemusic);
        engine.preloadEffect(CCDirector.theApp,R.raw.points);
        engine.preloadEffect(CCDirector.theApp,R.raw.shoop);
        engine.preloadEffect(CCDirector.theApp,R.raw.siren);
        engine.preloadEffect(CCDirector.theApp,R.raw.win);
        engine.preloadEffect(CCDirector.theApp,R.raw.groan4);
        engine.playSound(CCDirector.theApp, R.raw.faster, true);

        CCSprite ccSprite_menu = CCSprite.sprite("menu/main_menu_bg.png");
        ccSprite_menu.setAnchorPoint(0, 0);
        addChild(ccSprite_menu);

        //创建菜单
        CCMenu ccMenu = CCMenu.menu();

        //开始冒险
        //菜单默认未按下
        CCSprite ccSprite_start_adventure_default = CCSprite.sprite("menu/start_adventure_default.png");
        //菜单按下
        CCSprite ccSprite_start_adventure_press = CCSprite.sprite("menu/start_adventure_press.png");
        //创建菜单子项精灵
        CCMenuItemSprite ccMenuItemSprite = CCMenuItemSprite.item(ccSprite_start_adventure_default,
                ccSprite_start_adventure_press, this, "start");

        //设置菜单子项位置
        ccMenuItemSprite.setPosition(270, 160);
        //增加进菜单
        ccMenu.addChild(ccMenuItemSprite);

        //商店
        //菜单默认未按下
        CCSprite ccSprite_shop = CCSprite.sprite("other/shop.png");
        //菜单按下
        CCSprite ccSprite_shop_press = CCSprite.sprite("other/shop_press.png");
        //创建菜单子项精灵
        CCMenuItemSprite ccMenuItemSprite2 = CCMenuItemSprite.item(ccSprite_shop,
                ccSprite_shop_press, this, "shop");
        ccMenuItemSprite2.setScale(1.5f);
        //设置菜单子项位置
        ccMenuItemSprite2.setPosition(60, -180);
        //增加进菜单
        ccMenu.addChild(ccMenuItemSprite2);

        //将菜单增加进图层
        addChild(ccMenu);

        winSize = CCDirector.sharedDirector().winSize();
        setIsTouchEnabled(true);

    }

    public void start(Object item) {
        CCScene ccScene = CCScene.node();
        ccScene.addChild(new CombatLayer());
        CCFadeTransition ccFadeTransition = CCFadeTransition.transition(2, ccScene);
        CCDirector.sharedDirector().runWithScene(ccFadeTransition);
    }

    public void shop(Object item) {
        CCScene ccScene = CCScene.node();
        ccScene.addChild(new ShopLayer());
        CCFadeTransition ccFadeTransition = CCFadeTransition.transition(2, ccScene);
        CCDirector.sharedDirector().runWithScene(ccFadeTransition);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGRect cgRect = CGRect.make(winSize.width - 130, 80, 100, 100);
        if (CGRect.containsPoint(cgRect, convertTouchToNodeSpace(event))) {
            //点击了退出
            CCDirector.sharedDirector().getActivity().finish();
        }
        return super.ccTouchesBegan(event);
    }


}
