package at.bleeding182.flashlight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author David Medenjak on 6/27/2016.
 */
public class PreviewImageView extends ImageView {
    public PreviewImageView(Context context) {
        super(context);
        setImageDrawable(new IconDrawable(200));
    }

    public PreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageDrawable(new IconDrawable(200));
    }

    public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(new IconDrawable(200));
    }
}
