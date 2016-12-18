package at.alexnavratil.navhelper.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by alexnavratil on 29/01/16.
 */
public class ReadabilityWebView extends WebView {
    public ReadabilityWebView(Context context) {
        this(context, null, 0);
    }

    public ReadabilityWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadabilityWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        optimizeWebView();
    }

    public void loadData(String content) {
        String html = prepareContent(content);
        this.loadDataWithBaseURL("file:///android_res/", html, "text/html", "UTF-8", null);
    }

    private String prepareContent(String content) {
        return String.format("<!DOCTYPE html><html><head><meta charset=\"utf-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"><script> readConvertLinksToFootnotes = false; readStyle = 'style-ebook'; readSize = 'size-medium'; readMargin = 'margin-x-narrow'; _readability_script = document.createElement('script'); _readability_script.type = 'text/javascript'; _readability_script.src = 'file:///android_res/raw/readability_script.js'; document.documentElement.appendChild(_readability_script); _readability_css = document.createElement('link'); _readability_css.rel = 'stylesheet'; _readability_css.href = 'file:///android_res/raw/readability_style.css'; _readability_css.type = 'text/css'; _readability_css.media = 'all'; document.documentElement.appendChild(_readability_css);</script></head><body><div class=\"style-ebook\">%s</div></body></html>", content);
    }

    private void optimizeWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);

        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent externalPage = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(externalPage);
                return true;
            }
        });

        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        this.setLongClickable(false);
        this.setHapticFeedbackEnabled(false);
    }

    public void enableFullscreenSupport(final WebViewFullscreenSupport fullscreenImpl) {
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                fullscreenImpl.showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                fullscreenImpl.hideCustomView();
            }
        });
    }
}
