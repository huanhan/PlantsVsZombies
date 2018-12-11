package xin.lrvik.plantsvszombies;

import org.cocos2d.nodes.CCSprite;

import java.util.Locale;

/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/9.
 */
public class PlantCard {
    private int id;
    private CCSprite light;
    private CCSprite dark;

    public PlantCard(int id) {
        this.id = id;
        light=CCSprite.sprite(String.format(Locale.CHINA,"choose/p%02d.png",id));
        dark=CCSprite.sprite(String.format(Locale.CHINA,"choose/p%02d.png",id));
        dark.setOpacity(100);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CCSprite getLight() {
        return light;
    }

    public void setLight(CCSprite light) {
        this.light = light;
    }

    public CCSprite getDark() {
        return dark;
    }

    public void setDark(CCSprite dark) {
        this.dark = dark;
    }
}
