package vad.zuev.imagedownloader.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vad.zuev.imagedownloader.tools.ImageDownloadTask;

@SuppressWarnings("unused, WeakerAccess")
public class BasicImageDownloader {

    private static ImageDownloadTask downloadTask;

    /**
     * Interface definition for callbacks to be invoked
     * when the image download status changes.
     */
    public interface ImageDownloadListener {
        /**
         * Invoked if an error has occurred and thus
         * the download did not complete
         *
         * @param error the occurred error
         */
        void onError(ImageError error);

        /**
         * Invoked every time the progress of the download changes
         *
         * @param percent new status in %
         */
        void onProgressChange(int percent);

        /**
         * Invoked after the image has been successfully downloaded
         *
         * @param result the downloaded image
         */
        void onComplete(Bitmap result);
    }

    /**
     * Downloads the image from the given URL using an {@link AsyncTask}. If a download
     * for the given URL is already in progress this method returns immediately.
     *
     * @param imageUrl        the URL to get the image from
     * @param displayProgress if <b>true</b>, the {@link ImageDownloadListener#onProgressChange(int)}
     *                        callback will be triggered to notify the caller of the download progress
     * @param listener        interface instance to receive completion/progress change callbacks
     */
    public static void download(@NonNull String imageUrl, boolean displayProgress, @NonNull ImageDownloadListener listener) {
        if (downloadTask != null && downloadTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.e("BasicImageDownloader", "A download is already running: concurrent downloads are not yet supported");
            return;
        }
        downloadTask = new ImageDownloadTask(displayProgress, listener);
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrl);
    }

    public static void cancel() {
        if (downloadTask != null && downloadTask.getStatus() != AsyncTask.Status.FINISHED) {
            downloadTask.setCancelledByUser();
            downloadTask.cancel(true);
        }
    }

    /**
     * Interface definition for callbacks to be invoked when
     * the image save procedure status changes
     */
    public interface OnBitmapSaveListener {
        /**
         * Invoked to notify that the image has been
         * successfully saved
         */
        void onBitmapSaved();

        /**
         * Invoked if an error occurs while saving the image
         *
         * @param error the occurred error
         */
        void onBitmapSaveError(ImageError error);
    }

    /**
     * Tries to write the given Bitmap to device's storage using an {@link AsyncTask}.
     * This method handles common errors and will provide an error message via the
     * {@link OnBitmapSaveListener#onBitmapSaveError(ImageError)} callback in case anything
     * goes wrong.
     *
     * @param imageFile       a File representing the image to be saved
     * @param image           the actual Bitmap to save
     * @param listener        an OnBitmapSaveListener instance
     * @param format          image format. Can be one of the following:<br>
     *                        <ul>
     *                        <li>{@link android.graphics.Bitmap.CompressFormat#PNG}</li>
     *                        <li>{@link android.graphics.Bitmap.CompressFormat#JPEG}</li>
     *                        <li>{@link android.graphics.Bitmap.CompressFormat#WEBP}</li>
     *                        </ul>
     * @param shouldOverwrite whether to overwrite an existing file
     */
    public static void writeToDisk(@NonNull final File imageFile, @NonNull final Bitmap image,
                                   @NonNull final OnBitmapSaveListener listener,
                                   @NonNull final Bitmap.CompressFormat format, boolean shouldOverwrite) {

        if (imageFile.isDirectory()) {
            listener.onBitmapSaveError(new ImageError("the specified path points to a directory, " +
                    "should be a file").setErrorCode(ImageError.ERROR_IS_DIRECTORY));
            return;
        }

        if (imageFile.exists()) {
            if (!shouldOverwrite) {
                listener.onBitmapSaveError(new ImageError("file already exists, " +
                        "write operation cancelled").setErrorCode(ImageError.ERROR_FILE_EXISTS));
                return;
            } else if (!imageFile.delete()) {
                listener.onBitmapSaveError(new ImageError("could not delete existing file, " +
                        "most likely the write permission was denied")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        }

        File parent = imageFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            listener.onBitmapSaveError(new ImageError("could not create parent directory")
                    .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
            return;
        }

        try {
            if (!imageFile.createNewFile()) {
                listener.onBitmapSaveError(new ImageError("could not create file")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED));
                return;
            }
        } catch (IOException e) {
            listener.onBitmapSaveError(new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION));
            return;
        }

        new Thread(() -> {
            ImageError error = null;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imageFile);
                image.compress(format, 100, fos);
            } catch (IOException e) {
                error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(error != null)
                listener.onBitmapSaveError(error);
            else
                listener.onBitmapSaved();

        }).start();
    }

    /**
     * Interface definition for callbacks to be invoked
     * after the image read operation finishes
     */
    public interface OnImageReadListener {
        void onImageRead(Bitmap bitmap);
        void onReadFailed();
    }

    /**
     * Reads the given file as Bitmap in the background. The appropriate callback
     * of the provided <i>OnImageReadListener</i> will be triggered upon completion.
     *
     * @param imageFile the file to read
     * @param listener  the listener to notify the caller when the
     *                  image read operation finishes
     */
    public static void readFromDisk(@NonNull File imageFile, @NonNull final OnImageReadListener listener) {
        new Thread(() -> {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            if (bitmap != null)
                listener.onImageRead(bitmap);
            else
                listener.onReadFailed();
        }).start();
    }


    /**
     * Represents an error that has occurred while
     * downloading image or writing it to disk. Since
     * this class extends {@code Throwable}, you may get the
     * stack trace from an {@code ImageError} object
     */
    public static final class ImageError extends Throwable {

        private int errorCode;
        /**
         * An exception was thrown during an operation.
         * Check the error message for details.
         */
        public static final int ERROR_GENERAL_EXCEPTION = -1;
        /**
         * The URL does not point to a valid file
         */
        public static final int ERROR_INVALID_FILE = 0;
        /**
         * The downloaded file could not be decoded as bitmap
         */
        public static final int ERROR_DECODE_FAILED = 1;
        /**
         * File already exists on disk and shouldOverwrite == false
         */
        public static final int ERROR_FILE_EXISTS = 2;
        /**
         * Could not complete a file operation, most likely due to permission denial
         */
        public static final int ERROR_PERMISSION_DENIED = 3;
        /**
         * The target file is a directory
         */
        public static final int ERROR_IS_DIRECTORY = 4;


        public ImageError(@NonNull String message) {
            super(message);
        }

        public ImageError(@NonNull Throwable error) {
            super(error.getMessage(), error.getCause());
            this.setStackTrace(error.getStackTrace());
        }

        /**
         * @param code the code for the occurred error
         * @return the same ImageError object
         */
        public ImageError setErrorCode(int code) {
            this.errorCode = code;
            return this;
        }

        /**
         * @return the error code that was previously set
         * by {@link #setErrorCode(int)}
         */
        public int getErrorCode() {
            return errorCode;
        }
    }
}
