package io.toasterapp.toaster_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebBackForwardList;
import android.graphics.Color;
import android.util.Log;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;


public class MainActivity extends Activity {

    private XWalkView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        int statusbar_color = Color.rgb(255, 70, 79);
        window.setStatusBarColor(statusbar_color);

        mWebView = (XWalkView) findViewById(R.id.xwalkWebView);

//        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//
//        settings.setBuiltInZoomControls(true);
//        settings.setSupportZoom(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);
//        mWebView.setWebViewClient(new MyCustomWebViewClient());
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

//        mWebView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            public void onFocusChange(View arg0, boolean hasFocus) {
//
//                if (!hasFocus) {
//                    Log.d("hasFocus", "false");
//                    mWebView.requestFocus(View.FOCUS_DOWN);
//                    mWebView.setFocusable(true);
//                }
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(mWebView, 0);
//            }
//        });

        if (savedInstanceState==null) {
            mWebView.load("http://104.131.158.80/", null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.pauseTimers();
            mWebView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of the WebView
            mWebView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(webCanGoBack()){
//            webview.goBack();
            mWebView.load("javascript:$('[data-nav-container]').addClass('nav-view-direction-back');$('[data-navbar-container]').addClass('nav-bar-direction-back');history.back();", null);
        }else{
            super.onBackPressed();
        }

    }

    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            if (url.contains("posts")) {

            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // Display the keyboard automatically when relevant
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (url.contains("newPost")) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (url.contains("post")){
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    private boolean webCanGoBack() {
//        String historyUrl="";
//        WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
//        if (mWebBackForwardList.getCurrentIndex() > 0) {
//            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
//            Log.d("historyUrl", historyUrl);

            if (mWebView.getUrl().contains("posts") || mWebView.getUrl().contains("settings")) {
                return true;
            }
            return false;
//        }else {
//            return mWebView.canGoBack();
//        }
    }
}
