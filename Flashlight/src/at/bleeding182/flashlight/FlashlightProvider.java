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

public class FlashlightProvider extends BroadcastReceiver {
	/**
	 * Action to toggle camera on/off for the intent.
	 */
	public static final String TOGGLE_ACTION = "at.bleeding182.flashlight.TOGGLE";
	private static final String FLASH_STATE = "flashState";

	private static boolean getFlashState(Context context) {
		return getFlashState(context.getSharedPreferences(
				FlashlightProvider.class.getName(), 0));
	}

	private static boolean getFlashState(SharedPreferences prefs) {
		return prefs.getBoolean(FLASH_STATE, false);
	}

	private static void setFlashState(SharedPreferences settings, boolean state) {
		settings.edit().putBoolean(FLASH_STATE, state).apply();
	}

	private static RemoteViews getRemoteViews(String packageName, int layout,
			boolean flashState) {
		RemoteViews remoteViews = new RemoteViews(packageName,
				R.layout.widget_layout);
		remoteViews.setImageViewResource(R.id.update,
				flashState ? R.drawable.standby_on : R.drawable.standby_off);
		return remoteViews;
	}

	public static void update(Context context, AppWidgetManager appWidgetManager) {
		update(context, appWidgetManager, getFlashState(context));
	}

	public static void update(Context context,
			AppWidgetManager appWidgetManager, boolean flashOn) {
		Log.d("FlashlightProvider", "Updating");
		Intent intent = new Intent(context, FlashlightProvider.class);
		intent.setAction(FlashlightProvider.TOGGLE_ACTION);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);

		RemoteViews remoteViews = getRemoteViews(context.getPackageName(),
				R.layout.widget_layout, flashOn);
		remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
		appWidgetManager.updateAppWidget(new ComponentName(context,
				FlashlightProvider.class), remoteViews);
	}

	public void onReceive(Context context, Intent intent) {
		Log.v(getClass().getSimpleName(), "Action: " + intent.getAction());
		context = context.getApplicationContext();
		String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
			update(context, AppWidgetManager.getInstance(context));
			return;
		} else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
			onDisabled(context);
			return;
		} else if (!TOGGLE_ACTION.equals(action)) {
			// Ignore enabled, deleted, options_changed
			return;
		}
		SharedPreferences settings = context.getSharedPreferences(getClass()
				.getName(), 0);
		boolean flashOn = getFlashState(settings);
		Intent service = new Intent(context, FlashlightService.class);
		if (flashOn)
			context.stopService(service);
		else
			context.startService(service);

		flashOn = !flashOn;
		// Persist state
		setFlashState(settings, flashOn);
		update(context, AppWidgetManager.getInstance(context), flashOn);
	}

	public static void onDisabled(Context context) {
		Log.d("FlashlightProvider", "Disabled");
		setFlashState(context.getSharedPreferences(
				FlashlightService.class.getName(), 0), false);
		context.stopService(new Intent(context, FlashlightService.class));
	}
}
