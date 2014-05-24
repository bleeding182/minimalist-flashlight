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

	private static Camera cam;
	private static boolean flashOn;

	private static volatile AsyncTask<Void, Void, Void> toggler;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		ComponentName thisWidget = new ComponentName(context,
				FlashlightProvider.class);

		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setImageViewResource(R.id.update,
					flashOn ? R.drawable.standby_on : R.drawable.standby_off);

			Intent intent = new Intent(context, FlashlightProvider.class);

			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			intent.putExtra("toggle", true);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (toggler != null && toggler.getStatus() != AsyncTask.Status.FINISHED) {
			return;
		}

		if (intent.getBooleanExtra("toggle", false)) {
			if (flashOn) {
				stopCamera();
			} else {
				flashOn = true;
				toggler = new StartWorker();
				toggler.execute();
			}
		}
		super.onReceive(context, intent);

	}

	private void startCamera() {
		flashOn = true;
		try {
			cam = Camera.open();
		} catch (RuntimeException e) {
			stopCamera();
			return;
		}
		Parameters p = cam.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		cam.setParameters(p);
		cam.startPreview();
	}

	private class StartWorker extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			startCamera();
			return null;
		}
	}

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
