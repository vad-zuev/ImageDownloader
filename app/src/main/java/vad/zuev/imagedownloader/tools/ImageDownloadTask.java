package vad.zuev.imagedownloader.tools;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vad.zuev.imagedownloader.tools.BasicImageDownloader.ImageDownloadListener;

import static vad.zuev.imagedownloader.tools.BasicImageDownloader.ImageError;

/**
 * Background task that downloads an image and provides the result via {@link ImageDownloadListener} interface
 */
public class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {

    private final String TAG = getClass().getSimpleName();
    private boolean needProgress;
    private boolean cancelledByUser;
    private ImageDownloadListener downloadListener;
    private ImageError error;

    /**
     * @param needProgress whether to provide download progress (via {@link ImageDownloadListener#onProgressChange(int)})
     * @param downloadListener interface instance to receive completion/progress change callbacks
     */
    public ImageDownloadTask(boolean needProgress, @NonNull ImageDownloadListener downloadListener) {
        this.needProgress = needProgress;
        this.downloadListener = downloadListener;
    }

    /**
     * Sets a flag indicating that the task was cancelled intentionally and no error should be reported
     */
    public void setCancelledByUser() {
        cancelledByUser = true;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            if (needProgress) {
                connection.connect();
                final int length = connection.getContentLength();
                if (length <= 0) {
                    error = new ImageError("Invalid content length. The URL is probably not pointing to a file")
                            .setErrorCode(ImageError.ERROR_INVALID_FILE);
                    this.cancel(true);
                }
                is = new BufferedInputStream(connection.getInputStream(), 8192);
                out = new ByteArrayOutputStream();
                byte bytes[] = new byte[8192];
                int count;
                long read = 0;
                while ((count = is.read(bytes)) != -1) {
                    read += count;
                    out.write(bytes, 0, count);
                    publishProgress((int) ((read * 100) / length));
                }
                bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            } else {
                is = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (Throwable e) {
            if (!this.isCancelled()) {
                error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                this.cancel(true);
            }
        } finally {
            try {
                if (connection != null)
                    connection.disconnect();
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
                error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                this.cancel(true);
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result == null) {
            Log.e(TAG, "factory returned a null result");
            downloadListener.onError(new ImageError("downloaded file could not be decoded as bitmap").setErrorCode(ImageError.ERROR_DECODE_FAILED));
        } else {
            Log.d(TAG, "download complete, " + result.getByteCount() + " bytes transferred");
            downloadListener.onComplete(result);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        downloadListener.onProgressChange(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (!cancelledByUser)
            downloadListener.onError(error);
    }
}
