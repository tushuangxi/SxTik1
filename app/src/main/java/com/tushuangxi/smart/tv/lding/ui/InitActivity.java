package com.tushuangxi.smart.tv.lding.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.billy.android.preloader.PreLoader;
import com.billy.android.preloader.PreLoaderWrapper;
import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fengchen.uistatus.UiStatusController;
import com.fengchen.uistatus.annotation.UiStatus;
import com.github.hariprasanths.floatingtoast.FloatingToast;
import com.tushuangxi.smart.tv.BuildConfig;
import com.tushuangxi.smart.tv.lding.entity.SiteNavigationRsp;
import com.tushuangxi.smart.tv.lding.entity.livedata.Data;
import com.tushuangxi.smart.tv.lding.entity.livedata.DataLiveData;
import com.tushuangxi.smart.tv.lding.eventbus.EventMessage;
import com.tushuangxi.smart.tv.lding.http.ApiConstants;
import com.tushuangxi.smart.tv.lding.other.AppGlobalConsts;
import com.tushuangxi.smart.tv.lding.rerxmvp.base.BaseActivity;
import com.tushuangxi.smart.tv.lding.rerxmvp.interfaceUtils.interfaceUtilsAll;
import com.tushuangxi.smart.tv.lding.rerxmvp.presenter.SiteNavigationRspPresenter;
import com.tushuangxi.smart.tv.lding.utils.FloatingToastUtils;
import com.tushuangxi.smart.tv.lding.widget.DragFloatButton;
import com.tushuangxi.smart.tv.lding.widget.FocusViewUtils;
import com.tushuangxi.smart.tv.library.asyncchain.AsyncChain;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainError;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainErrorCallback;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainRunnable;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainTask;
import com.tushuangxi.smart.tv.library.imageloaderfactory.cofig.MainActivity;
import com.tushuangxi.smart.tv.library.loading.conn.LoadingApp;
import com.tushuangxi.smart.tv.library.logcat.FloatingLogcatView;
import com.tushuangxi.smart.tv.library.mmkv.KVUtils;
import com.tushuangxi.smart.tv.lding.widget.LoadingDialogFg;
import com.tushuangxi.smart.tv.library.imageloaderfactory.ImageLoaderUtils;
import com.tushuangxi.smart.tv.library.updater.ui.UpdateVersionShowDialog;
import com.tushuangxi.smart.tv.library.updater.utils.AppUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tushuangxi.smart.tv.R;
import com.tushuangxi.smart.tv.lding.utils.ActivityUtils;
import com.tushuangxi.smart.tv.lding.utils.DoubleClickHelper;
import com.tushuangxi.smart.tv.lding.utils.HideUtil;
import com.tushuangxi.smart.tv.lding.utils.TipUtil;
import com.tushuangxi.smart.tv.library.router.UiPage;
import com.tushuangxi.smart.tv.net.EnvironmentConfig;
import com.vise.log.ViseLog;
import com.xiaomai.environmentswitcher.EnvironmentSwitchActivity;
import com.xiaomai.environmentswitcher.EnvironmentSwitcher;
import com.xiaomai.environmentswitcher.bean.EnvironmentBean;
import com.xiaomai.environmentswitcher.bean.ModuleBean;
import com.xiaomai.environmentswitcher.listener.OnEnvironmentChangeListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class InitActivity extends BaseActivity implements   interfaceUtilsAll.SiteNavigationRspView,OnEnvironmentChangeListener {

    String TAG = "TAG: "+ InitActivity.class.getSimpleName()+"....";
    public static  InitActivity mActivity;
    private UiStatusController mUiStatusController;
    private PreLoaderWrapper<String> preLoader;

    @BindView(R.id.ll_init_root)
    RelativeLayout ll_init_root;
    @BindView(R.id.bt_switch_host)
    DragFloatButton bt_switch_host;
    @BindView(R.id.iv_ImageView)
    ImageView iv_ImageView;
    @BindView(R.id.bt_switch_environment)
    TextView bt_switch_environment;
    @BindView(R.id.jcVideoPlayerStandard)
    JCVideoPlayerStandard jcVideoPlayerStandard;
    public static Data data = new Data();

    String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2897251542,2330444017&fm=26&gp=0.jpg";
    @Override
    public void initBundleData(Bundle bundle) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        HideUtil.initHide(mActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        goSiteNavigation(15,1);

        showSwitchHost();
    }

    private void showSwitchHost() {
        if (BuildConfig.DEBUG) {
            bt_switch_host.setVisibility(View.VISIBLE);
        } else {
            bt_switch_host.setVisibility(View.GONE);
        }
        bt_switch_host.setAlpha(0.5f);
//        bt_switch_host.setTitle("当前:" + host);
//        bt_switch_host.setOnClickListener(v -> showSwitchDialog(dragFloatButton));

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        //切换环境
        EnvironmentSwitcher.addOnEnvironmentChangeListener(this);
        if (!BuildConfig.DEBUG) {
            bt_switch_environment.setVisibility(View.GONE);
        }
        bt_switch_environment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EnvironmentSwitchActivity.launch(LoadingApp.getContext());
                Intent intent = new Intent(LoadingApp.getContext(), EnvironmentSwitchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        mActivity = InitActivity.this;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //报错  解决android.os.NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mUiStatusController = UiStatusController.get().bind(findViewById(R.id.ll_init_root));
        if (1==1){
            mUiStatusController.changeUiStatusIgnore(UiStatus.CONTENT);
        }else {
            mUiStatusController.changeUiStatusIgnore(UiStatus.EMPTY);
//            mUiStatusController.changeUiStatusIgnore(UiStatus.LOAD_ERROR);
        }

        addOnClickListeners(R.id.ll_init_root

        );

        //图片
//        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypeUil,url,iv_ImageView,true);
//        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypeGlide,url,iv_ImageView,true);
        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypePicasso,url,iv_ImageView,true);

        //存储
        KVUtils.getInstance().putString(AppGlobalConsts.Token,"Token3");
        if (KVUtils.getInstance().containsKey(AppGlobalConsts.Token)){
            KVUtils.getInstance().removeString(AppGlobalConsts.Token);
        }
        String Token = KVUtils.getInstance().getString(AppGlobalConsts.Token);
//        ViseLog.w(KVUtils.getInstance().getString(AppGlobalConsts.Token));


        //监听Data数据变化  回调
        DataLiveData.getInstance().observe(this, new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
//                mTextView.setText(data.getName());
                ViseLog.w( data.getName());
            }
        });
        //异步任务
        asyncChainTask();
        initJCVideoPlayerStandard();
    }

    private void initJCVideoPlayerStandard() {
        jcVideoPlayerStandard.setVisibility(View.GONE);
        String videoUrl = KVUtils.getInstance().getString(AppGlobalConsts.VIDE_OURL);
        jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        //增加封面1
//        jcVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4198741971,2693253849&fm=26&gp=0.jpg"));

        //增加封面
        jcVideoPlayerStandard.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        loadCover(jcVideoPlayerStandard.thumbImageView,"http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4",LoadingApp.getContext());
    }

    private void asyncChainTask() {
        AsyncChain.withWork(new AsyncChainRunnable(){
                    @Override
                    public void run(AsyncChainTask task){
                        //执行一个异步操作
//                        doSomething1(new someCallback1(){
//                            void callback(newResult){
//                                //标识一个行为出错了
//                                 task.onError(new AsyncChainError(""))
//                                //标识一个异步操作的结束，进行下一步操作
//                                task.onNext(newResult);
//                            }
//                        })
                        task.onError(new AsyncChainError("出错了"));
                        ViseLog.w("出错了1");
                        ViseLog.w("继续1");
                        task.onNext("继续");
                        ViseLog.w("继续");
                    }
                })
                .withMain(new AsyncChainRunnable(){
                    @Override
                    public void run(AsyncChainTask task){
                        //使用异步操作的结果更新UI
//                        updateUI(lastResult);
                        //标识整个异步链式结束了，即使后面还有行为没有执行也不会继续下去了
                        ViseLog.w("异步链式结束了");
                        task.onComplete();
                    }
                })
                .errorMain(new AsyncChainErrorCallback(){
                    @Override
                    public void error(AsyncChainError error) throws Exception {
                        //在主线程处理错误
                        //一旦error*方法执行，异步链就中断了
                        ViseLog.w("异步链就中断了");
                    }
                })
                .go(mContext);

        //延迟1000毫秒，然后Toast提示
        AsyncChain.delay(1000).withMain(new AsyncChainRunnable() {
                    @Override
                    public void run(AsyncChainTask task) throws Exception {
                        ViseLog.w("延迟1000毫秒");
                        task.onComplete();
                    }
                }).go(mContext);
    }

    @Override
    public void getHttpData(Context context) {
        initPreLoader(context);
    }

    private void initPreLoader(Context context) {
        preLoader = PreLoader.just(new Loader(), new LoaderDataListener());
        preLoader.listenData();
    }

    @Override
    public void updateSiteNavigationRspSuccess(SiteNavigationRsp siteNavigationRsp) {

    }

    @Override
    public void updateSiteNavigationRspError(Throwable throwable) {

    }


    class Loader implements DataLoader<String> {
        @Override
        public String loadData() {
            requestPermissionsMore();
            return null;
        }
    }

    class LoaderDataListener implements DataListener<String> {
        @SuppressLint("NewApi")
        @Override
        public void onDataArrived(String data) {
            initQueryText();
        }
    }

    //
    private void initQueryText() {

    }

    private void requestPermissionsMore() {
        XXPermissions.with(this)
                // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .constantRequest()
                // 申请悬浮窗权限
//                .permission(Permission.SYSTEM_ALERT_WINDOW)
//                .permission(Permission.SYSTEM_ALERT_WINDOW)
                // 申请单个权限
//                .permission(Permission.CAMERA)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                .permission(Permission.Group.LOCATION)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        hasAll = all;
                        if (all) {
                            //版本更新
                            getApkVersionUpdate(AppUtils.getVersionCode(mContext));

                        } else {
                            TipUtil.newThreadToast(R.string.not_granted_permission);
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        noQuick=quick;
                        if (quick) {
                            TipUtil.newThreadToast(R.string.denied_authorization);
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(mContext, denied);
                        } else {
                            TipUtil.newThreadToast(R.string.failed_to_get_permission);
                        }
                    }
                });
    }

    private boolean hasAll,noQuick;

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionType() {
        return TransitionMode.FADE;
    }

    @Override
    protected boolean isBindEventBus() {
        return true;
    }

    //粘性事件  处理消息
    @Subscribe(threadMode= ThreadMode.MAIN, sticky=false)
    public void myEventBusMessage(EventMessage eventMessage){
        switch (eventMessage.getCode()) {

            default:
        }
    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return true;
    }

    @Override
    protected boolean isResultOK() {
        return false;
    }

    @Override
    protected boolean isOpenFloatingAnimationService() {
        return false;
    }

    @Override
    protected boolean isOpenFloatingErWerMaService() {
        return false;
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_init;
    }

    @Override
    protected View getContentViewLayoutView() {
        return null;
    }

    @Override
    public void onBackPressed() {
        if (DoubleClickHelper.isOnDoubleClick()) {
            //移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            ll_init_root.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.finishAllActivity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, 300);
        } else {
            TipUtil.newThreadToast(R.string.again_to_exit);
        }
    }

    //接入按钮  3s内只可响应点击一次
    private boolean isoncl=true;
    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.ll_init_root:
                FloatingToastUtils.showCenterToast(ll_init_root,"点击3s后就改成true，这样zhi就实现只dao点击一次了");

                //修改 Data
                Data data = DataLiveData.getInstance().getValue();
                data.setName("我是" + Math.random());
                DataLiveData.getInstance().setValue(data);

                if(isoncl){
                    isoncl=false;
                    //点击3s后就改成true，这样zhi就实现只dao点击一次了
                    ll_init_root.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isoncl=true;
                        }
                    },3000);

