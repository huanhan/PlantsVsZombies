package xin.lrvik.plantsvszombies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import xin.lrvik.plantsvszombies.plant.CherryBomb;
import xin.lrvik.plantsvszombies.plant.Chomper;
import xin.lrvik.plantsvszombies.plant.Peashooter;
import xin.lrvik.plantsvszombies.plant.PotatoMine;
import xin.lrvik.plantsvszombies.plant.Repeater;
import xin.lrvik.plantsvszombies.plant.SnowPea;
import xin.lrvik.plantsvszombies.plant.SunFlower;
import xin.lrvik.plantsvszombies.plant.WallNut;

import static android.content.ContentValues.TAG;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/8.
 */
public class CombatLayer extends CCLayer {

    private CGSize winSize;
    private ArrayList<PlantCard> plantCards;
    private ArrayList<PlantCard> selectPlantCards;
    private CCSprite ccSprite_seedBank;
    private CCLabel ccLabel;
    private CCSprite ccSprite_seedChooser;
    private boolean isMove;
    private CCSprite ccSprite_SeedChooser_Button_Disabled;
    private CCSprite ccSprite_SeedChooser_Button;
    private CCTMXTiledMap cctmxTiledMap;
    private ArrayList<CCSprite> ccSprites_show;
    private CCSprite ccSprite_startReady;
    private boolean isStart;
    private PlantCard selectCard;
    private Plant selectPlant;
    private ArrayList<CombatLine> combatLines;
    private ArrayList<ArrayList<CGPoint>> cgPoints_towers;
    private ArrayList<CGPoint> cgPoints_path;
    private Random random;

    private int currentSunNumber = 50000;
    private ArrayList<Sun> suns;
    private Sun sun;
    private CCLabel ccLabel1ZombiesBatch;
    private CCSprite finalWave;
    private CCLabel ccLabel1;
    private int killZombiesNum = 0;
    private CCSprite ccSprite_tipbg;
    private CCLabel ccLabel_pause;
    private CCSprite ccSprite_diamond;
    private CCSprite ccSprite_tipbg2;
    private CCLabel ccLabel_diamond;
    private ArrayList<Diamond> diamonds;
    private int diamondsNum;
    private Diamond diamond;
    private CCSprite ccSprite_shovel;
    private SoundEngine engine;


    public CombatLayer() {
        loadMap();
    }

