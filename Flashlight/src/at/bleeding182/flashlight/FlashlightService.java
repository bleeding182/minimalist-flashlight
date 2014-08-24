package at.bleeding182.flashlight;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class FlashlightService extends Service {

	/**
	 * Camera instance.
	 */
	private Camera cam;
	private PowerManager.WakeLock wl;

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
			List<String> flashes = p.getSupportedFlashModes();
			if (flashes == null)
				return error(this, R.string.err_available);
			if (flashes.contains(Parameters.FLASH_MODE_TORCH))
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			else if (flashes.contains(Parameters.FLASH_MODE_ON))
				p.setFlashMode(Parameters.FLASH_MODE_ON);
			else
				return error(this, R.string.err_available);
			cam.setParameters(p);
			// Not needed for all devices.
			cam.setPreviewTexture(new SurfaceTexture(0));
			cam.startPreview();
			wl = ((PowerManager) getSystemService(Context.POWER_SERVICE))
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
							"FlashlightService");
			wl.acquire();
			return START_NOT_STICKY;
		} catch (Exception e) {
			return error(this, R.string.err_access);
		}
	}

	private static int error(Context context, int toast) {
		Toast.makeText(context, context.getString(toast), Toast.LENGTH_SHORT)
				.show();
		context.sendBroadcast(new Intent(context, FlashlightProvider.class)
				.setAction(FlashlightProvider.TOGGLE_ACTION));
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v("FlashlightService", "Flash Service destroyed");
		stopCamera();
		wl.release();
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
