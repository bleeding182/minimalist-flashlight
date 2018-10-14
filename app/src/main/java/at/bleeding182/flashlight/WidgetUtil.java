package at.bleeding182.flashlight;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.RemoteViews;

public final class WidgetUtil {

  private Bitmap bitmap;
  private Canvas canvas;
  private OldIconDrawable drawable;
  private String packageName;

  public WidgetUtil(Context context) {
    int size = context.getResources().getDimensionPixelSize(R.dimen.size);

    packageName = context.getPackageName();
    bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    canvas = new Canvas(bitmap);
    drawable = new OldIconDrawable();
    drawable.setBounds(0, 0, size, size);
  }

  /**
   * Sets up the Widget Layout.
   *
   * @param flashState the state of the flash.
   * @param pendingIntent the intent to execute on click
   * @return the initialized view.
   */
  public RemoteViews getRemoteViews(boolean flashState, PendingIntent pendingIntent) {
    final RemoteViews remoteViews = new RemoteViews(packageName, R.layout.widget_layout);

    remoteViews.setImageViewBitmap(R.id.update, getBitmap(flashState));
    remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
    return remoteViews;
  }

  private Bitmap getBitmap(boolean flashState) {
    bitmap.eraseColor(Color.TRANSPARENT);
    drawable.setFlashOn(flashState);
    drawable.draw(canvas);
    return bitmap;
  }
}
