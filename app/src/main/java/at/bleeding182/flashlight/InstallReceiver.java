package at.bleeding182.flashlight;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class InstallReceiver extends BroadcastReceiver {

  public static void updateInstallerActivityState(Context context) {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    ComponentName myProvider = new ComponentName(context, FlashlightProvider.class);

    int[] widgetIds = appWidgetManager.getAppWidgetIds(myProvider);
    final int widgetCount;
    if (widgetIds == null) {
      widgetCount = 0;
    } else {
      widgetCount = widgetIds.length;
    }

    ComponentName installerActivity = new ComponentName(context, InstallerActivity.class);
    int flags = PackageManager.DONT_KILL_APP;

    final int state;
    if (widgetCount > 0) {
      Log.d("Flashlight", "disabling activity");
      state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    } else {
      Log.d("Flashlight", "enabling activity");
      state = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }

    context.getPackageManager().setComponentEnabledSetting(installerActivity, state, flags);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("FlashlightInstaller", "updated");

    updateInstallerActivityState(context);
  }
}
