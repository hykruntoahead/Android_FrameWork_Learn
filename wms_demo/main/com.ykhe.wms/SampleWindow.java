package com.ykhe.wms;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Looper;
import android.os.ServiceManager;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.IWindowSession;
import android.view.IWindow;
import android.view.IWindowManager;
import android.hardware.display.IDisplayManager;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

public class SampleWindow{

    //用于在窗口绘制一帧
    private Runnable mFrameReader = new Runnable() {
        @Override
        public void run() {
            try {
                //获取当前事件戳
                long time = mChoreographer.getFrameTime() % 1000;

                //绘图
                if (mSurface.isValid()){
                    Canvas canvas = mSurface.lockCanvas(null);
                    canvas.drawColor(Color.DKGRAY);
                    canvas.drawRect(2*mLp.width*time/1000 - mLp.width,
                            0,2*mLp.width*time/1000,mLp.height,mPaint);
                    mSurface.unlockCanvasAndPost(canvas);
                    mSession.finishDrawing(mWindow);
                }

                if (mContinueAnime)
                    scheduleNextFrame();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    //定义一个类继承InputEventReceiver，用于在其onInputEvent函数中接收窗口的输入事件
    class InputHandler extends InputEventReceiver{

        Looper mLooper = null;
        /**
         * Creates an input event receiver bound to the specified input channel.
         *
         * @param inputChannel The input channel.
         * @param looper       The looper to use when invoking callbacks.
         */
        public InputHandler(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
            mLooper = looper;
        }

        @Override
        public void onInputEvent(InputEvent event) {
            if (event instanceof MotionEvent){
                MotionEvent me = (MotionEvent) event;
                if (me.getAction() == MotionEvent.ACTION_UP){
                    //退出程序
                    mLooper.quit();
                }
            }
            super.onInputEvent(event);
        }
    }

    class MyWindow extends IWindow.Stub{

        public void executeCommand(String command, String parameters,
                                   ParcelFileDescriptor descriptor){};

        public void resized(Rect frame, Rect overscanInsets,Rect contentInsets,
                    Rect visibleInsets, boolean reportDraw, Configuration newConfig) {}
        public void moved(int newX, int newY){}
        public void dispatchAppVisibility(boolean visible){}
        public void dispatchGetNewSurface(){}
        public void dispatchScreenState(boolean on){}
        /**
         * Tell the window that it is either gaining or losing focus.  Keep it up
         * to date on the current state showing navigational focus (touch mode) too.
         */
        public void windowFocusChanged(boolean hasFocus,boolean inTouchMode){}

        public void closeSystemDialogs(String reason){}

        /**
         * Called for wallpaper windows when their offsets change.
         */
        public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep,
                                             boolean sync){}

        public void dispatchWallpaperCommand(String action, int x, int y,
                                      int z,Bundle extras, boolean sync){}
        /**
         * Drag/drop events
         */
        public void dispatchDragEvent(DragEvent event){}
        /**
         * System chrome visibility changes
         */
        public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility,
                                               int localValue, int localChanges){}
        /**
         * If the window manager returned RELAYOUT_RES_ANIMATING
         * from relayout(), this method will be called when the animation
         * is done.
         */
        public void doneAnimating(){}
    }

    public static void main(String[] args) {
        try {
            new SampleWindow().Run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //IWindowSession是客户端向WMS请求窗口操作的中间代理,并且是进程唯一的
    IWindowSession mSession = null;
    // InputChannel是窗口接收用户输入事件的管道
    InputChannel mInputChannel = new InputChannel();
    //下面Rect保存窗口布局结果
    Rect mInsets = new Rect();
    Rect mFrame = new Rect();
    Rect mVisibleInsets = new Rect();

    Configuration mConfig = new Configuration();
    //窗口的Surface,在此Surface上进行的绘制都将在此窗口上显示出来
    Surface mSurface = new Surface();
    //用于在窗口上进行绘制的画刷
    Paint mPaint = new Paint();
    //添加窗口所需令牌
    IBinder mToken = new Binder();
    //一个窗口对象
    MyWindow mWindow = new MyWindow();
    //WindowManager.LayoutParams定义窗口的布局属性,包括位置.尺寸及窗口类型等
    WindowManager.LayoutParams mLp = new WindowManager.LayoutParams();

    Choreographer mChoreographer = null;
    //InputHandler　用于从InputChannel接受按键事件并做出响应
    InputHandler mInputHandler = null;

    boolean mContinueAnime = true;

    private void Run() throws Exception {
        Looper.prepare();
        //获取WMS服务
        IWindowManager wms = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
        //通过WindowManagerGlobal获取进程唯一的IWindowSession实例.它将用于向WMS发送请求
        mSession = WindowManagerGlobal.getWindowSession(/**Looper.myLooper()**/);

        //获取屏幕分辨率
        IDisplayManager dm = IDisplayManager.Stub.asInterface(ServiceManager.getService(Context.DISPLAY_SERVICE));
        DisplayInfo di = dm.getDisplayInfo(Display.DEFAULT_DISPLAY);
        Point scrnSize = new Point(di.appWidth,di.appHeight);

        //初始化WindowManager.LayoutParams
        initLayoutParams(scrnSize);

        //将窗口添加到WMS
        installWindow(wms);
        //初始化Choreographer是实例，此实力线程唯一．用法与Handler类似，不过它总是在VSYC同步时回调，所以比Handler更适合做动画循环器
        mChoreographer = Choreographer.getInstance();

        //开始处理第一帧动画
        scheduleNextFrame();

        //当前线程陷入循环，直到Looper.quit()
        Looper.loop();

        //标记不要继续绘制动画帧
        mContinueAnime = false;

        //卸载当前Window
        uninstallWindow(wms);
    }


    private void initLayoutParams(Point scrnSize) {
        //标记即将安装的窗口类型为SYSTEM_ALERT,这样使得窗口的ZOrder顺序比较靠前
        mLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//;TYPE_APPLICATION_OVERLAY
        mLp.setTitle("SampleWindow");
        //设定窗口的左上角坐以及高度和宽度
        mLp.gravity = Gravity.LEFT | Gravity.TOP;
        mLp.x = scrnSize.x / 4;
        mLp.y = scrnSize.y / 4;
        mLp.width = scrnSize.x / 2;
        mLp.height = scrnSize.y / 2;
        //和输入事件相关的Flag,希望当输入事件发生在此窗口之外时，其他窗口也可以接收输入事件
        mLp.flags = mLp.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }

    private void installWindow(IWindowManager wms) throws Exception{
        //向WMS声明一个Token,任何一个Window都需要隶属于一个特定类型的Token
        wms.addWindowToken(mToken, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//TYPE_APPLICATION_OVERLAY
        //设置窗口所隶属的token
        mLp.token = mToken;

        //通过IWindowSession将窗口安装进WMS,注意此时仅仅是安装到WMS,本例的Window目前仍然没有有效的Surface.
        // 不过，经过这个调用后,mInputChannal已经可用来接收输入事件了
        mSession.add(mWindow,0,mLp, View.VISIBLE,mInsets,mInputChannel);

        // 通过IWindowSession要求WMS对本窗口进行布局．经过该操作后,WMS将会为窗口创建一块用于绘制的Surface并保存在参数mSurface中
        // 同时这个Surface被WMS放置在LayoutParams所指定的位置上

        //public int relayout(android.view.IWindow window, int seq,
        // android.view.WindowManager.LayoutParams attrs, int requestedWidth,
        // int requestedHeight, int viewVisibility, int flags,
        // android.graphics.Rect outFrame,
        // android.graphics.Rect outOverscanInsets,
        // android.graphics.Rect outContentInsets,
        // android.graphics.Rect outVisibleInsets,
        // android.content.res.Configuration outConfig,
        // android.view.Surface outSurface) throws android.os.RemoteException
        Rect outOverscanInsets = new Rect();
        mSession.relayout(mWindow,0,mLp,mLp.width,mLp.height,View.VISIBLE,
                0,mFrame,outOverscanInsets,mInsets,mVisibleInsets,mConfig,mSurface);

        if (!mSurface.isValid()){
            throw new RuntimeException("Failed creating Surface");
        }

        //基于WMS返回的InputChannel创建一个Handler,用于监听输入事件
        //mInputHandler一旦被创建，就已经在监听输入事件了
        mInputHandler  = new InputHandler(mInputChannel,Looper.myLooper());
    }

    public void uninstallWindow(IWindowManager wms) throws Exception{
        //从WMS处卸载窗口
        mSession.remove(mWindow);
        //从WMS处移除之前添加的token
        wms.removeWindowToken(mToken);
    }


    private void scheduleNextFrame() {
        //要求在显示系统刷新下一帧时回调mFrameReader,注意　只回调一次
        mChoreographer.postCallback(Choreographer.CALLBACK_ANIMATION,mFrameReader,null);
    }

}