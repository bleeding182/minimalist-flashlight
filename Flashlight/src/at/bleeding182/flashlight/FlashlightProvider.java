package at.bleeding182.flashlight;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class FlashlightProvider extends AppWidgetProvider {
	/**
	 * Action to toggle camera on/off for the intent.
	 */
	private static final String TOGGLE_ACTION = "at.bleeding182.flashlight.TOGGLE";
	private static final String FLASH_STATE = "flashState";

	public static final String START = "start";
	public static final String STOP = "stop";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d("FlashlightProvider", "Update");
		Intent intent = new Intent(context, FlashlightProvider.class);
		intent.setAction(FlashlightProvider.TOGGLE_ACTION);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		boolean flashOn = context.getSharedPreferences(getClass().getName(), 0)
				.getBoolean(FLASH_STATE, false);
		for (int widgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setImageViewResource(R.id.update,
					flashOn ? R.drawable.standby_on : R.drawable.standby_off);

			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(TOGGLE_ACTION)) {
			super.onReceive(context, intent);
			return;
		}

		SharedPreferences settings = context.getSharedPreferences(getClass()
				.getName(), 0);
		boolean flashOn = settings.getBoolean(FLASH_STATE, false);
		Log.d("FlashlightProvider", "Toggle !" + flashOn);
		if (flashOn) {
			if (!isMyServiceRunning(context))
				return;
			context.startService(new Intent(context, FlashlightService.class)
					.setAction(STOP));
		} else {
			if (isMyServiceRunning(context))
				return;
			context.startService(new Intent(context, FlashlightService.class)
					.setAction(START));
		}
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

	private boolean isMyServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (FlashlightService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDisabled(Context context) {
		Log.d("FlashlightProvider", "Disabled");
		context.stopService(new Intent(context, FlashlightService.class));
	}

}
