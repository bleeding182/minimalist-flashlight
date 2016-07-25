package at.bleeding182.flashlight.wizard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import at.bleeding182.flashlight.IconDrawable;
import at.bleeding182.flashlight.OldIconDrawable;

/**
 * @author David Medenjak on 6/27/2016.
 */
public class PreviewImageView extends ImageView {

    private IconDrawable drawable;
    private boolean isFlashOn;

    public PreviewImageView(Context context) {
        super(context);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        OldIconDrawable drawable = new OldIconDrawable();
        setImageDrawable(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isInEditMode()) {
            drawable = new OldIconDrawable();
            drawable.setFlashOn(isFlashOn);
            setImageDrawable((Drawable) drawable);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ((Drawable) drawable).setBounds(left, top, right, bottom);
    }

    public void setDrawable(IconDrawable drawable) {
        this.drawable = drawable;
        setImageDrawable((Drawable) drawable);
    }

    public void setFlashOn(boolean isOn) {
        isFlashOn = isOn;
        if (drawable != null) {
            drawable.setFlashOn(isOn);
        }
    }

    public void setColors(int backgroundDisabled, int backgroundEnabled, int disabled, int enabled) {
        if (drawable != null) {
            drawable.setColors(backgroundDisabled, backgroundEnabled, disabled, enabled);
            invalidate();
        }
    }
}
