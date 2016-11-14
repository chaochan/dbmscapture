package chaochan.dbmscapture.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by  on 2016/11/10.
 */

public class ImageAnalyzer {

    /**
     * 解析済みデータ
     */
    public static class AnalyzedImage {
        public Bitmap nameArea;
        public Bitmap sireLineArea;
    }


    /**
     * 解析済みデータ
     */
    public static class AnalyzedAbility {
        public Bitmap nameArea;
        public Bitmap sireLineArea;
    }


    /**
     * コンストラクタ
     */
    private ImageAnalyzer() {
    }


    /**
     * スクリーンショットを解析する
     * @param uri
     */
    public static AnalyzedImage analyzeScreenShot(Context context, Uri uri) {
        try {
            Bitmap screenShot = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return analyzeScreenShot(screenShot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * スクリーンショットを解析する
     * @param screenShot
     */
    public static AnalyzedImage analyzeScreenShot(Bitmap screenShot) {
        final Rect nameRect = new Rect(50, 276, 900, 350);
        final Rect sireLineRect = new Rect(78, 668, 860, 1382);

        AnalyzedImage image = new AnalyzedImage();
        image.nameArea = clipNameArea(screenShot, nameRect);
        image.sireLineArea = clipSireLineArea(screenShot, sireLineRect);
        return image;
    }


    /**
     * スクリーンショットから名前欄を切り抜く
     * @param screenShot
     * @return
     */
    private static Bitmap clipNameArea(Bitmap screenShot, Rect nameRect) {
        return clipBitmap(screenShot, nameRect);
    }


    /**
     * スクリーンショットから血統欄を切り抜く
     * @param screenShot
     * @return
     */
    private static Bitmap clipSireLineArea(Bitmap screenShot, Rect sireLineRect) {
        final int marginX = 4;
        final int marginWidthBase = 40;
        final int[] marginWidthCount = new int[] { 1, 2, 3, 4, 4, 3, 4, 4, 2, 3, 4, 4, 3, 4, 4, };
        final int lineHeight = sireLineRect.height() / marginWidthCount.length;

        final Bitmap dstBitmap = Bitmap.createBitmap(sireLineRect.width(), sireLineRect.height(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(dstBitmap);

        final Paint paint = new Paint();
        for (int i = 0; i < marginWidthCount.length; i++) {
            final int left   = marginX + marginWidthBase * marginWidthCount[i];
            final int top    = lineHeight * i + 10;
            final int right  = 550;
            final int bottom = top + lineHeight;

            final Rect srcRect = new Rect();
            srcRect.left   = left   + sireLineRect.left;
            srcRect.top    = top    + sireLineRect.top;
            srcRect.right  = right  + sireLineRect.left;
            srcRect.bottom = srcRect.top + lineHeight - 10;

            final Rect dstRect = new Rect(0, top, srcRect.width(), top + srcRect.height());
            canvas.drawBitmap(screenShot, srcRect, dstRect, paint);
        }

        return dstBitmap;
    }


    /**
     * Bitmapを指定された矩形で切り抜き、新しいBitmapとして返す
     * @param srcBitmap
     * @param clipRect
     * @return
     */
    private static Bitmap clipBitmap(Bitmap srcBitmap, Rect clipRect) {
        final Bitmap dstBitmap = Bitmap.createBitmap(clipRect.width(), clipRect.height(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(dstBitmap);

        canvas.drawBitmap(srcBitmap, clipRect, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), new Paint());

        return dstBitmap;
    }
}
