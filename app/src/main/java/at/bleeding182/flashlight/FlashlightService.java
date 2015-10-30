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

/**
 * Service to access the camera flash and keep the flash running.
 * 
 * @author David Medenjak 2014
 * 
 */
public class FlashlightService extends Service {

	/**
	 * Camera instance to access the flash.
	 */
	private Camera cam;
	/**
	 * Wakelock to keep flashlight running with screen off.
	 */
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
			// Needed for some devices.
			cam.setPreviewTexture(new SurfaceTexture(0));
			// Needed for some more devices.
			cam.startPreview();

			// Keep phone awake with screen off
			wl = ((PowerManager) getSystemService(Context.POWER_SERVICE))
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
							"FlashlightService");
			if (wl != null && !wl.isHeld())
				wl.acquire();
			return START_NOT_STICKY;
		} catch (Exception e) {
			return error(this, R.string.err_access);
		}
	}

	/**
	 * Toasting an error message.
	 * 
	 * @param context
	 *            the application context.
	 * @param messageRessource
	 *            the id of the message to display.
	 * @return
	 */
	private static int error(Context context, int messageRessource) {
		Toast.makeText(context, context.getString(messageRessource),
				Toast.LENGTH_SHORT).show();
		context.sendBroadcast(new Intent(context, FlashlightProvider.class)
				.setAction(FlashlightProvider.TOGGLE_ACTION));
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v("FlashlightService", "Flash Service destroyed");
		stopCamera();
		if (wl != null) {
			if (wl.isHeld())
				wl.release();
			wl = null;
		}
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
