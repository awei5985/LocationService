package edu.umd.mindlab.androidservicetest;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CASLoginActivity extends AppCompatActivity {

    public static final String TAG = "CASLoginActivity";
    public static WebView mWebView;
    private static final String TERMS_ACCEPT = "Are_Terms_Accepted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caslogin);

        // if the user has not logged out, it should take them directly to the main activity
        // if this causes any problems, just remove it. I think it will be fine without it.
        LoggedIn log = LoggedIn.getLog();
        if (log.getLoggedIn()) {

            Intent mainIntent = new Intent(CASLoginActivity.this, MainActivity.class);
            startActivity(mainIntent);

        }

        mWebView = (WebView) findViewById(R.id.caswebview);

        mWebView.setInitialScale(1);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("https://login.umd.edu/");
        Log.v(TAG, "Should have loaded login.");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url){
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);
                if (url.contains("demo") && cookies.contains("shib_idp_session")) {
                    destroyWebView();

                    // the cookies will not contain "login_again" only in the page that comes after CAS login, so let them in then
                    if(!cookies.contains("login_again")){

                        cookieManager.setCookie("https://login.umd.edu/", "shib_idp_session=login_again");

                        SharedPreferences sharedPref = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
                        boolean termsAccepted =  sharedPref.getBoolean(TERMS_ACCEPT, true);

                        // need to change this back to termsAccepted
                        if (termsAccepted){
                            Intent mainIntent = new Intent(CASLoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        } else{
                            Intent getInfoIntent = new Intent(CASLoginActivity.this, GetPersonalInfo.class);
                            startActivity(getInfoIntent);
                        }

                    }

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void destroyWebView() {

        // Make sure you remove the WebView from its parent view before doing anything.
        mWebView.removeAllViews();

        mWebView.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        mWebView.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        mWebView.loadUrl("about:blank");

        /* starting making changes
        mWebView.onPause();
        mWebView.removeAllViews();
        mWebView.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        mWebView.pauseTimers();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        mWebView.destroy();

        // Null out the reference so that you don't end up re-using it.
        mWebView = null; */
    }
}
