package xin.lrvik.plantsvszombies;

import android.util.SparseArray;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import xin.lrvik.plantsvszombies.bullet.Bullet;
import xin.lrvik.plantsvszombies.plant.ShooterPlant;


/**
 * Author by 豢涵, Email huanhanfu@126.com, Date on 2018/11/17.
 */
public class CombatLine {

    private final Random random;
    private SparseArray<Plant> plants;
    private ArrayList<Zombie> zombies;
    private final ArrayList<ShooterPlant> shooterPlants;

    public CombatLine() {
        plants = new SparseArray<>();
        zombies = new ArrayList<>();
        shooterPlants = new ArrayList<>();
        CCScheduler.sharedScheduler().schedule("attackPlant", this, 1, false);
        CCScheduler.sharedScheduler().schedule("attackZombie", this, 1, false);
        CCScheduler.sharedScheduler().schedule("bulletHurtCompute", this, 0.2f, false);
        random = new Random();
    }

    public void addPlant(int col, Plant plant) {
        plants.put(col, plant);
        if (plant instanceof ShooterPlant) {
            shooterPlants.add((ShooterPlant) plant);
        }
    }

    public void removePlant(int col){
        Plant plant = plants.get(col);
        plants.remove(col);
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
                                plants.remove(col);
                                plant.removeSelf();
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

    public void bulletHurtCompute(float t) {
        //判断植物和僵尸是否为空
        if (!shooterPlants.isEmpty() && !zombies.isEmpty()) {
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
                                ((CombatLayer) zombie.getParent().getParent()).setKillZombiesNum();
                                if (random.nextInt(100) > 60) {
                                    ((CombatLayer) zombie.getParent().getParent()).addDiamond(zombie);
                                }
                                zombie.removeSelf();
                                iterator.remove();
                            }

                        }
                    }
                }
            }
        }
    }

}
