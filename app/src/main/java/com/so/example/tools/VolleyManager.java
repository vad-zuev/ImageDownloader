package com.so.example.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RedirectError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vadim Zuev
 * @version 1.0
 * Sample implementation as per official documentation with
 * some minor changes.
 */

/**
 * Singleton class for managing Volley requests
 */
public class VolleyManager {

    private static VolleyManager that;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;
    private static final Map<Class, String> mErrorMessageMap = new HashMap<>(7);

    /*
      Private constructor to ensure the Singleton pattern. If you
      are not familiar with this approach, see
        https://en.wikipedia.org/wiki/Singleton_pattern
        and
        http://www.oodesign.com/singleton-pattern.html
    */
    private VolleyManager(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQ();
        final int MAX_CACHE_SIZE = 30;
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(MAX_CACHE_SIZE);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
        //initialize error map
        mErrorMessageMap.put(NoConnectionError.class, "No connection");
        mErrorMessageMap.put(AuthFailureError.class, "Authentication failed");
        mErrorMessageMap.put(TimeoutError.class, "Connection timeout");
        mErrorMessageMap.put(ParseError.class, "Failed to parse the response");
        mErrorMessageMap.put(ServerError.class, "Internal server error");
        mErrorMessageMap.put(RedirectError.class, "Redirect error");
        mErrorMessageMap.put(NetworkError.class, "Network error");
    }

    /**
     * @param error the occurred VolleyError
     * @return the message based on the type of the error
     */
    public String checkError(VolleyError error) {
        return mErrorMessageMap.get(error.getClass()) == null ? "Unknown error" :
                mErrorMessageMap.get(error.getClass());
    }

    public static synchronized VolleyManager getInstance(Context context) {
        if (that == null)
            that = new VolleyManager(context);
        return that;
    }


    public RequestQueue getRequestQ() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext);
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
