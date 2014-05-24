package at.bleeding182.flashlight;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;

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
		if (intent.getAction().equals(FlashlightProvider.START)) {
			startCamera();
		} else {
			stopCamera();
			stopSelf();
		}
		return START_NOT_STICKY;
	}

	private void startCamera() {
		if (cam != null)
			return;
		try {
			cam = Camera.open();
			Parameters p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			cam.setParameters(p);
			cam.startPreview(); // Not needed for all devices it seems.
		} catch (RuntimeException e) {
			stopCamera();
		}
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
