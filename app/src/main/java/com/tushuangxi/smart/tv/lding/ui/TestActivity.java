package com.tushuangxi.smart.tv.lding.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.lifecycle.Observer;

import com.billy.android.preloader.PreLoader;
import com.billy.android.preloader.PreLoaderWrapper;
import com.billy.android.preloader.interfaces.DataListener;
import com.billy.android.preloader.interfaces.DataLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fengchen.uistatus.UiStatusController;
import com.fengchen.uistatus.annotation.UiStatus;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tushuangxi.smart.tv.R;
import com.tushuangxi.smart.tv.lding.entity.SiteNavigationRsp;
import com.tushuangxi.smart.tv.lding.entity.livedata.Data;
import com.tushuangxi.smart.tv.lding.entity.livedata.DataLiveData;
import com.tushuangxi.smart.tv.lding.eventbus.EventMessage;
import com.tushuangxi.smart.tv.lding.http.ApiConstants;
import com.tushuangxi.smart.tv.lding.other.AppGlobalConsts;
import com.tushuangxi.smart.tv.lding.rerxmvp.base.BaseActivity;
import com.tushuangxi.smart.tv.lding.rerxmvp.interfaceUtils.interfaceUtilsAll;
import com.tushuangxi.smart.tv.lding.rerxmvp.presenter.SiteNavigationRspPresenter;
import com.tushuangxi.smart.tv.lding.utils.ActivityUtils;
import com.tushuangxi.smart.tv.lding.utils.DoubleClickHelper;
import com.tushuangxi.smart.tv.lding.utils.HideUtil;
import com.tushuangxi.smart.tv.lding.utils.TipUtil;
import com.tushuangxi.smart.tv.lding.widget.LoadingDialogFg;
import com.tushuangxi.smart.tv.library.asyncchain.AsyncChain;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainError;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainErrorCallback;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainRunnable;
import com.tushuangxi.smart.tv.library.asyncchain.core.AsyncChainTask;
import com.tushuangxi.smart.tv.library.imageloaderfactory.ImageLoaderUtils;
import com.tushuangxi.smart.tv.library.loading.conn.LoadingApp;
import com.tushuangxi.smart.tv.library.logcat.FloatingLogcatView;
import com.tushuangxi.smart.tv.library.mmkv.KVUtils;
import com.tushuangxi.smart.tv.library.router.UiPage;
import com.tushuangxi.smart.tv.library.updater.ui.UpdateVersionShowDialog;
import com.tushuangxi.smart.tv.library.updater.utils.AppUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class TestActivity extends BaseActivity implements   interfaceUtilsAll.SiteNavigationRspView{

    String TAG = "TAG: "+ TestActivity.class.getSimpleName()+"....";
    public static TestActivity mActivity;
    private UiStatusController mUiStatusController;
    private PreLoaderWrapper<String> preLoader;

    @BindView(R.id.ll_init_root)
    RelativeLayout ll_init_root;
    @BindView(R.id.iv_ImageView)
    ImageView iv_ImageView;
    @BindView(R.id.bt_joinAuthor)
    Button bt_joinAuthor;
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

        if (hasAll){
//            FloatingLogcatView.getInstance(getApplicationContext());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        mActivity = TestActivity.this;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //??????  ??????android.os.NetworkOnMainThreadException
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
                , R.id.bt_joinAuthor
        );

        //??????
//        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypeUil,url,iv_ImageView,true);
//        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypeGlide,url,iv_ImageView,true);
        ImageLoaderUtils.loadPic(ImageLoaderUtils.loadTypePicasso,url,iv_ImageView,true);

        //??????
        KVUtils.getInstance().putString(AppGlobalConsts.Token,"Token3");
        if (KVUtils.getInstance().containsKey(AppGlobalConsts.Token)){
            KVUtils.getInstance().removeString(AppGlobalConsts.Token);
        }
        String Token = KVUtils.getInstance().getString(AppGlobalConsts.Token);
//        ViseLog.w(TAG,KVUtils.getInstance().getString(AppGlobalConsts.Token));


        //??????Data????????????  ??????
        DataLiveData.getInstance().observe(this, new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
//                mTextView.setText(data.getName());
                ViseLog.w( data.getName());
            }
        });
        //????????????
        asyncChainTask();
        initJCVideoPlayerStandard();
    }

    private void initJCVideoPlayerStandard() {
        jcVideoPlayerStandard.setVisibility(View.VISIBLE);
        String videoUrl = KVUtils.getInstance().getString(AppGlobalConsts.VIDE_OURL);
        jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        //????????????1
//        jcVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4198741971,2693253849&fm=26&gp=0.jpg"));

        //????????????
        loadCover(jcVideoPlayerStandard.thumbImageView,"http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", LoadingApp.getContext());
    }
    private void asyncChainTask() {
        AsyncChain.withWork(new AsyncChainRunnable(){
                    @Override
                    public void run(AsyncChainTask task){
                        //????????????????????????
//                        doSomething1(new someCallback1(){
//                            void callback(newResult){
//                                //???????????????????????????
//                                 task.onError(new AsyncChainError(""))
//                                //?????????????????????????????????????????????????????????
//                                task.onNext(newResult);
//                            }
//                        })
                        task.onError(new AsyncChainError("?????????"));
                        ViseLog.w("?????????1");
                        ViseLog.w("??????1");
                        task.onNext("??????");
                        ViseLog.w("??????");
                    }
                })
                .withMain(new AsyncChainRunnable(){
                    @Override
                    public void run(AsyncChainTask task){
                        //?????????????????????????????????UI
//                        updateUI(lastResult);
                        //????????????????????????????????????????????????????????????????????????????????????????????????
                        ViseLog.w("?????????????????????");
                        task.onComplete();
                    }
                })
                .errorMain(new AsyncChainErrorCallback(){
                    @Override
                    public void error(AsyncChainError error) throws Exception {
                        //????????????????????????
                        //??????error*????????????????????????????????????
                        ViseLog.w("?????????????????????");
                    }
                })
                .go(mContext);

        //??????1000???????????????Toast??????
        AsyncChain.delay(1000).withMain(new AsyncChainRunnable() {
                    @Override
                    public void run(AsyncChainTask task) throws Exception {
                        ViseLog.w("??????1000??????");
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
                // ????????????????????????????????????????????????????????????????????????
                .constantRequest()
                // ?????????????????????
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                // ??????????????????
//                .permission(Permission.CAMERA)
                // ??????????????????
                .permission(Permission.Group.STORAGE)
                .permission(Permission.Group.LOCATION)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        hasAll = all;
                        if (all) {
                            //????????????
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
                            // ??????????????????????????????????????????????????????????????????
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

    //????????????  ????????????
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
            //???????????????????????????????????????????????????????????????
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

    //????????????  3s???????????????????????????
    private boolean isoncl=true;
    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.ll_init_root:
//                if (hasAll){
//                    UiPage.init(mContext).with(mActivity, PartyActivity.class,false);
//                }else if (noQuick){
//                    TipUtil.showToast(mContext,R.string.denied_authorization, 1000);
//                    requestPermissionsMore();
//                }else {
//                    TipUtil.showToast(mContext,R.string.not_granted_permission, 1000);
//                }
                ViseLog.w("goUpdater...");

                //?????? Data
                Data data = DataLiveData.getInstance().getValue();
                data.setName("??????" + Math.random());
                DataLiveData.getInstance().setValue(data);


                break;

            case R.id.bt_joinAuthor:
                if(isoncl){
                    isoncl=false;
                    //??????3s????????????true?????????zhi????????????dao???????????????
                    ll_init_root.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isoncl=true;
                        }
                    },3000);

                    UiPage.init(mContext).with(mActivity, PartyActivity.class,false);
                }

                int a = 10;
                ViseLog.w("a:..."+a/0);
                break;

            default:
        }
    }

    private LoadingDialogFg loadingDialogFg;

    //???????????????
    public void showLoadingfg(String msg,boolean touch){
        if(loadingDialogFg == null){
            loadingDialogFg = new LoadingDialogFg();
        }else{
            loadingDialogFg.dismiss();
        }
        loadingDialogFg.setMsg(msg)
                .setOnTouchOutside(touch)
                .show(getSupportFragmentManager(),"loading");
        //fragment????????????getSupportFragmentManager????????????getChildFragmentManager
    }

    //?????????????????????????????????
    public void setLoadingMsg(String msg){
        if(loadingDialogFg == null){
            return;
        }
        loadingDialogFg.setMsg(msg);
    }

    //???????????????
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
//                            //??????
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
            //???????????????
            dialog = UpdateVersionShowDialog.show(mActivity, apkUrl);
        }
    }

    @Override
    public void onNetworkStateChanged(boolean networkConnected, NetworkInfo currentNetwork, NetworkInfo lastNetwork) {
        if(networkConnected) {
//            ViseLog.w(TAG,"????????????:" + (null == currentNetwork ? "" : ""+currentNetwork.getTypeName()+":"+currentNetwork.getState()));
//            TipUtil.newThreadToast("???????????????!");
            //????????????
            if (dialog!=null&&dialog.commitUpdater){
                dialog.goUpdater("???????????????,?????????...");
                ViseLog.w("goUpdater...");
            }
        } else {
//            TipUtil.newThreadToast("???????????????!");
        }
//        ViseLog.w(TAG,null == currentNetwork ? "????????????:???????????????" : "????????????:"+currentNetwork.toString());
    }
    /**
     * ????????????????????????????????????
     *  url?????????????????????
     */
    public static void loadCover(ImageView imageView, String url, Context context) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(3000000)
                                .centerCrop()
//                                .error(R.mipmap.eeeee)//????????????
//                                .placeholder(R.mipmap.ppppp)//????????????
                )
                .load(url)
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preLoader != null) {
            preLoader.destroy();
        }
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
}
