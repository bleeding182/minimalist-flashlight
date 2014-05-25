package at.bleeding182.flashlight;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class FlashlightService extends Service {

	/**
	 * Camera instance.
	 */
	private Camera cam;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("FlashlightService", "Starting Flash");
		if (cam != null)
			return START_NOT_STICKY;
		try {
			cam = Camera.open();
			Parameters p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			cam.setParameters(p);
			cam.startPreview(); // Not needed for all devices it seems.
		} catch (RuntimeException e) {
			Toast.makeText(getApplicationContext(), "Could not access camera.",
					Toast.LENGTH_SHORT).show();

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplication(), 0, new Intent(getApplication(),
							FlashlightProvider.class)
							.setAction(FlashlightProvider.TOGGLE_ACTION),
					PendingIntent.FLAG_UPDATE_CURRENT);
			try {
				pendingIntent.send();
			} catch (CanceledException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v("FlashlightService", "Flash Service destroyed");
		stopCamera();
		super.onDestroy();
	}

	/**
	 * Stops the camera and sets the instance to null.
	 */
	private void stopCamera() {
		if (cam != null) {
			cam.stopPreview();
			cam.release();
		}
		cam = null;
	}

}