//                UiPage.init(mContext).with(mActivity, QianhaiActivity.class,false);
//                    UiPage.init(mContext).with(mActivity, PartyActivity.class,false);
                }
                break;

            case R.id.bt_joinAuthor:


                break;

            default:
        }
    }

    private LoadingDialogFg loadingDialogFg;

    //显示等待框
    public void showLoadingfg(String msg,boolean touch){
        if(loadingDialogFg == null){
            loadingDialogFg = new LoadingDialogFg();
        }else{
            loadingDialogFg.dismiss();
        }
        loadingDialogFg.setMsg(msg)
                .setOnTouchOutside(touch)
                .show(getSupportFragmentManager(),"loading");
        //fragment的话就把getSupportFragmentManager参数换成getChildFragmentManager
    }

    //动态修改等待框中的文字
    public void setLoadingMsg(String msg){
        if(loadingDialogFg == null){
            return;
        }
        loadingDialogFg.setMsg(msg);
    }

    //隐藏等待框
    public void hideLoadingfg(){
        if(loadingDialogFg != null){
            loadingDialogFg.dismiss();
        }
    }

    //----------------------------------------------------------------------------------------------
    private void goSiteNavigation(int pageNum,int pageSize) {
        SiteNavigationRspPresenter.getPresenter(this,mContext).requestSiteNavigationRspList( pageNum,pageSize,this);
    }
    public static void getApkVersionUpdate(int versionNumber) {
//        ApkVersionUpdateJsonData jsonData = new ApkVersionUpdateJsonData();
//        jsonData.setVersionNumber(versionNumber);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(jsonData));
//        RetrofitManager.getDefault(HostType.TYPE_HOST_HENGYUANIOT).getApkVersionUpdateRspObservable(body)
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ApkVersionUpdateRsp>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(ApkVersionUpdateRsp apkVersionUpdateRsp) {
//                        ViseLog.w("TAG","ApkVersionUpdateRsp:"+apkVersionUpdateRsp.getMessage());
//                        if (apkVersionUpdateRsp.getCode()==AppGlobalConsts.HTTP_SUCCESS){
//                            if (apkVersionUpdateRsp.getResult()==null){
//                                return;
//                            }
//                            int versionNumber = apkVersionUpdateRsp.getResult().getVersionNumber();
//                            String url = apkVersionUpdateRsp.getResult().getUrl();
//                            //拼接
//                            String apkUrl = ApiConstants.BASE_HOST + url;
//                            if (!TextUtils.isEmpty(apkUrl)) {
//                                updateCheck(versionNumber, apkUrl);
//                            }
//                        }
//                    }
//                });
    }

    public static UpdateVersionShowDialog dialog;
    public static void updateCheck(int versionNumber, String apkUrl) {
        if(versionNumber > AppUtils.getVersionCode(mActivity)){
            //发现新版本
            dialog = UpdateVersionShowDialog.show(mActivity, apkUrl);
        }
    }

    @Override
    public void onNetworkStateChanged(boolean networkConnected, NetworkInfo currentNetwork, NetworkInfo lastNetwork) {
        if(networkConnected) {
//            ViseLog.w(TAG,"网络状态:" + (null == currentNetwork ? "" : ""+currentNetwork.getTypeName()+":"+currentNetwork.getState()));
//            TipUtil.newThreadToast("网络已连接!");
            //版本更新
            if (dialog!=null&&dialog.commitUpdater){
                dialog.goUpdater("网络已连接,请稍后...");
                ViseLog.w("goUpdater...");
            }
        } else {
//            TipUtil.newThreadToast("网络已断开!");
        }
//        ViseLog.w(TAG,null == currentNetwork ? "网络状态:无网络连接" : "网络状态:"+currentNetwork.toString());
    }

    /**
     * 加载第四秒的帧数作为封面
     *  url就是视频的地址
     */
    public static void loadCover(ImageView imageView, String url, Context context) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(3000000)
                                .centerCrop()
//                                .error(R.mipmap.eeeee)//可以忽略
//                                .placeholder(R.mipmap.ppppp)//可以忽略
                )
                .load(url)
                .into(imageView);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
      if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                exit();
                ActivityUtils.finishAllActivity();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exit() {
        if (preLoader != null) {
            preLoader.destroy();
        }
    }

    @Override
    public void onEnvironmentChanged(ModuleBean module, EnvironmentBean oldEnvironment, EnvironmentBean newEnvironment) {
        String msg = module.getAlias()+" "+module.getName() + " 模块: 由" + oldEnvironment.getName() + "环境，Url=" + oldEnvironment.getUrl()
                + ",切换为" + newEnvironment.getName() + "环境，Url=" + newEnvironment.getUrl();
        Toast.makeText(LoadingApp.getContext(),msg,Toast.LENGTH_LONG).show();
        ViseLog.w( msg);
//        if (module.equals(EnvironmentSwitcher.MODULE_APP)) {
//            // 如果环境切换后重新请求的接口需要 token，可以通过 postDelay 在延迟一定时间后再请求
//            // if the request need token, you can send in postDelay.
//            long delayTime = 1500;
//            findViewById(R.id.ll_init_root).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    // 发送需要 token 参数的接口请求
//                    // send the request need token
//                    Log.e(TAG, "run: send request");
//
//                    Toast.makeText(LoadingApp.getContext(), "send request", Toast.LENGTH_SHORT).show();
//                }
//            }, delayTime);
//        }

        EnvironmentConfig.LogSwitcher(module,newEnvironment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preLoader != null) {
            preLoader.destroy();
        }
        EnvironmentSwitcher.removeOnEnvironmentChangeListener(this);
    }
}
