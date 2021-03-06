package io.toasterapp.toaster_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebBackForwardList;
import android.graphics.Color;
import android.util.Log;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

public class MainActivity extends Activity {

    private static WebView mWebView;
    private static String mUserId;
    private static PrivateChannel mChannel;
    private static Pusher mPusher;

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

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        settings.setUserAgentString(
                settings.getUserAgentString()
                        + " "
                        + getString(R.string.user_agent_suffix)
        );
        
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.setWebViewClient(new MyCustomWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
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
            mWebView.loadUrl("http://192.168.0.106:3000");
        }

        SharedPreferences prefs = getSharedPreferences("UserInfo", 0);
        if (prefs.getString("userId", "").toString() != null) {
            mUserId = prefs.getString("userId", "").toString();
            Log.d("mUserId", mUserId);
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

        if (webCanGoBack()) {
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

//            if (url.contains("posts")) {
//
//            }

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
        String historyUrl="";
        WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
        if (mWebBackForwardList.getCurrentIndex() > 0) {
//            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
//            Log.d("historyUrl", historyUrl);

            if (mWebView.getUrl().contains("posts") || mWebView.getUrl().contains("settings")) {
                return true;
            }
            return false;
        }else {
            return mWebView.canGoBack();
        }
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            if (message.contains("logout")) {
                mPusher.disconnect();
                return true;
            }
            Log.d("Sign in userId", message);

            SharedPreferences prefs = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userId", message);
            editor.commit();
            mUserId = message;

            HttpAuthorizer authorizer = new HttpAuthorizer("http://example.com/some_auth_endpoint");
            PusherOptions options = new PusherOptions().setAuthorizer(authorizer);

            mPusher = new Pusher("3f8ba7f168a24152f488", options);

            mPusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    System.out.println("State changed to " + change.getCurrentState() +
                            " from " + change.getPreviousState());
                    if (change.getCurrentState() == ConnectionState.CONNECTED) {
                        mPusher.connect();
                    }
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    System.out.println("There was a problem connecting!");
                }
            }, ConnectionState.ALL);

            // Subscribe to a channel
            mChannel = mPusher.subscribePrivate("userId",
                    new PrivateChannelEventListener() {
                        @Override
                        public void onEvent(String channelName, String eventName, String data) {
                            //TODO
                            Log.d("channel event", channelName);
                        }

                        @Override
                        public void onSubscriptionSucceeded(String channelName) {
                            //TODO
                            Log.d("subscription success", channelName);
                        }

                        @Override
                        public void onAuthenticationFailure(String message, Exception e) {
                            System.out.println(
                                    String.format("Authentication failure due to [%s], exception was [%s]", message, e)
                            );
                        }

                        // Other ChannelEventListener methods
                    });

            // Bind to listen for events called "my-event" sent to "my-channel"
            mChannel.bind("my-event", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channel, String event, String data) {
                    System.out.println("Received event with data: " + data);
                }
            });

            result.confirm();
            return true;
        }
    }

//
//    @Override
//    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//        Log.d("SampleActivity", consoleMessage.message() + " -- From line "
//                + consoleMessage.lineNumber() + " of "
//                + consoleMessage.sourceId() );
//
//
//        SharedPreferences prefs = getSharedPreferences("UserInfo", 0);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("userId", consoleMessage.message());
//        editor.commit();
//
//        return true;
//    }
}
