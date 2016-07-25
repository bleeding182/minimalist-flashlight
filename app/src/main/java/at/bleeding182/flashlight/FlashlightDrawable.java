package at.bleeding182.flashlight;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * @author David Medenjak on 6/27/2016.
 */
public class FlashlightDrawable extends Drawable implements IconDrawable {

    private Paint mPaint = new Paint();
    private float mRadius;
    private float mShadowRadius;
    private RadialGradient mLightShadow;
    private RadialGradient mShadow;
    private int mSize;
    private boolean mFlashOn;

    private Path mPath = new Path();
    private int backgroundDisabled = Color.BLACK;
    private int backgroundEnabled = Color.BLACK;
    private int disabled = Color.BLUE;
    private int enabled = Color.RED;

    public FlashlightDrawable() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateSize(Math.min(bottom - top, right - left));
    }

    private void updateSize(int size) {
        mSize = size;
        createPath(size);

        mRadius = mSize * (0.8f / 2f);
        mShadowRadius = mSize * (0.95f / 2);

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

    private void createPath(int bound) {
        mPath.rewind();
        // M18,4
        mPath.moveTo(18f, 4f);
        // H6
        mPath.lineTo(6f, 4f);
        // V2
        mPath.lineTo(6f, 2f);
        // H18
        mPath.lineTo(18f, 2f);
        // V4
        mPath.close();

        // M9,10
        mPath.moveTo(9f, 10f);
        // L6,5
        mPath.lineTo(6f, 5f);
        // H18
        mPath.lineTo(18f, 5f);
        // L15,10
        mPath.lineTo(15f, 10f);
        // H9
        mPath.close();
        // M9,22
        mPath.moveTo(9f, 22f);
        // V11
        mPath.lineTo(9f, 11f);
        // H15
        mPath.lineTo(15f, 11f);
        // V22
        mPath.lineTo(15f, 22f);
        // H9
        mPath.close();
        // M12,13
        mPath.moveTo(12f, 13f);
        // A1,1 0,0 0,11 14
        mPath.arcTo(new RectF(11, 12, 13, 14), 0, 360);
        // A1,1 0,0 0,12 15
        // A1,1 0,0 0,13 14
        // A1,1 0,0 0,12 13
        // Z
        mPath.close();

        Matrix matrix = new Matrix();
        float size = bound * 0.6f;
        matrix.preScale(size / 24f, size / 24f);
        float offset = (bound - size) / 2;
        matrix.postTranslate(offset, offset);
        mPath.transform(matrix);
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setShader(mFlashOn ? mLightShadow : mShadow);
        canvas.drawCircle(mSize / 2, mSize / 2, mShadowRadius, mPaint);
        mPaint.setShader(null);
        mPaint.setColor(mFlashOn ? backgroundEnabled : backgroundDisabled);
        canvas.drawCircle(mSize / 2, mSize / 2, mRadius, mPaint);

        mPaint.setColor(mFlashOn ? enabled : disabled);
        canvas.drawPath(mPath, mPaint);
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

    @Override
    public void setFlashOn(boolean flashOn) {
        mFlashOn = flashOn;
    }

    @Override
    public void setColors(int backgroundDisabled, int backgroundEnabled, int disabled, int enabled) {
        this.backgroundDisabled = backgroundDisabled;
        this.backgroundEnabled = backgroundEnabled;
        this.disabled = disabled;
        this.enabled = enabled;
    }
}
