package at.bleeding182.flashlight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class FlashlightProvider extends BroadcastReceiver {
	/**
	 * Action to toggle camera on/off for the intent.
	 */
	public static final String TOGGLE_ACTION = "at.bleeding182.flashlight.TOGGLE";
	private static final String FLASH_STATE = "flashState";

	public static void onUpdate(Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("FlashlightProvider", "Update");
		Intent intent = new Intent(context, FlashlightProvider.class);
		intent.setAction(FlashlightProvider.TOGGLE_ACTION);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		boolean flashOn = context.getSharedPreferences(
				FlashlightProvider.class.getName(), 0).getBoolean(FLASH_STATE,
				false);
		for (int widgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setImageViewResource(R.id.update,
					flashOn ? R.drawable.standby_on : R.drawable.standby_off);

			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	public void onReceive(Context context, Intent intent) {
		Log.v(getClass().getSimpleName(), intent.getAction());
		String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				int[] appWidgetIds = extras
						.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
				if (appWidgetIds != null && appWidgetIds.length > 0) {
					onUpdate(context, AppWidgetManager.getInstance(context),
							appWidgetIds);
					return;
				}
			}
		} else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
			onDisabled(context);
			return;
		} else if (!action.equals(TOGGLE_ACTION)) {
			// Ignore enabled, deleted, options_changed
			return;
		}
		SharedPreferences settings = context.getSharedPreferences(getClass()
				.getName(), 0);
		boolean flashOn = settings.getBoolean(FLASH_STATE, false);
		Log.d("FlashlightProvider", "Flash was  " + flashOn);
		// Ignore if it is already in the wanted state (e.g. spamming button)
		Intent service = new Intent(context, FlashlightService.class);
		if (flashOn)
			context.stopService(service);
		else
			context.startService(service);

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		settings.edit().putBoolean(FLASH_STATE, !flashOn).apply();
		for (int widgetId : appWidgetManager.getAppWidgetIds(new ComponentName(
				context, FlashlightProvider.class))) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setImageViewResource(R.id.update,
					flashOn ? R.drawable.standby_off : R.drawable.standby_on);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	public static void onDisabled(Context context) {
		Log.d("FlashlightProvider", "Disabled");
		context.getSharedPreferences(FlashlightService.class.getName(), 0)
				.edit().putBoolean(FLASH_STATE, false).apply();
		context.stopService(new Intent(context, FlashlightService.class));
	}

}
