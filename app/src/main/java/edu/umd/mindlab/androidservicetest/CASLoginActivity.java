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
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CASLoginActivity extends AppCompatActivity {

    public static final String TAG = "CASLoginActivity";
    public static WebView mWebView;
    private static final String TERMS_ACCEPT = "Are_Terms_Accepted";
    private static String mLoginUrl = "https://login.umd.edu/";
    private boolean webViewActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caslogin);
    }

    @Override
    protected void onStart(){
        super.onStart();

        // if the user has not logged out, it should take them directly to the main activity
        // if this causes any problems, just remove it. I think it will be fine without it.
        LoggedIn log = LoggedIn.getLog();
        if (log.getLoggedIn()) {

            Intent mainIntent = new Intent(CASLoginActivity.this, MainActivity.class);
            startActivity(mainIntent);

        }

        Log.v(TAG, "CASLoginActivity:OnStart");

        createwView(mLoginUrl);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void destroyWebView() {

        if (mWebView == null) {
            return;
        }

        Log.v(TAG, "CASLoginActivity:DestroyWebView:DeleteAllData");
        WebStorage.getInstance().deleteAllData();

        // Make sure you remove the WebView from its parent view before doing anything.
        mWebView.removeAllViews();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:RemoveAllViews");

        mWebView.clearHistory();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:ClearHistory");

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        mWebView.clearCache(true);
        Log.v(TAG, "CASLoginActivity:DestroyWebView:ClearCache");

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        // mWebView.loadUrl("about:blank");
        // Log.v(TAG, "CASLoginActivity:DestroyWebView:LoadUrl Blank");

        // starting making changes
        mWebView.onPause();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:OnPause");

        //It was calling this twice, which I guess was causing a problem
        //mWebView.removeAllViews();
        mWebView.destroyDrawingCache();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:DestroyDrawingCache");

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        mWebView.pauseTimers();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:PauseTimers");

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        mWebView.destroy();
        Log.v(TAG, "CASLoginActivity:DestroyWebView:Destroy");

        // Null out the reference so that you don't end up re-using it.
        mWebView = null;
        Log.v(TAG, "CASLoginActivity:DestroyWebView Complete");
    }

    public void createwView(String url){

        mWebView = new WebView(CASLoginActivity.this);
        mWebView = (WebView) findViewById(R.id.caswebview);

        Log.v(TAG, "CASLoginActivity:CreateView - Hooking up XML ID to WebView Object");

        mWebView.setInitialScale(1);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        Log.v(TAG, "CASLoginActivity:CreateView - Setting Scale and View Mode");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Log.v(TAG, "CASLoginActivity:CreateView - Enabling JavaScript");

        mWebView.loadUrl(url);

        Log.v(TAG, "CASLoginActivity:CreateView - Url Loaded: " + url);

        Log.v(TAG, "CASLoginActivity:CreateView - WebView Created");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient - OnPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient - ShouldOverrideUrlLoaded");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url){
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);

                Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished - URL: " + url);

                Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished - cookies: " + cookies.toString());

                if (url != null &&
                        cookies != null &&
                        url.contains("demo") &&
                        cookies.contains("shib_idp_session")) {

                    destroyWebView();

                    Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished DEMO");

                    // the cookies will not contain "login_again" only in the page that comes after CAS login, so let them in then
                    if (!cookies.contains("login_again")) {

                        Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished Login_Again");

                        cookieManager.setCookie("https://login.umd.edu/", "shib_idp_session=login_again");

                        SharedPreferences sharedPref = getSharedPreferences("edu.umd.mindlab.androidservicetest", MODE_PRIVATE);
                        boolean termsAccepted = sharedPref.getBoolean(TERMS_ACCEPT, false);

                        // need to change this back to termsAccepted
                        if (termsAccepted) {
                            Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished TermsAccepted Check, CASLogin Complete");

                            Intent mainIntent = new Intent(CASLoginActivity.this, MainActivity.class);
                            mainIntent.putExtra("CallingActivity", "caslogin");
                            startActivity(mainIntent);

                        } else {
                            Log.v(TAG, "CASLoginActivity:CreateView:WebViewClient:OnPageFinished TermsAccepted Failed, CASLogin InComplete");

                            Intent getInfoIntent = new Intent(CASLoginActivity.this, GetPersonalInfo.class);
                            startActivity(getInfoIntent);
                        }

                    }

                    //Intent getInfoIntent = new Intent(CASLoginActivity.this, GetPersonalInfo.class);
                    //startActivity(getInfoIntent);

                }
            }


        });

    }
}
