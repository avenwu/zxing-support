package avenwu.net.support.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by chaobin on 9/15/15.
 */
public class ViewFinderView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int POINT_SIZE = 6;

    private Bitmap resultBitmap;
    /**
     * 定于需要用到的几个颜色值
     */
    private final int maskColor = 0x60000000;
    private final int resultColor = 0xb0000000;
    private final int laserColor = 0xffcc0000;
    private final int resultPointColor = 0xc0ffbd21;
    private final Paint paint;
    /**
     * 扫描框的位置，这里会直接定义区域，实际最好通过camera做尺寸关联；
     */
    Rect mRect = new Rect();
    private int scannerAlpha;

    public ViewFinderView(Context context) {
        this(context, null);
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect frame = getFramingRect();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        //上边区域
        canvas.drawRect(0, 0, width, frame.top, paint);
        //左边区域
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        //右边区域
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        //下边区域
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        // Draw a red "laser scanner" line through the middle to show decoding is active
        paint.setColor(laserColor);
        paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = frame.height() / 2 + frame.top;
        canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
        Log.d("TestDemo", "rect:" + mRect);
        // Request another update at the animation interval, but only repaint the laser line,
        // not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY,
            frame.left - POINT_SIZE,
            frame.top - POINT_SIZE,
            frame.right + POINT_SIZE,
            frame.bottom + POINT_SIZE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 这里简单起见，直接计算中间位置作为扫描框
         */
        final int width = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
        final int left = (getMeasuredWidth() - width) / 2;
        final int right = left + width;
        final int top = (getMeasuredHeight() - width) / 2;
        final int bottom = top + width;
        mRect.set(left, top, right, bottom);
    }

    private Rect getFramingRect() {
        return mRect;
    }
}
