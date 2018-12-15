package xin.lrvik.plantsvszombies;

import android.util.SparseArray;

import org.cocos2d.actions.CCScheduler;
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
import java.util.Random;

import xin.lrvik.plantsvszombies.bullet.Bullet;
import xin.lrvik.plantsvszombies.plant.Chomper;
import xin.lrvik.plantsvszombies.plant.PotatoMine;
import xin.lrvik.plantsvszombies.plant.ShooterPlant;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/17.
 */
public class CombatLine {

    private final Random random;
    private final ArrayList<Chomper> chomperPlants;
    private SparseArray<Plant> plants;
    private ArrayList<Zombie> zombies;
    private final ArrayList<ShooterPlant> shooterPlants;
    private final ArrayList<PotatoMine> potatoMinePlants;
    private LawnMower lawnMower;


    public CombatLine(LawnMower lawnMower) {
        this.lawnMower = lawnMower;
        plants = new SparseArray<>();
        zombies = new ArrayList<>();
        shooterPlants = new ArrayList<>();
        chomperPlants = new ArrayList<>();
        potatoMinePlants = new ArrayList<>();
        CCScheduler.sharedScheduler().schedule("attackPlant", this, 1, false);
        CCScheduler.sharedScheduler().schedule("attackZombie", this, 1, false);
        CCScheduler.sharedScheduler().schedule("bulletHurtCompute", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("chomperHurt", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("potatoMineHurt", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("lawnMowerHurt", this, 0.2f, false);
        random = new Random();
    }

    public CombatLine() {
        plants = new SparseArray<>();
        zombies = new ArrayList<>();
        shooterPlants = new ArrayList<>();
        chomperPlants = new ArrayList<>();
        potatoMinePlants = new ArrayList<>();
        CCScheduler.sharedScheduler().schedule("attackPlant", this, 1, false);
        CCScheduler.sharedScheduler().schedule("attackZombie", this, 1, false);
        CCScheduler.sharedScheduler().schedule("bulletHurtCompute", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("chomperHurt", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("potatoMineHurt", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("lawnMowerHurt", this, 0.2f, false);
        random = new Random();
    }

    public void addPlant(int col, Plant plant) {
        plants.put(col, plant);
        if (plant instanceof ShooterPlant) {
            shooterPlants.add((ShooterPlant) plant);
        }
        if (plant instanceof Chomper) {
            chomperPlants.add((Chomper) plant);
        }
        if (plant instanceof PotatoMine) {
            potatoMinePlants.add((PotatoMine) plant);
        }
    }

    public void removePlant(int col) {
        Plant plant = plants.get(col);
        plants.remove(col);
        if (plant instanceof ShooterPlant) {
            shooterPlants.remove(plant);
        }
        if (plant instanceof Chomper) {
            shooterPlants.remove(plant);
        }
        if (plant instanceof PotatoMine) {
            potatoMinePlants.remove(plant);
        }
        plant.removeSelf();
    }

    public boolean isContainPlant(int col) {
        if (plants.get(col) != null) {
            return true;
        }
        return false;
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void attackPlant(float t) {
        if (zombies.size() != 0 && plants.size() != 0) {
            for (Zombie zombie : zombies) {
                int col = (int) (zombie.getPosition().x - 280) / 105;
                if (isContainPlant(col)) {//判断当前位置是否有植物
                    switch (zombie.getState()) {
                        case MOVE:
                            zombie.stopAllActions();
                            zombie.attack();
                            break;
                        case ATTACK:
                            Plant plant = plants.get(col);
                            plant.hurtCompute(zombie.getAttack());
                            if (plant.getHP() == 0) {
                                removePlant(col);
                                //plant.removeSelf();
                                zombie.stopAllActions();
                                zombie.move();
                            }
                            break;
                    }
                } else if (zombie.getState() == Zombie.State.ATTACK) {
                    zombie.stopAllActions();
                    zombie.move();
                }
            }
        }
    }

    public void attackZombie(float t) {
        if (!shooterPlants.isEmpty()) {
            for (ShooterPlant shooterPlant : shooterPlants) {
                if (zombies.isEmpty()) {
                    shooterPlant.stopAttackZombie();
                } else {
                    shooterPlant.attackZombie();
                }
            }
        }
    }

    public void chomperHurt(float t) {
        if (chomperPlants.size() > 0 && zombies.size() > 0) {
            for (Chomper chomperPlant : chomperPlants) {
                for (Zombie zombie : zombies) {
                    float dis = zombie.getPosition().x - chomperPlant.getPosition().x;
                    if (dis < 50 && dis > -50) {
                        if (!chomperPlant.isEat()) {
                            chomperPlant.eat(zombie);
                            ((CombatLayer) zombie.getParent().getParent()).setKillZombiesNum();
                            zombies.remove(zombie);
                        }
                        //zombie.removeSelf();
                    }

                }
            }
        }
    }

    public void potatoMineHurt(float t) {

        if (potatoMinePlants.size() > 0 && zombies.size() > 0) {
            for (PotatoMine potatoMine : potatoMinePlants) {
                //int col = (int) (potatoMine.getPosition().x - 280) / 105;
                for (Zombie zombie : zombies) {
                    float dis = zombie.getPosition().x - potatoMine.getPosition().x;
                    if (dis < 50 && dis > -50) {
                        if (potatoMine.isBig() && !potatoMine.isBoom()) {
                            potatoMine.boom();
                            /*zombies.remove(zombie);
                            removePlant(col);*/
                        }
                    }

                }
            }
        }
    }

    public void bulletHurtCompute(float t) {
        //判断植物和僵尸是否为空
        if (!zombies.isEmpty()) {
            //遍历植物
            for (ShooterPlant shooterPlant : shooterPlants) {
                //获取植物的子弹
                for (Bullet bullet : shooterPlant.getBullets()) {
                    //遍历僵尸
                    Iterator<Zombie> iterator = zombies.iterator();

                    while (iterator.hasNext()) {
                        Zombie zombie = iterator.next();
                        //如果子弹可见且子弹在僵尸左右
                        if (bullet.getVisible() && bullet.getPosition().x > zombie.getPosition().x - 20
                                && bullet.getPosition().x < zombie.getPosition().x + 20) {
                            //展示子弹破裂
                            bullet.showBulletBlast(zombie);
                            //设置隐藏子弹
                            bullet.setVisible(false);
                            //增加僵尸伤害
                            zombie.hurtCompute(bullet.getAttack());
                            //僵尸血量为0时移除僵尸
                            if (zombie.getHP() == 0) {
                                //设置击杀僵尸数
                                ((CombatLayer) zombie.getParent().getParent()).setKillZombiesNum();
                                //随机掉落钻石
                                if (random.nextInt(100) > 60) {
                                    ((CombatLayer) zombie.getParent().getParent()).addDiamond(zombie);
                                }

                                //zombie.removeSelf();
                                zombie.die(0);
                                iterator.remove();
                            }

                        }
                    }
                }
            }
        }
    }

    public void lawnMowerHurt(float t) {
        if (lawnMower != null && lawnMower.getState() != LawnMower.State.END) {
            for (Zombie zombie : zombies) {
                float dis = zombie.getPosition().x - lawnMower.getPosition().x;
                if (dis < 0) {
                    zombie.die(0);
                    ((CombatLayer) zombie.getParent().getParent()).setKillZombiesNum();
                    zombies.remove(zombie);
                }
            }
        }
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public void setZombies(ArrayList<Zombie> zombies) {
        this.zombies = zombies;
    }

    public LawnMower getLawnMower() {
        return lawnMower;
    }
}
