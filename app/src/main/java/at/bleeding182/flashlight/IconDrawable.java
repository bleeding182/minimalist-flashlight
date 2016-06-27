package at.bleeding182.flashlight;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * @author David Medenjak on 6/27/2016.
 */
public class IconDrawable extends Drawable {

    public static final int LIGHT_BACKGROUND = 0xff22252b;
    private final float mRadius;
    private final float mShadowRadius;
    private final RadialGradient mLightShadow;
    Paint mPaint = new Paint();
    private int mSize;
    private final RadialGradient mShadow;
    private boolean mFlashOn;

    public IconDrawable(int size) {
        mSize = size;
        mPaint.setAntiAlias(true);


        mRadius = mSize * (0.8f / 2f);
        mShadowRadius = mSize * (0.98f / 2);

        float startRatio = mRadius / mShadowRadius;
        float midRatio = startRatio + ((1f - startRatio) / 2f);

        mShadow = new RadialGradient(mSize / 2, mSize / 2, mShadowRadius,
                new int[]{0, 0x44000000, 0x14000000, 0},
                new float[]{0f, startRatio, midRatio, 1f},
                Shader.TileMode.CLAMP);
        mLightShadow = new RadialGradient(mSize / 2, mSize / 2, mShadowRadius,
                new int[]{0, 0x6433b5e5, 0x1433b5e5, 0},
                new float[]{0f, startRatio, midRatio, 1f},
                Shader.TileMode.CLAMP);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mFlashOn ? mLightShadow : mShadow);
        canvas.drawCircle(mSize / 2, mSize / 2, mShadowRadius, mPaint);
        mPaint.setShader(null);
        mPaint.setColor(mFlashOn ? LIGHT_BACKGROUND : Color.BLACK);
        canvas.drawCircle(mSize / 2, mSize / 2, mRadius, mPaint);

        mPaint.setColor(mFlashOn ? 0xff33b5e5 : 0xff888888);
        canvas.drawRect(mSize / 2 - mRadius * 0.1f, mSize / 2 - mRadius * 0.8f,
                mSize / 2 + mRadius * 0.1f, mSize / 2 - mRadius * 0.1f, mPaint);

        canvas.clipRect(mSize / 2 - mRadius * 0.25f, mSize / 2 - mRadius * 0.8f,
                mSize / 2 + mRadius * 0.25f, mSize / 2 - mRadius * 0.1f, Region.Op.XOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mSize * 0.075f);
        canvas.drawCircle(mSize / 2, mSize / 2, mRadius * 0.6f, mPaint);
        canvas.clipRect(0, 0, mSize, mSize, Region.Op.UNION);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void setFlashOn(boolean flashOn) {
        mFlashOn = flashOn;
    }
}
