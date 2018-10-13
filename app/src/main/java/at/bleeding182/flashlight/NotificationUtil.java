package at.bleeding182.flashlight;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public final class NotificationUtil {

  public static final String CHANNEL_DEFAULT = "default";

  private NotificationUtil() {
    // restrict initialization
  }

  @TargetApi(Build.VERSION_CODES.O)
  public static Notification flashlightNotification(final Context context) {
    final NotificationManager manager = context.getSystemService(NotificationManager.class);

    if (manager == null) {
      throw new IllegalStateException();
    }

    createChannel(context, manager);

    Intent intent =
        new Intent(context, FlashlightProvider.class).setAction(FlashlightProvider.FLASH_OFF);
    PendingIntent offIntent =
        PendingIntent.getBroadcast(context, 2141, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    return new Notification.Builder(context)
        .setChannelId(CHANNEL_DEFAULT)
        .setSmallIcon(R.drawable.ic_flashlight)
        .addAction(
            new Notification.Action.Builder(
                    null, context.getString(R.string.notification_action_turn_off), offIntent)
                .build())
        .build();
  }

  @TargetApi(Build.VERSION_CODES.O)
  private static void createChannel(Context context, NotificationManager manager) {
    final String title = context.getString(R.string.app_name);
    final int importance = NotificationManager.IMPORTANCE_LOW;

    final NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT, title, importance);
    channel.enableVibration(false);
    channel.setShowBadge(true);
    channel.setSound(null, null);
    manager.createNotificationChannel(channel);
  }
}
