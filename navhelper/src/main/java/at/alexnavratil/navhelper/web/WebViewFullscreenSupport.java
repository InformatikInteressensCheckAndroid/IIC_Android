package at.alexnavratil.navhelper.web;

import android.view.View;
import android.webkit.WebChromeClient;

/**
 * Created by alexnavratil on 29/01/16.
 */
public interface WebViewFullscreenSupport {
    void showCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void hideCustomView();
}
