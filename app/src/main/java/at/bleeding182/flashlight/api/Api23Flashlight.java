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

package at.bleeding182.flashlight.api;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.M)
public class Api23Flashlight implements Flashlight {

  private static final String TAG = "Api23Flashlight";
  private CameraManager mCameraManager;

  private boolean flashOn;

  public Api23Flashlight(CameraManager cameraManager) {
    Log.d(TAG, "constructor");
    mCameraManager = cameraManager;
  }

  @Override
  public void turnFlashOn() throws IOException {
    Log.d(TAG, "turnFlashOn");
    try {
      mCameraManager.setTorchMode(mCameraManager.getCameraIdList()[0], true);
      flashOn = true;
    } catch (CameraAccessException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void turnFlashOff() {
    Log.d(TAG, "turnFlashOff");
    if (!flashOn) return;
    try {
      String[] idList = mCameraManager.getCameraIdList();
      if (idList.length == 0) {
        flashOn = false;
        return;
      }

      mCameraManager.setTorchMode(idList[0], false);
      flashOn = false;
    } catch (CameraAccessException | RuntimeException e) {
    }
  }
}
