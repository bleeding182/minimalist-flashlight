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

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/** Writing / reading SharedPreferences to toggle between on/off state and updating the widgets. */
public class FlashlightProvider extends BroadcastReceiver {

  public static final String FLASH_ON = BuildConfig.APPLICATION_ID + ".FLASH_ON";
  public static final String FLASH_OFF = BuildConfig.APPLICATION_ID + ".FLASH_OFF";

  /**
   * What to do when the last instance of the widget is removed. It stops the service and sets the
   * status in the preferences to off.
   *
   * @param context the application context.
   */
  public static void onDisabled(final Context context, final Intent intent) {
    Log.d("FlashlightProvider", "onDisabled");
    context.stopService(intent);
    InstallReceiver.updateInstallerActivityState(context);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.v("FlashlightProvider", "Action: " + intent.getAction());

    final String action = intent.getAction();
    if (action == null) return;

    final Intent serviceIntent = new Intent(context, FlashlightService.class);
    switch (action) {
      case FLASH_ON:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(serviceIntent);
        } else {
          context.startService(serviceIntent);
        }
        break;
      case FLASH_OFF:
        context.stopService(serviceIntent);
        break;
      case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
        WidgetUtil util = new WidgetUtil(context);
        FlashlightService.updateWidgets(context, util, FlashlightService.isRunning);
        break;
      case AppWidgetManager.ACTION_APPWIDGET_ENABLED:
        InstallReceiver.updateInstallerActivityState(context);
        break;
      case AppWidgetManager.ACTION_APPWIDGET_DISABLED:
        onDisabled(context, serviceIntent);
        break;
    }

    // Ignore enabled, deleted, options_changed
  }
}