    private void loadMap() {

        engine = SoundEngine.sharedEngine();

        ccSprites_show = new ArrayList<>();

        //加载地图文件
        cctmxTiledMap = CCTMXTiledMap.tiledMap("combat/map1.tmx");
        //将地图文件增加进图层
        addChild(cctmxTiledMap);
        //获取所有僵尸位置点
        CCTMXObjectGroup objectGroup_show = cctmxTiledMap.objectGroupNamed("show");

        ArrayList<HashMap<String, String>> objects = objectGroup_show.objects;
        for (HashMap<String, String> object : objects) {
            //获取僵尸位置x
            float x = Float.parseFloat(object.get("x"));
            //获取僵尸位置y
            float y = Float.parseFloat(object.get("y"));
            //创建僵尸精灵
            CCSprite ccSprite_shake = CCSprite.sprite("zombies/zombies_1/shake/Frame00.png");
            //设置精灵位置
            ccSprite_shake.setPosition(x, y);
            //将精灵增加进地图内。不使用addChild是因为，精灵的位置不是相对图层的，而是相对地图的。
            cctmxTiledMap.addChild(ccSprite_shake);
            //增加进最终要展示的集合
            ccSprites_show.add(ccSprite_shake);
            //僵尸抖动动画
            //创建抖动贞集合
            ArrayList<CCSpriteFrame> frames = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                //创建贞精灵
                CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                        "zombies/zombies_1/shake/Frame%02d.png", i))
                        .displayedFrame();
                //增加进抖动贞集合
                frames.add(ccSpriteFrame);
            }
            //将贞集合转变为动画，运行0.2秒
            CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.2f);
            //创建动画，执行完成是恢复初始状态
            CCAnimate ccAnimate = CCAnimate.action(ccAnimation, true);
            //创建永远循环
            CCRepeatForever ccRepeatForever = CCRepeatForever.action(ccAnimate);
            //僵尸精灵运行动画
            ccSprite_shake.runAction(ccRepeatForever);
        }
        winSize = CCDirector.sharedDirector().winSize();

        CCDelayTime ccDelayTime = CCDelayTime.action(2);
        //创建图层移动
        CCMoveBy ccMoveBy = CCMoveBy.action(2,
                ccp(winSize.getWidth() - cctmxTiledMap.getContentSize().getWidth(), 0));
        //移动到僵尸界面后调用函数
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "loadChoose");
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccMoveBy, ccCallFunc);
        cctmxTiledMap.runAction(ccSequence);

        ccSprite_tipbg = CCSprite.sprite("other/tipbg.png");
        ccSprite_tipbg.setPosition(winSize.getWidth() - ccSprite_tipbg.getBoundingBox().size.getWidth() / 2 - 30,
                winSize.getHeight() - ccSprite_tipbg.getBoundingBox().size.getHeight() / 2 - 30);
        addChild(ccSprite_tipbg);
        ccLabel_pause = CCLabel.makeLabel("菜单", "", 30);
        ccLabel_pause.setColor(ccColor3B.ccGREEN);
        ccLabel_pause.setPosition(ccSprite_tipbg.getPosition());
        addChild(ccLabel_pause);

        ccSprite_tipbg2 = CCSprite.sprite("other/tipbg.png");
        ccSprite_tipbg2.setPosition(winSize.getWidth() / 2 - ccSprite_tipbg2.getBoundingBox().size.getWidth() / 2 + 200,
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

        diamonds = new ArrayList<>();
    }

    public void loadChoose() {
        //创建选择框
        ccSprite_seedBank = CCSprite.sprite("choose/SeedBank.png");
        //设置锚点在左上角
        ccSprite_seedBank.setAnchorPoint(0, 1);
        //设置位置在左边顶部
        ccSprite_seedBank.setPosition(0, winSize.getHeight());
        //增加进图层
        addChild(ccSprite_seedBank);

        //创建文字
        ccLabel = CCLabel.makeLabel("50", "", 20);
        //设置颜色
        ccLabel.setColor(ccColor3B.ccBLACK);
        //设置位置
        ccLabel.setPosition(40, 695);
        //增加到图层
        addChild(ccLabel);

        //创建选择界面
        ccSprite_seedChooser = CCSprite.sprite("choose/SeedChooser.png");
        //设置选择界面锚点在左下角
        ccSprite_seedChooser.setAnchorPoint(0, 0);
        ccSprite_seedChooser.setPosition(ccp(0, 0));
        //增加到图层
        addChild(ccSprite_seedChooser);

        ccSprite_SeedChooser_Button_Disabled = CCSprite.sprite("choose/SeedChooser_Button_Disabled.png");
        ccSprite_SeedChooser_Button_Disabled.setPosition(ccSprite_seedChooser.getContentSize().getWidth() / 2, 80);
        ccSprite_seedChooser.addChild(ccSprite_SeedChooser_Button_Disabled);

        ccSprite_SeedChooser_Button = CCSprite.sprite("choose/SeedChooser_Button.png");
        ccSprite_SeedChooser_Button.setPosition(ccSprite_seedChooser.getContentSize().getWidth() / 2, 80);
        ccSprite_seedChooser.addChild(ccSprite_SeedChooser_Button);
        ccSprite_SeedChooser_Button.setVisible(false);


        plantCards = new ArrayList<>();
        selectPlantCards = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            PlantCard plantCard = new PlantCard(i);
            plantCards.add(plantCard);
            //给深色卡片设置位置
            plantCard.getDark().setPosition(50 + 60 * i, 590);
            //增加进选择框内
            ccSprite_seedChooser.addChild(plantCard.getDark());
            //给浅色卡片设置位置
            plantCard.getLight().setPosition(50 + 60 * i, 590);
            //增加进选择框内
            ccSprite_seedChooser.addChild(plantCard.getLight());
        }
        setIsTouchEnabled(true);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint cgPoint = convertTouchToNodeSpace(event);

        if (CGRect.containsPoint(ccSprite_tipbg.getBoundingBox(), cgPoint)) {
            /*if (CCDirector.sharedDirector().getIsPaused()) {
                CCDirector.sharedDirector().resume();
                ccLabel_pause.setString("暂停");
            } else {
                CCDirector.sharedDirector().pause();
                ccLabel_pause.setString("开始");
            }*/
            engine.pauseSound();
            CCScene ccScene = CCScene.node();
            ccScene.addChild(new PauseLayer());
            CCDirector.sharedDirector().pushScene(ccScene);

        }

        //判断是否开始游戏
        if (isStart) {

            if (CGRect.containsPoint(ccSprite_seedBank.getBoundingBox(), cgPoint)) {
                //初始化选中
                if (selectCard != null) {
                    selectCard.getLight().setOpacity(255);
                    selectCard = null;
                }

                //遍历所有卡片
                for (PlantCard plantCard : selectPlantCards) {
                    //如果某个卡片被点击
                    if (CGRect.containsPoint(plantCard.getLight().getBoundingBox(), cgPoint)
                            && plantCard.getLight().getOpacity() == 255) {
                        selectCard = plantCard;
                        //被选中的卡片变灰色
                        selectCard.getLight().setOpacity(100);
                        //创建卡片对应的精灵
                        switch (selectCard.getId()) {
                            case 0:
                                selectPlant = new Peashooter();
                                break;
                            case 1:
                                selectPlant = new SunFlower();
                                break;
                            case 2:
                                selectPlant = new CherryBomb();
                                break;
                            case 3:
                                selectPlant = new WallNut();
                                break;
                            case 4:
                                selectPlant = new PotatoMine();
                                break;
                            case 5:
                                selectPlant = new SnowPea();
                                break;
                            case 6:
                                selectPlant = new Chomper();
                                break;
                            case 7:
                                selectPlant = new Repeater();
                                break;
                        }
                    }
                }
                //如果点击的是非选择卡片区域，则进行种植
            } else if (selectPlant != null && selectCard != null) {
                //算出点击的位置的行列
                int col = (int) (cgPoint.x - 220) / 105;
                int row = (int) (cgPoint.y - 40) / 120;
                if (col >= 0 && col < 9 && row >= 0 && row < 5) {
                    //获取行
                    CombatLine combatLine = combatLines.get(row);
                    //获取当前列，看是否有植物
                    if (!combatLine.isContainPlant(col)) {
                        //当前位置没有植物，增加进位置里
                        combatLine.addPlant(col, selectPlant);
                        //设置植物位置为地图保存的图像点
                        selectPlant.setPosition(cgPoints_towers.get(row).get(col));
                        //增加进图层
                        cctmxTiledMap.addChild(selectPlant);

                        addSunNumber(-selectPlant.getPrice());
                        selectPlant = null;
                        selectCard = null;

                        //选中卡片变亮
                        /*selectPlant = null;
                        selectCard.getLight().setOpacity(255);
                        selectCard = null;*/


                        /*selectPlant.setPosition(cgPoint);
                        addChild(selectPlant);*/
                    }
                }
            } else if (CGRect.containsPoint(ccSprite_shovel.getBoundingBox(), cgPoint)) {
                if (ccSprite_shovel.getOpacity() != 100) {
                    ccSprite_shovel.setOpacity(100);
                } else {
                    ccSprite_shovel.setOpacity(255);
                }

            } else if (ccSprite_shovel.getOpacity() == 100) {
                //算出点击的位置的行列
                int col = (int) (cgPoint.x - 220) / 105;
                int row = (int) (cgPoint.y - 40) / 120;
                if (col >= 0 && col < 9 && row >= 0 && row < 5) {
                    //获取行
                    CombatLine combatLine = combatLines.get(row);
                    //获取当前列，看是否有植物
                    if (combatLine.isContainPlant(col)) {
                        combatLine.removePlant(col);
                    }
                    ccSprite_shovel.setOpacity(255);
                }

            } else {
                for (Sun sun : suns) {
                    if (CGRect.containsPoint(sun.getBoundingBox(), cgPoint)) {
                        engine.playEffect(CCDirector.theApp, R.raw.points, false);
                        sun.collect();
                    }
                }

                for (Diamond diamond : diamonds) {
                    if (CGRect.containsPoint(diamond.getBoundingBox(), cgPoint)) {
                        engine.playEffect(CCDirector.theApp, R.raw.points, false);
                        diamond.collect(ccSprite_tipbg2.getPosition());
                    }

                }
            }

        } else {//未开始游戏

            //判断点击是否在选择框内
            if (CGRect.containsPoint(ccSprite_seedChooser.getBoundingBox(), cgPoint)) {
                //判断是否已经选满了
                if (selectPlantCards.size() < 6) {
                    //遍历所有卡片
                    for (PlantCard plantCard : plantCards) {
                        //判断浅色卡片是否被点击了
                        if (CGRect.containsPoint(plantCard.getLight().getBoundingBox(), cgPoint)) {
                            //已选中列表是否包含
                            if (!selectPlantCards.contains(plantCard)) {
                                //增加进已选中列表
                                selectPlantCards.add(plantCard);

                                //设置移动到的位置
                                CCMoveTo ccMoveTo = CCMoveTo.action(0.1f, ccp(50 + 60 * selectPlantCards.size(), 725));
                                plantCard.getLight().runAction(ccMoveTo);

                                if (selectPlantCards.size() == 6) {
                                    ccSprite_SeedChooser_Button.setVisible(true);
                                }
                            }
                        }
                    }
                }
            }

            //点击是否是选中列表
            if (CGRect.containsPoint(ccSprite_seedBank.getBoundingBox(), cgPoint)) {
                //设置是否移动flag
                isMove = false;
                //循环选中列表
                for (PlantCard plantCard : selectPlantCards) {
                    //判断是否点击的是选中列表的项
                    if (CGRect.containsPoint(plantCard.getLight().getBoundingBox(), cgPoint)) {
                        //设置移动，移动位置为该项深色的位置
                        CCMoveTo ccMoveTo = CCMoveTo.action(0.1f, plantCard.getDark().getPosition());
                        //让亮色移动
                        plantCard.getLight().runAction(ccMoveTo);
                        //删除选中列表的该项
                        selectPlantCards.remove(plantCard);
                        ccSprite_SeedChooser_Button.setVisible(false);
                        //修改flag
                        isMove = true;
                        break;
                    }
                }
            }
            //如果被修改了就进行选中列表的重新排序
            if (isMove) {
                for (int i = 0; i < selectPlantCards.size(); i++) {
                    PlantCard plantCard = selectPlantCards.get(i);
                    CCMoveTo ccMoveTo = CCMoveTo.action(0.1f, ccp(110 + 60 * i, 725));
                    plantCard.getLight().runAction(ccMoveTo);
                }
            }

            //如果开始按钮可见
            if (ccSprite_SeedChooser_Button.getVisible()) {
                //判断是否点击的是开始按钮
                if (CGRect.containsPoint(ccSprite_SeedChooser_Button.getBoundingBox(), cgPoint)) {
                    //将选中的卡片加载到主layer中，不然直接移除因为是ccSprite_seedChooser会直接消失
                    for (PlantCard plantCard : selectPlantCards) {
                        addChild(plantCard.getLight());
                    }
                    //ccSprite_seedChooser移除自身
                    ccSprite_seedChooser.removeSelf();
                    //移动回主界面
                    CCMoveTo ccMoveTo = CCMoveTo.action(2, ccp(-100, 0));
                    CCCallFunc ccCallFunc = CCCallFunc.action(this, "startReady");
                    CCSequence ccSequence = CCSequence.actions(ccMoveTo, ccCallFunc);

                    cctmxTiledMap.runAction(ccSequence);
                }
            }
        }
        return super.ccTouchesBegan(event);
    }

    public void startReady() {

        engine.playSound(CCDirector.theApp, R.raw.watery, true);
        //将已经绘制好的僵尸移除，节省内存
        for (CCSprite ccSprite : ccSprites_show) {
            ccSprite.removeSelf();
        }
        //设置不可点击
        setIsTouchEnabled(false);

        //提示安放植物
        ccSprite_startReady = CCSprite.sprite("startready/startReady_00.png");
        ccSprite_startReady.setPosition(winSize.getWidth() / 2, winSize.getHeight() / 2);
        addChild(ccSprite_startReady);

        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            //创建贞精灵
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
                    "startready/startReady_%02d.png", i)).displayedFrame();
            //增加进抖动贞集合
            frames.add(ccSpriteFrame);
        }
        CCAnimation ccAnimation = CCAnimation.animationWithFrames(frames, 0.5f);
        CCAnimate ccAnimate = CCAnimate.action(ccAnimation, false);
        CCCallFunc ccCallFunc = CCCallFunc.action(this, "start");
        CCSequence ccSequence = CCSequence.actions(ccAnimate, ccCallFunc);
        ccSprite_startReady.runAction(ccSequence);
    }

    public void start() {
        ccSprite_startReady.removeSelf();
        setIsTouchEnabled(true);
        isStart = true;

        cgPoints_towers = new ArrayList<>();
        //读取地图tower
        for (int i = 0; i <= 4; i++) {
            ArrayList<CGPoint> cgPoints_tower = new ArrayList<>();
            CCTMXObjectGroup objectGroup_tower = cctmxTiledMap.objectGroupNamed("tower" + i);
            ArrayList<HashMap<String, String>> objects = objectGroup_tower.objects;
            //将tower里的点保存到cgPoints_tower
            for (HashMap<String, String> object : objects) {
                float x = Float.parseFloat(object.get("x"));
                float y = Float.parseFloat(object.get("y"));
                cgPoints_tower.add(ccp(x, y));
            }
            //保存所有的tower
            cgPoints_towers.add(cgPoints_tower);
        }


        cgPoints_path = new ArrayList<>();
        CCTMXObjectGroup objectGroup_tower = cctmxTiledMap.objectGroupNamed("path");
        ArrayList<HashMap<String, String>> objects = objectGroup_tower.objects;

        for (HashMap<String, String> object : objects) {
            float x = Float.parseFloat(object.get("x"));
            float y = Float.parseFloat(object.get("y"));
            cgPoints_path.add(ccp(x, y));
        }

        //创建每层的空位
        combatLines = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            boolean lm = (boolean) SPUtils.get(CCDirector.sharedDirector().getActivity(), "lm", false);
            if (lm) {
                LawnMower lawnMower = new LawnMower(cgPoints_path.get(2 * i + 1), cgPoints_path.get(2 * i));
                cctmxTiledMap.addChild(lawnMower, 5 - i);
                combatLines.add(new CombatLine(lawnMower));
            } else {
                combatLines.add(new CombatLine());
            }

        }

        random = new Random();
        //CCScheduler.sharedScheduler().schedule("addZombie", this, 30, false);

        suns = new ArrayList<>();
        update();

        CCDelayTime ccDelayTime1 = CCDelayTime.action(15);
        CCCallFunc ccCallFunc1 = CCCallFunc.action(this, "startAddZombie1");
        CCDelayTime ccDelayTime2 = CCDelayTime.action(70);
        CCCallFunc ccCallFunc2 = CCCallFunc.action(this, "startAddZombie2");
        CCDelayTime ccDelayTime3 = CCDelayTime.action(150);
        CCCallFunc ccCallFunc3 = CCCallFunc.action(this, "startAddZombie3");

        //天上落下阳光 20秒下一次
        CCScheduler.sharedScheduler().schedule("createSkySun", this, 20, false);

        CCSequence ccSequence = CCSequence.actions(ccDelayTime1, ccCallFunc1, ccDelayTime2, ccCallFunc2, ccDelayTime3, ccCallFunc3);
        runAction(ccSequence);


        ccLabel1ZombiesBatch = CCLabel.makeLabel("第  0/3  波僵尸", "", 20);
        ccLabel1ZombiesBatch.setPosition(ccp(winSize.getWidth() - 100, 50));
        ccLabel1ZombiesBatch.setColor(ccColor3B.ccRED);
        addChild(ccLabel1ZombiesBatch);

        ccLabel1 = CCLabel.makeLabel("击杀0", "", 25);
        ccLabel1.setVisible(false);
        ccLabel1.setPosition(ccp(winSize.getWidth() - 250, winSize.getHeight() - 50));
        ccLabel1.setColor(ccColor3B.ccRED);
        addChild(ccLabel1);

        ccSprite_shovel = CCSprite.sprite("other/shovel.png");
        ccSprite_shovel.setPosition(winSize.getWidth() / 2 - 150, winSize.getHeight() - 40);
        addChild(ccSprite_shovel);

    }


    public void setKillZombiesNum() {
        killZombiesNum++;
        ccLabel1.setString("击杀" + killZombiesNum);

        //295
        if (killZombiesNum >= 295) {
            SoundEngine.sharedEngine().playEffect(CCDirector.theApp, R.raw.win, false);
            CCLabel ccLabel2 = CCLabel.makeLabel("通关成功", "", 40);
            ccLabel2.setPosition(ccp(winSize.getWidth() / 2, winSize.getHeight() / 2));
            ccLabel2.setColor(ccColor3B.ccRED);
            addChild(ccLabel2);
            CCSprite ccSprite_trophy = CCSprite.sprite("other/trophy.png");
            ccSprite_trophy.setPosition(ccp(winSize.getWidth() / 2,
                    winSize.getHeight() / 2 + ccSprite_trophy.getBoundingBox().size.getHeight()));
            addChild(ccSprite_trophy);
            CCDelayTime ccDelayTime = CCDelayTime.action(2);
            CCCallFunc end = CCCallFunc.action(this, "restart");
            CCSequence ccSequence = CCSequence.actions(ccDelayTime, end);
            runAction(ccSequence);
        }
    }

    private void setZombiesBatch(int batch) {
        ccLabel1ZombiesBatch.setString("第  " + batch + "/3  拨僵尸");
    }

    public void createSkySun(float t) {
        sun = new Sun();
        int randomInt = random.nextInt(100);
        //设置位置在最顶部
        sun.setPosition(winSize.getWidth() / 2 + randomInt, winSize.getHeight() - 100);
        addChild(sun);
        addSun(sun);
        CCJumpTo ccJumpTo = CCJumpTo.action(1f, ccp(winSize.getWidth() / 2 - randomInt, winSize.getHeight() / 3), 200, 1);
        //5秒后自动消失
        CCDelayTime ccDelayTime = CCDelayTime.action(5);
        CCCallFunc removeSun = CCCallFunc.action(this, "removeSun");
        CCSequence ccSequence = CCSequence.actions(ccJumpTo, ccDelayTime, removeSun);
        sun.runAction(ccSequence);
    }

    public void removeSun() {
        removeSun(sun);
        sun.removeSelf();
    }

    public void startAddZombie1() {
        setZombiesBatch(1);
        addZombiesByNum(15, 5f);
    }


    public void startAddZombie2() {
        setZombiesBatch(2);
//        CCScheduler.sharedScheduler().schedule("addZombie", this, 10, false);
        addZombiesByNum(80, 3f);
    }

    public void startAddZombie3() {
        setIsTouchEnabled(false);
        finalWave = CCSprite.sprite("other/FinalWave.png");
        finalWave.setPosition(winSize.getWidth() / 2, winSize.getHeight() / 2);
        addChild(finalWave);

        CCDelayTime ccDelayTime = CCDelayTime.action(2);
        CCCallFunc removeFinalWave = CCCallFunc.action(this, "removeFinalWave");
        CCSequence ccSequence = CCSequence.actions(ccDelayTime, removeFinalWave);
        runAction(ccSequence);

        setZombiesBatch(3);
//        CCScheduler.sharedScheduler().schedule("addZombie", this, 50, false);
        addZombiesByNum(200, 1f);
    }

    public void removeFinalWave() {
        finalWave.removeSelf();
        setIsTouchEnabled(true);
    }

    //根据数量增加僵尸
    private void addZombiesByNum(int num, float delay) {
        engine.playEffect(CCDirector.theApp, R.raw.siren, false);
        //CCScheduler.sharedScheduler().schedule("addZombie", this, 20, false);
        CCFiniteTimeAction[] cCFiniteTimeActions = new CCFiniteTimeAction[num * 2];
        for (int i = 0; i < num * 2; i += 2) {
            cCFiniteTimeActions[i] = CCDelayTime.action(delay);
            cCFiniteTimeActions[i + 1] = CCCallFunc.action(this, "addZombie");
        }
        CCSequence ccSequence = CCSequence.actions(CCDelayTime.action(1F), cCFiniteTimeActions);
        runAction(ccSequence);
    }

    private void update() {
        for (PlantCard plantCard : selectPlantCards) {
            int price = 0;
            switch (plantCard.getId()) {
                case 0:
                    price = 100;
                    break;
                case 1:
                    price = 50;
                    break;
                case 2:
                    price = 150;
                    break;
                case 3:
                    price = 50;
                    break;
                case 4:
                    price = 25;
                    break;
                case 5:
                    price = 175;
                    break;
                case 6:
                    price = 150;
                    break;
                case 7:
                    price = 200;
                    break;
            }
            if (currentSunNumber >= price) {
                plantCard.getLight().setOpacity(255);
            } else {
                plantCard.getLight().setOpacity(30);
            }
        }

    }

    public void addZombie() {
        int i = random.nextInt(5);
        Zombie zombie = new Zombie(this, cgPoints_path.get(2 * i), cgPoints_path.get(2 * i + 1), i);
        cctmxTiledMap.addChild(zombie, 5 - i);
        combatLines.get(i).addZombie(zombie);
    }


    public void end(Object node, Object row) {
        Log.d(TAG, "row: " + row);
        LawnMower lawnMower = combatLines.get((Integer) row).getLawnMower();
        if (lawnMower != null && lawnMower.getState() != LawnMower.State.END) {
            SoundEngine.sharedEngine().playEffect(CCDirector.theApp, R.raw.lawnmower, false);
            lawnMower.move();
        } else {

            setIsTouchEnabled(false);
            //CCScheduler.sharedScheduler().unschedule("addZombie", this);
            for (CCNode ccNode : cctmxTiledMap.getChildren()) {
                ccNode.stopAllActions();
                ccNode.unscheduleAllSelectors();
            }
            for (CCNode ccNode : getChildren()) {
                ccNode.stopAllActions();
                ccNode.unscheduleAllSelectors();
            }
            engine.playEffect(CCDirector.theApp, R.raw.losemusic, false);
            CCSprite ccSprite_ZombiesWon = CCSprite.sprite("zombieswon/ZombiesWon.png");
            ccSprite_ZombiesWon.setPosition(winSize.getWidth() / 2, winSize.getHeight() / 2);
            addChild(ccSprite_ZombiesWon);
            CCDelayTime ccDelayTime = CCDelayTime.action(2);
            CCCallFunc ccCallFunc = CCCallFunc.action(this, "restart");
            CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccCallFunc);
            ccSprite_ZombiesWon.runAction(ccSequence);
        }
    }

    public void restart() {
        CCScene ccScene = CCScene.node();
        ccScene.addChild(new MenuLayer());
        CCFlipXTransition ccFlipXTransition = CCFlipXTransition.transition(2, ccScene, 1);
        CCDirector.sharedDirector().replaceScene(ccFlipXTransition);
        engine.realesAllEffects();
    }

    public void addSun(Sun sun) {
        suns.add(sun);
    }

    public void removeSun(Sun sun) {
        suns.remove(sun);
    }

    public void addSunNumber(int i) {
        currentSunNumber += i;
        ccLabel.setString(currentSunNumber + "");
        update();
    }

    public void addDiamond(Zombie zombie) {
        if (diamond == null) {
            diamond = new Diamond();
            diamond.setScale(0.8f);
            diamond.setPosition(zombie.getPosition().x - 80, zombie.getPosition().y);
            addChild(diamond);
            diamonds.add(diamond);

            CCDelayTime ccDelayTime = CCDelayTime.action(5);
            CCCallFunc ccCallFunc1 = CCCallFunc.action(this, "removeDiamond");
            CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccCallFunc1);
            runAction(ccSequence);
        }
    }

    public void removeDiamond() {
        diamond.removeSelf();
        diamonds.remove(diamond);
        diamond = null;
    }

    public void addDiamondNumber() {
        diamondsNum++;
        ccLabel_diamond.setString(diamondsNum + "");
        diamond = null;
        SPUtils.put(CCDirector.sharedDirector().getActivity(), "zs", diamondsNum);
    }

    public ArrayList<CombatLine> getCombatLines() {
        return combatLines;
    }


}
