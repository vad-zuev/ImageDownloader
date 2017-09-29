package vad.zuev.imagedownloader.tools;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

public final class Utils {
    /**
     * Displays a short {@link Toast}
     *
     * @param context the Context to create the Toast with
     * @param msg     the message
     */
    public static void toastShort(@NonNull Context context, @NonNull String msg) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays a long {@link Toast}
     *
     * @param context the Context to create the Toast with
     * @param msg     the message
     */
    public static void toastLong(@NonNull Context context, @NonNull String msg) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
    }

}
