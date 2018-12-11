package xin.lrvik.plantsvszombies;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends Activity {

    private CCDirector ccDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION :隐藏虚拟按键（导航栏）
            //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY :粘性全屏沉浸模式
            //顶部下滑会显示出透明的状态栏和虚拟导航栏，在一段时间以后自动恢复成全屏沉浸模式
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CCGLSurfaceView ccglSurfaceView = new CCGLSurfaceView(this);//创建画布
        setContentView(ccglSurfaceView);

        ccDirector = CCDirector.sharedDirector();//获得导演
        ccDirector.attachInView(ccglSurfaceView);//将导演连接到画布
        ccDirector.setDisplayFPS(true);//设置显示FPS
        ccDirector.setScreenSize(1280, 768);//设置屏幕大小，超过会自动适配
        CCScene ccScene = CCScene.node();//创建场景
        ccScene.addChild(new logoLayer());//给场景增加图层
        ccDirector.runWithScene(ccScene);//运行场景

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ccDirector.end();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ccDirector.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ccDirector.onPause();
    }
}
