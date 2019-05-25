package at.bleeding182.flashlight;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;

public class InstallerActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    InstallerActivity context = this;

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    ComponentName myProvider = new ComponentName(context, FlashlightProvider.class);

    if (tryAddingWidget(appWidgetManager, myProvider)) {
      finish();
    }

    setContentView(R.layout.activity_install_info);
  }

  private boolean tryAddingWidget(AppWidgetManager appWidgetManager, ComponentName myProvider) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && appWidgetManager != null
        && appWidgetManager.isRequestPinAppWidgetSupported()) {
      try {
        return appWidgetManager.requestPinAppWidget(myProvider, null, null);
      } catch (IllegalStateException ex) {
        return false;
      }
    }
    return false;
  }
}
