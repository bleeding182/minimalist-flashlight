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

  private static final String CHANNEL_DEFAULT = "default";

  private NotificationUtil() {
    // restrict initialization
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public static Notification flashlightNotification(final Context context) {
    final NotificationManager manager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    if (manager == null) {
      throw new IllegalStateException();
    }

    final Notification.Action action = createTurnOffAction(context);

    Notification.Builder builder =
        new Notification.Builder(context)
            .setSmallIcon(R.drawable.ic_flashlight)
            .setContentTitle(context.getString(R.string.app_name))
            .addAction(action);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel(context, manager);
      builder.setChannelId(CHANNEL_DEFAULT);
    }

    return builder.build();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private static Notification.Action createTurnOffAction(Context context) {
    final Intent intent =
        new Intent(context, FlashlightProvider.class).setAction(FlashlightProvider.FLASH_OFF);
    final PendingIntent offIntent =
        PendingIntent.getBroadcast(context, 2141, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    final Notification.Action action;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      action =
          new Notification.Action.Builder(
                  null, context.getString(R.string.notification_action_turn_off), offIntent)
              .build();
    } else {
      action =
          new Notification.Action.Builder(
                  0, context.getString(R.string.notification_action_turn_off), offIntent)
              .build();
    }
    return action;
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
