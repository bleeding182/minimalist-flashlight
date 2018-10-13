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

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && appWidgetManager != null
        && appWidgetManager.isRequestPinAppWidgetSupported()) {
      appWidgetManager.requestPinAppWidget(myProvider, null, null);
      finish();
    }

    setContentView(R.layout.activity_install_info);
  }
}
