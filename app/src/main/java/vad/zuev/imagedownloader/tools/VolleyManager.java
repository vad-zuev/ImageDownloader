package vad.zuev.imagedownloader.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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
 * Singleton class for managing Volley requests. Sample implementation as per official documentation with
 * some minor changes.
 *
 * @see <a href="https://developer.android.com/training/volley/requestqueue.html">Setting Up a RequestQueue</a>
 */
public class VolleyManager {

    private static VolleyManager that;
    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static final Map<Class, String> errorMap = new HashMap<>(7);

    private VolleyManager() {
        errorMap.put(NoConnectionError.class, "No connection");
        errorMap.put(AuthFailureError.class, "Authentication failed");
        errorMap.put(TimeoutError.class, "Connection timeout");
        errorMap.put(ParseError.class, "Failed to parse the response");
        errorMap.put(ServerError.class, "Internal server error");
        errorMap.put(RedirectError.class, "Redirect error");
        errorMap.put(NetworkError.class, "Network error");
    }

    /**
     * @param error the occurred VolleyError
     * @return the message based on the type of the error
     */
    public String checkError(VolleyError error) {
        Class clazz = error.getClass();
        return errorMap.get(clazz) == null ? "Unknown error" : errorMap.get(clazz);
    }

    /**
     * @param context  Context to init the RequestQueue
     * @return the singleton instance
     */
    public static synchronized VolleyManager getInstance(@NonNull Context context) {
        if (that == null)
            that = new VolleyManager();
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        if (imageLoader == null) {
            final int MAX_CACHE_SIZE = 30;
            imageLoader = new ImageLoader(requestQueue,
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
        }
        return that;
    }

    /**
     * Cancels all pending/running download requests
     */
    public void cancelAll(){
        if(requestQueue != null)
            requestQueue.cancelAll(request -> true);
    }

    /**
     * @return thee ImageLoader instance
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
