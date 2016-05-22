/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 David Medenjak
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

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * @author David Medenjak on 5/22/2016.
 */
@SuppressWarnings("deprecation")
public class CompatFlashlight implements Flashlight {

    /**
     * Camera instance to access the flash.
     */
    private Camera mCamera;

    public CompatFlashlight(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void turnFlashOn() throws IOException {
        final Camera.Parameters parameters = mCamera.getParameters();
        configFlashParameters(parameters);

        // will work on some devices
        mCamera.setParameters(parameters);
        // Needed for some devices.
        mCamera.setPreviewTexture(new SurfaceTexture(0));
        // Needed for some more devices.
        mCamera.startPreview();
    }

    @Override
    public void turnFlashOff() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }


    private void configFlashParameters(Camera.Parameters p) {
        if (BuildConfig.DEBUG) {
            Log.v("FlashlightService", "configFlashParameters");
        }
        final List<String> flashes = p.getSupportedFlashModes();
        if (flashes == null) {
            throw new IllegalStateException("No flash available");
        }
        if (flashes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else if (flashes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            throw new IllegalStateException("No useable flash mode");
        }
    }
}
