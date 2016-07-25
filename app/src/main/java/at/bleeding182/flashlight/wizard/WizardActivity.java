package at.bleeding182.flashlight.wizard;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import at.bleeding182.flashlight.FlashlightDrawable;
import at.bleeding182.flashlight.IconDrawable;
import at.bleeding182.flashlight.OldIconDrawable;
import at.bleeding182.flashlight.R;

public class WizardActivity extends Activity implements ColorDialogFragment.ColorCallback {

    public static final String BACKGROUND_DISABLED = "bg_disabled";
    public static final String BACKGROUND_ENABLED = "bg_enabled";
    public static final String COLOR_DISABLED = "color_disabled";
    public static final String COLOR_ENABLED = "color_enabled";
    public static final String[] NAME = new String[]{
            BACKGROUND_DISABLED,
            BACKGROUND_ENABLED,
            COLOR_DISABLED,
            COLOR_ENABLED,
    };
    private static final int[] IDS = new int[]{
            R.id.background_disabled,
            R.id.background_enabled,
            R.id.color_disabled,
            R.id.color_enabled
    };
    private static final String COLOR_FRAGMENT = "color_fragment";

    private TextView[] colorSettings = new TextView[4];
    private ColorPreferences preferences;
    private PreviewImageView previewOn;
    private PreviewImageView previewOff;
    private RadioGroup iconGroup;
    private int mAppWidgetId;

    private static IconDrawable getIconDrawable(int style) {
        if (style == ColorPreferences.STYLE_OLD) {
            return new OldIconDrawable();
        } else {
            return new FlashlightDrawable();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_wizard);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        previewOn = (PreviewImageView) findViewById(R.id.preview_on);
        previewOff = (PreviewImageView) findViewById(R.id.preview_off);

        iconGroup = (RadioGroup) findViewById(R.id.icon_style);
        findViewById(R.id.action_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });


        preferences = new ColorPreferences(PreferenceManager.getDefaultSharedPreferences(this));

        for (int i = 0; i < colorSettings.length; i++) {
            colorSettings[i] = (TextView) findViewById(IDS[i]);
            colorSettings[i].setOnClickListener(new ColorClickListener(i, NAME[i], this));

            ColorDrawable colorDrawable = new ColorDrawable();
            int previewColorSize = getResources().getDimensionPixelSize(R.dimen.color_preview_size);
            colorDrawable.setBounds(0, 0, previewColorSize, previewColorSize);
            colorDrawable.setColor(preferences.getColor(NAME[i]));
            colorSettings[i].setCompoundDrawables(null, null, colorDrawable, null);
        }

        iconGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int style;
                if (checkedId == R.id.icon_flashlight) {
                    style = ColorPreferences.STYLE_FLASHLIGHT;
                } else {
                    style = ColorPreferences.STYLE_OLD;
                }
                preferences.setStyle(style);
                updateDrawables(style);
            }
        });
        int style = preferences.getStyle();
        iconGroup.check(style == ColorPreferences.STYLE_OLD ? R.id.icon_old : R.id.icon_flashlight);

        updateDrawables(style);
    }

    private void updateDrawables(int style) {
        previewOn.setDrawable(getIconDrawable(style));
        previewOff.setDrawable(getIconDrawable(style));
        previewOn.setFlashOn(true);
        updateDrawableColors();
    }

    public void onColorClicked(int position, String name) {
        ColorDialogFragment.newInstance(position, name, preferences.getColor(name))
                .show(getFragmentManager(), COLOR_FRAGMENT);
    }

    @Override
    public void onColorSelected(int position, String name, int color) {
        preferences.setColor(name, color);
        TextView setting = colorSettings[position];
        ((ColorDrawable) setting.getCompoundDrawables()[2]).setColor(color);
        setting.invalidate();

        updateDrawableColors();
    }

    private void updateDrawableColors() {
        previewOn.setColors(preferences.getColor(NAME[0]), preferences.getColor(NAME[1]), preferences.getColor(NAME[2]), preferences.getColor(NAME[3]));
        previewOff.setColors(preferences.getColor(NAME[0]), preferences.getColor(NAME[1]), preferences.getColor(NAME[2]), preferences.getColor(NAME[3]));
    }
}
