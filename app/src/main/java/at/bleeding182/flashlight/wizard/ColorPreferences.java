package at.bleeding182.flashlight.wizard;

import android.content.SharedPreferences;

public class ColorPreferences {

    private static final String PREF_STYLE = "style";
    public static final int STYLE_OLD = 0;
    public static final int STYLE_FLASHLIGHT = 1;

    private SharedPreferences preferences;

    public ColorPreferences(SharedPreferences preferences) {

        this.preferences = preferences;
    }

    public int getStyle() {
        return preferences.getInt(PREF_STYLE, STYLE_OLD);
    }

    public void setStyle(int style) {
        preferences.edit().putInt(PREF_STYLE, style).apply();
    }

    public void setColor(String name, int color) {
        preferences.edit().putInt(name, color).apply();
    }

    public int getColor(String name) {
        return preferences.getInt(name, 0xffffffff);
    }
}
