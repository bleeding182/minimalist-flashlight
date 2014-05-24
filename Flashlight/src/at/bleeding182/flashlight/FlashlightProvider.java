package at.bleeding182.flashlight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class FlashlightProvider extends AppWidgetProvider {

	/**
	 * Camera instance.
	 */
	private static Camera cam;
	/**
	 * Current state of flash / camera.
	 */
	private static boolean flashOn;

	/**
	 * Action to toggle camera on/off for the intent.
	 */
	private static final String TOGGLE_ACTION = "at.bleeding182.flashlight.TOGGLE";

	/**
	 * Task used for turning on the camera.
	 */
	private static AsyncTask<Void, Void, Void> toggler;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent intent = new Intent(context, FlashlightProvider.class);
		intent.setAction(FlashlightProvider.TOGGLE_ACTION);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
		// Not mine
		if (!intent.getAction().equals(TOGGLE_ACTION)) {
			super.onReceive(context, intent);
			return;
		}
		if (toggler != null && toggler.getStatus() != AsyncTask.Status.FINISHED) {
			return;
		}
		if (flashOn) {
			stopCamera();
		} else {
			flashOn = true;
			toggler = new StartWorker();
			toggler.execute();
		}
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		for (int widgetId : appWidgetManager.getAppWidgetIds(new ComponentName(
				context, FlashlightProvider.class))) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setImageViewResource(R.id.update,
					flashOn ? R.drawable.standby_on : R.drawable.standby_off);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	private class StartWorker extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				cam = Camera.open();
			} catch (RuntimeException e) {
				stopCamera();
				return null;
			}
			Parameters p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			cam.setParameters(p);
			cam.startPreview(); // Not needed for all devices it seems.
			return null;
		}
	}

	/**
	 * Stops the camera and sets the instance to null.
	 */
	private void stopCamera() {
		flashOn = false;
		if (cam != null) {
			cam.stopPreview();
			cam.release();
		}
		cam = null;
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		stopCamera();
	}

}
