package at.bleeding182.flashlight.wizard;

import android.view.View;

public class ColorClickListener implements View.OnClickListener {

    private final String name;
    private final WizardActivity activity;
    private int position;

    public ColorClickListener(int position, String name, WizardActivity activity) {
        this.position = position;
        this.name = name;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.onColorClicked(position, name);
    }
}
