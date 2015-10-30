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
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Writing / reading SharedPreferences to toggle between on/off state and
 * updating the widgets.
 *
 * @author David Medenjak 2014
 */
public class FlashlightProvider extends BroadcastReceiver {
    /**
     * Action to toggle flash on/off.
     */
    public static final String TOGGLE_ACTION = "at.bleeding182.flashlight.TOGGLE";
    /**
     * Preference name for current flash state.
     */
    private static final String FLASH_STATE = "flashState";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(getClass().getSimpleName(), "Action: " + intent.getAction());

        context = context.getApplicationContext();
        String action = intent.getAction();

        SharedPreferences settings = context.getSharedPreferences(getClass()
                .getName(), 0);

        switch (action) {
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                update(context, getFlashState(settings));
                break;
            case AppWidgetManager.ACTION_APPWIDGET_DISABLED:
                onDisabled(context, settings);
                break;
            case TOGGLE_ACTION:
                boolean flashOn = !getFlashState(settings);
                Intent service = new Intent(context, FlashlightService.class);
                if (flashOn)
                    context.startService(service);
                else
                    context.stopService(service);

                // Persist state
                setFlashState(settings, flashOn);
                update(context, flashOn);
                break;
        }

        // Ignore enabled, deleted, options_changed
    }

    /**
     * Getter for the current flash state.
     *
     * @param prefs the preferences to read.
     * @return the current state of the flash.
     */
    private static boolean getFlashState(SharedPreferences prefs) {
        return prefs.getBoolean(FLASH_STATE, false);
    }

    /**
     * Setter for the current flash state.
     *
     * @param settings the preferences to write.
     * @param state    the current state of the flash.
     */
    private static void setFlashState(SharedPreferences settings, boolean state) {
        settings.edit().putBoolean(FLASH_STATE, state).apply();
    }

    /**
     * Sets up the Widget Layout.
     *
     * @param packageName the name of the application package.
     * @param layout      layout id of the widget.
     * @param flashState  the state of the flash.
     * @return the initialized view.
     */
    private static RemoteViews getRemoteViews(String packageName, int layout,
                                              boolean flashState) {
        RemoteViews remoteViews = new RemoteViews(packageName,
                R.layout.widget_layout);
        remoteViews.setImageViewResource(R.id.update,
                flashState ? R.drawable.standby_on : R.drawable.standby_off);
        return remoteViews;
    }

    /**
     * Updates the buttons.
     *
     * @param context the application context.
     * @param flashOn the state of the flash.
     */
    public static void update(Context context, boolean flashOn) {
        Log.d("FlashlightProvider", "Updating");
        Intent intent = new Intent(context, FlashlightProvider.class);
        intent.setAction(FlashlightProvider.TOGGLE_ACTION);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = getRemoteViews(context.getPackageName(),
                R.layout.widget_layout, flashOn);
        remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, FlashlightProvider.class),
                remoteViews);
    }

    /**
     * What to do when the last instance of the widget is removed. It stops the
     * service and sets the status in the preferences to off.
     *
     * @param context  the application context.
     * @param settings the preferences to update.
     */
    public static void onDisabled(Context context, SharedPreferences settings) {
        Log.d("FlashlightProvider", "Disabled");
        setFlashState(settings, false);
        context.stopService(new Intent(context, FlashlightService.class));
    }
}
