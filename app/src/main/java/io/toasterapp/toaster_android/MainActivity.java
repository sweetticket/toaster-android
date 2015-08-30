package io.toasterapp.toaster_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebBackForwardList;
import android.graphics.Color;
import android.util.Log;


public class MainActivity extends Activity {

    private WebView mWebView;

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

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new MyCustomWebViewClient());
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

//        mWebView.loadUrl("http://toaster-android.meteor.com/");
        mWebView.loadUrl("http://192.168.0.106:3000");
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
            mWebView.loadUrl("javascript:$('[data-nav-container]').addClass('nav-view-direction-back');$('[data-navbar-container]').addClass('nav-bar-direction-back');history.back();");
        }else{
            super.onBackPressed();
        }
    }

    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }


    private boolean webCanGoBack() {
        String historyUrl="";
        WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
        if (mWebBackForwardList.getCurrentIndex() > 0) {
//            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
//            Log.d("historyUrl", historyUrl);

            if (mWebView.getUrl().contains("posts")) {
                return true;
            }
            return false;
        }else {
            return mWebView.canGoBack();
        }
    }
}
