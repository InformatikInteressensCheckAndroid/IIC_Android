package at.ac.htlstp.app.cocolib.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NoCache;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexnavratil on 07.10.15.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private static Context mCtx;
    private final CookieStore mCookieStore = new CustomCookieStore();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleton(Context context) {
        mCtx = context;

        CookieManager manager = new CookieManager(mCookieStore, new CookiePolicy() {
            @Override
            public boolean shouldAccept(URI uri, HttpCookie httpCookie) {
                return true;
            }
        });

        CookieHandler.setDefault(manager);

        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                }) {
            protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight,
                                                       ImageView.ScaleType scaleType, final String cacheKey) {
                return new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        onGetImageSuccess(cacheKey, response);
                    }
                }, maxWidth, maxHeight, scaleType, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onGetImageError(cacheKey, error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headerMap = new HashMap<>(super.getHeaders());

                        List<HttpCookie> cookieList = null;
                        /*try {
                            cookieList = mCookieStore.get(new URI(super.getUrl()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }*/

                        cookieList = mCookieStore.getCookies();

                        StringBuilder strb = new StringBuilder();
                        for (HttpCookie cookie : cookieList) {
                            strb.append(cookie.getName() + "=" + cookie.getValue() + ";");
                        }

                        headerMap.put("Cookie", strb.toString());

                        return headerMap;
                    }
                };
            }
        };
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            mRequestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack(null)), 1);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
