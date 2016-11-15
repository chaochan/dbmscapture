package chaochan.dbmscapture.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

import chaochan.dbmscapture.model.database.Ancestor;
import chaochan.dbmscapture.model.database.Broodmare;
import chaochan.dbmscapture.model.database.Stallion;

/**
 * スクリーンショット解析
 */
public class SSAnalyzer {

    public static interface OnAnalyzedListener {
        public void onAnalyzed(Stallion stallion, Ancestor[] sireLine);
        public void onAnalyzed(Broodmare broodmare, Ancestor[] sireLine);
    }


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
    public static class Ability {
        public String ability1;
        public String ability2;
    }


    /**
     * コンストラクタ
     */
    private SSAnalyzer() {
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
        toAbilityList(screenShot, sireLineRect);
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
     * 指定されたスクリーンショットから因子の能力データを解析する
     * @param screenShot
     * @param sireLineRect
     * @return
     */
    private static Ability[] toAbilityList(Bitmap screenShot, Rect sireLineRect) {
        final int ability1X = 560 + sireLineRect.left;
        final int ability2X = 670 + sireLineRect.left;

        Ability[] list = new Ability[15];
        int lineWidth = sireLineRect.height() / list.length;
        int offsetY =  sireLineRect.top + (lineWidth / 2);
        for (int i = 0; i < list.length; i++) {
            Ability ability = new Ability();
            // 指定ピクセルの色から能力を取得する
            //android.util.Log.d("debug", String.format("x=%d, y=%d", ability1X, offsetY));
            ability.ability1 = colorToAbility(screenShot.getPixel(ability1X, offsetY));
            //android.util.Log.d("debug", String.format("x=%d, y=%d", ability1X, offsetY));
            ability.ability2 = colorToAbility(screenShot.getPixel(ability2X, offsetY));
            android.util.Log.d("debug", String.format("%s, %s", ability.ability1, ability.ability2));


            list[i] = ability;
            offsetY += lineWidth;
        }
        return list;
    }


    /**
     * 色 → 能力変換
     */
    private static String colorToAbility(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        float h = hsv[0];
        float s = hsv[1];
        if (h >= 50 && h <= 70) {
            if (s < 0.5) {
                // h=60.000, s=0.360, v=0.992
                return "速力";
            } else {
                // h=60.000, s=1.000, v=0.992
                return "短距離";
            }
        }
        if (h >= 20 && h <= 40) {
            return "底力";
        }
        if (h >= 170 && h <= 190) {
            return "長距離";
        }
        if (h >= 36 && h <= 40) {
            return "ダート";
        }
        if (h >= 322 && h <= 330) {
            return "丈夫";
        }
        if (h >= 116 && h <= 124) {
            return "早熟";
        }
        if (h >= 72 && h <= 80) {
            return "晩成";
        }
        if (h >= 176 && h <= 184) {
            return "堅実";
        }
        if (h >= 297 && h <= 303) {
            return "気性難";
        }

        return null;
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
