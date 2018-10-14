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

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import at.bleeding182.flashlight.api.Factory;
import at.bleeding182.flashlight.api.Flashlight;

/** Service to access the camera flash and keep the flash running. */
public class FlashlightService extends Service {

  private static final long TIMEOUT = TimeUnit.HOURS.toMillis(1);

  private static final String TAG = "FlashlightService";
  private static final String LOCK_TAG = BuildConfig.APPLICATION_ID + ":wakelog";

  static boolean isRunning = false;

  private WidgetUtil widgetUtil;

  /** Wakelock to keep flashlight running with screen off. */
  private PowerManager.WakeLock wakeLock;

  private Flashlight flashlight;

  public static void updateWidgets(Context context, WidgetUtil widgetUtil, boolean flashOn) {
    Log.v(TAG, "updateWidgets");

    final Intent intent = new Intent(context, FlashlightProvider.class);
    String action = flashOn ? FlashlightProvider.FLASH_OFF : FlashlightProvider.FLASH_ON;
    intent.setAction(action);
    intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

    final PendingIntent pendingIntent =
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    final RemoteViews views = widgetUtil.getRemoteViews(flashOn, pendingIntent);

    AppWidgetManager.getInstance(context)
        .updateAppWidget(new ComponentName(context, FlashlightProvider.class), views);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.v(TAG, "onCreate");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      startForeground(1, NotificationUtil.flashlightNotification(this));
    }

    widgetUtil = new WidgetUtil(this);
    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    if (powerManager == null) throw new IllegalStateException();

    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
    wakeLock.setReferenceCounted(false);
    wakeLock.acquire(TIMEOUT);

    try {
      flashlight = Factory.getFlashlight(this);
    } catch (RuntimeException e) {
      Toast.makeText(this, R.string.err_available, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.v(TAG, "onStartCommand " + (intent != null ? intent.getAction() : "none"));

    isRunning = true;

    try {
      startCamera();
      updateWidgets(this, widgetUtil, true);
      return START_STICKY;
    } catch (Exception ex) {
      Toast.makeText(this, R.string.err_available, Toast.LENGTH_SHORT).show();
      Log.v(TAG, "exception " + ex.getMessage());
    }

    updateWidgets(this, widgetUtil, false);
    stopSelf();
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.v(TAG, "onDestroy");
    isRunning = false;
    stopCamera();

    updateWidgets(this, widgetUtil, false);

    wakeLock.release();
  }

  /** Stops the camera and sets the instance to null. */
  private void stopCamera() {
    Log.v(TAG, "stopCamera");

    if (flashlight != null) {
      flashlight.turnFlashOff();
    }
    stopSelf();
  }

  private void startCamera() throws IOException {
    Log.v(TAG, "startCamera");
    flashlight.turnFlashOn();
  }
}
