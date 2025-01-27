package com.muslimuniversity.muslimuniversity_online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private LottieAnimationView lottieAnimationView;

    private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2 seconds
    private long mLastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.bar2); // Assuming the progress bar's id is bar2
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        webView.clearCache(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setTextZoom(90); // Adjust the percentage as needed
        webView.setInitialScale(1); // Set the initial scale to 1
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Check for internet connectivity before loading the website
        if (isConnected()) {

            // Enable JavaScript for interactive content (if required by the website)
            webView.getSettings().setJavaScriptEnabled(true);

            // Set up WebViewClient to handle page loading and errors
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    // Handle potential loading errors (e.g., net::ERR_CACHE_MISS)
                    Log.e("WebViewError", "Error loading URL: " + failingUrl + ", Error: " + description);
                    Toast.makeText(MainActivity.this, "Error loading website. Check your internet connection or try again later.", Toast.LENGTH_LONG).show();
                }

                // Handle external URLs for specific apps
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("whatsapp://") || url.contains("facebook.com") ||
                            url.contains("youtube.com") || url.contains("linkedin.com") ||
                            url.startsWith("twitter://") || url.contains("telegram.me") || url.contains("t.me")
                            || url.startsWith("tel:") || url.startsWith("mailto:") ) {

                        openUrlWithIntent(url, " App not installed for this URL.");
                        return true;
                    }
                    return false;
                }
            });

            // Set up WebChromeClient to handle progress updates
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress); // Update progress bar visually
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

            webView.loadUrl("https://www.muslimuniversity.education/");
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void openUrlWithIntent(String url, String errorMessage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
