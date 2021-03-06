package org.cc98.mycc98.activity.base;

import android.annotation.TargetApi;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;


import com.baidu.mobstat.StatService;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.ChromeClientCallbackManager;
import com.orhanobut.logger.Logger;

import org.cc98.mycc98.MainApplication;
import org.cc98.mycc98.R;
import org.cc98.mycc98.activity.UserProfileActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.Response;
import win.pipi.api.network.CC98APIInterface;
import win.pipi.api.network.CC98APIManager;

/**
 * Created by pipi6 on 2018/1/9.
 */

public class BaseWebViewActivity extends BaseSwipeBackActivity implements ChromeClientCallbackManager.ReceivedTitleCallback {
    public static final String UTF_8 = "utf-8";

    protected String urlToLoad;
    protected String callBridge;
    protected AgentWeb agentWeb;
    protected WebView webView;
    protected LinearLayout mLinearLayout;


    protected void initWebView(String url) {
        logi(url);
        urlToLoad = url;

        if (mLinearLayout == null) {
            Logger.w("Must Initialze Webview container");
            return;
        }
        agentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                //.setWebViewClient(new ApiGrantedWebViewClient())
                .createAgentWeb()  //
                .ready()
                .go(urlToLoad);
        webView = agentWeb.getWebCreator().get();
        configWebSettings(agentWeb.getAgentWebSettings().getWebSettings());

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        setTitle(title);
    }

    protected WebSettings configWebSettings(WebSettings webSettings) {
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setTextZoom(100);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        webSettings.setDefaultTextEncodingName(UTF_8);//设置编码格式
        webSettings.setDefaultFontSize(22);//设置 WebView 字体的大小，默认大小为 16
        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setMinimumFontSize(14);//设置 WebView 支持的最小字体大小，默认为 8
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能


        webSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);

        return webSettings;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView!=null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        if (agentWeb!=null)
        agentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (agentWeb!=null)
        agentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (agentWeb!=null)
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //WARNING:android.R.id   =.=
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
