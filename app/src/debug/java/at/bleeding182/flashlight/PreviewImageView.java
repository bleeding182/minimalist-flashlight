package at.bleeding182.flashlight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author David Medenjak on 6/27/2016.
 */
public class PreviewImageView extends ImageView {

  static int s = 0;

  public PreviewImageView(Context context) {
    super(context);
    init();
  }

  private void init() {
    OldIconDrawable drawable = new OldIconDrawable();
    setImageDrawable(drawable);
  }

  public PreviewImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    OldIconDrawable drawable = new OldIconDrawable();
    setImageDrawable(drawable);
    drawable.setFlashOn(getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT);
  }
}
