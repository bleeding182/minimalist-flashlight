/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 David Medenjak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package at.bleeding182.flashlight;

import java.io.IOException;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Service to access the camera flash and keep the flash running.
 *
 * @author David Medenjak 2014
 */
public class FlashlightService extends Service {
    /**
     * Action to toggle flash on/off.
     */
    public static final String FLASH_ON = BuildConfig.APPLICATION_ID + ".FLASH_ON";
    public static final String FLASH_OFF = BuildConfig.APPLICATION_ID + ".FLASH_OFF";

    /**
     * Camera instance to access the flash.
     */
    @SuppressWarnings("deprecation")
    private Camera mCamera;
    /**
     * Wakelock to keep flashlight running with screen off.
     */
    private PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "onCreate");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "onStartCommand " + (intent != null ? intent.getAction() : "none"));
        }
        if (intent != null) {
            final String action = intent.getAction();
            if (FLASH_ON.equals(action)) {
                try {
                    startCamera();
                    updateWidgets(this, true);
                    return START_STICKY;
                } catch (Exception ex) {
                    Toast.makeText(this, R.string.err_available, Toast.LENGTH_SHORT).show();
                    if (BuildConfig.DEBUG) {
                        Log.v("FlashlightService", "exception " + ex.getMessage());
                    }
                }
            }
        }
        updateWidgets(this, false);
        stopSelf();
        return START_NOT_STICKY;
    }


    /**
     * Sets up the Widget Layout.
     *
     * @param packageName   the name of the application package.
     * @param flashState    the state of the flash.
     * @param pendingIntent the intent to execute on click
     * @return the initialized view.
     */

    static RemoteViews getRemoteViews(String packageName, boolean flashState, PendingIntent pendingIntent) {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "getRemoteViews");
        }
        final RemoteViews remoteViews = new RemoteViews(packageName, R.layout.widget_layout);
        final int imageResource = flashState ? R.drawable.standby_on : R.drawable.standby_off;
        remoteViews.setImageViewResource(R.id.update, imageResource);
        remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
        return remoteViews;
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "onDestroy");
        }
        stopCamera();
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }
        super.onDestroy();
    }

    /**
     * Stops the camera and sets the instance to null.
     */
    private void stopCamera() {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "stopCamera");
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
        stopSelf();
    }

    public static void updateWidgets(Context context, boolean flashOn) {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "updateWidgets");
        }
        final Intent intent = new Intent(context, FlashlightService.class);
        intent.setAction(flashOn ? FLASH_OFF : FLASH_ON);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final RemoteViews views = getRemoteViews(context.getPackageName(), flashOn, pendingIntent);

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, FlashlightProvider.class), views);
    }

    @SuppressWarnings("deprecation")
    private void startCamera() throws IOException {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "startCamera");
        }
        mCamera = Camera.open();
        final Parameters parameters = mCamera.getParameters();
        configFlashParameters(parameters);

        // will work on some devices
        mCamera.setParameters(parameters);
        // Needed for some devices.
        mCamera.setPreviewTexture(new SurfaceTexture(0));
        // Needed for some more devices.
        mCamera.startPreview();

        // Keep phone awake with the screen off
        if (mWakeLock == null) {
            mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID);
        }
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
    }

    @SuppressWarnings("deprecation")
    private void configFlashParameters(Parameters p) {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "configFlashParameters");
        }
        final List<String> flashes = p.getSupportedFlashModes();
        if (flashes == null) {
            throw new IllegalStateException("No flash available");
        }
        if (flashes.contains(Parameters.FLASH_MODE_TORCH)) {
            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        } else if (flashes.contains(Parameters.FLASH_MODE_ON)) {
            p.setFlashMode(Parameters.FLASH_MODE_ON);
        } else {
            throw new IllegalStateException("No useable flash mode");
        }
    }

}
