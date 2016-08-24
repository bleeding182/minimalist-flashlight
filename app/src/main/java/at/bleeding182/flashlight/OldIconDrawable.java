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

public class OldIconDrawable extends Drawable {

    Paint mPaint = new Paint();
    private float mRadius;
    private float mShadowRadius;
    private RadialGradient mLightShadow;
    private RadialGradient mShadow;
    private int mSize;
    private boolean mFlashOn;

    private Path mPath = new Path();
    private int backgroundDisabled = Color.BLACK;
    private int backgroundEnabled = Color.BLACK;
    private int disabled = 0xffff4343;
    private int enabled = 0xff33b5e5;

    public OldIconDrawable() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateSize(Math.min(bottom - top, right - left));
    }

    public void updateSize(int size) {
        mSize = size > 0 ? size : 100;
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

    public void createPath(int bound) {
        mPath.rewind();
        mPath.moveTo(12f, 1f);
        mPath.lineTo(12f, 10f);

        mPath.addArc(new RectF(3, 3, 21, 21), -60, 300);

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
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mFlashOn ? mLightShadow : mShadow);
        canvas.drawCircle(mSize / 2, mSize / 2, mShadowRadius, mPaint);
        mPaint.setShader(null);
        mPaint.setColor(mFlashOn ? backgroundEnabled : backgroundDisabled);
        canvas.drawCircle(mSize / 2, mSize / 2, mRadius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRadius / 7f);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setColor(mFlashOn ? enabled : disabled);
        canvas.drawPath(mPath, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRadius / 24f);
        mPaint.setColor(0xdddddddd);
        canvas.drawCircle(mSize / 2f, mSize / 2f, mRadius, mPaint);
        mPaint.setStrokeWidth(mRadius / 16f);
        mPaint.setColor(0xeeeeeeee);
        canvas.drawCircle(mSize / 2f, mSize / 2f, mRadius - mRadius / 24f, mPaint);
        mPaint.setStrokeWidth(mRadius / 48f);
        mPaint.setColor(0xaaaaaaaa);
        canvas.drawCircle(mSize / 2f, mSize / 2f, mRadius - mRadius / 24f - mRadius / 32f, mPaint);
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

    public void setColors(int backgroundDisabled, int backgroundEnabled, int disabled, int enabled) {
        this.backgroundDisabled = backgroundDisabled;
        this.backgroundEnabled = backgroundEnabled;
        this.disabled = disabled;
        this.enabled = enabled;
    }
}