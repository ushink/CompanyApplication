package com.msystemlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

public class CircleImageView extends NetworkImageView
{
  public CircleImageView(Context context)
  {
    super(context);
  }

  public CircleImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  protected void onDraw(Canvas canvas)
  {
    Drawable drawable = getDrawable();

    if (drawable == null) {
      return;
    }

    if ((getWidth() == 0) || (getHeight() == 0)) {
      return;
    }

    Bitmap b = (drawable instanceof StateListDrawable) ? 
      ((BitmapDrawable)drawable
      .getCurrent()).getBitmap() : ((BitmapDrawable)drawable)
      .getBitmap();
    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

    int w = getWidth();

    Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
    canvas.drawBitmap(roundBitmap, 0.0F, 0.0F, null);
  }

  public static Bitmap getCroppedBitmap(Bitmap bmp, int radius)
  {
    Bitmap sbmp;
    if ((bmp.getWidth() != radius) || (bmp.getHeight() != radius))
      sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
    else
      sbmp = bmp;
    Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(output);

    Paint paint = new Paint();
    Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(Color.parseColor("#ff000000"));
    canvas.drawCircle(sbmp.getWidth() / 2.0F, sbmp
      .getHeight() / 2.0F, sbmp.getWidth() / 2, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(sbmp, rect, rect, paint);

    return output;
  }
}