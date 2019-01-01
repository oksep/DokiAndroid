package com.dokiwa.dokidoki.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    /**
     * 采用原图尺寸
     */
    public static Bitmap getBitmapNative(Context context, String fullPath) {
        return getBitmapNative(context, fullPath, 0, 0, 0);
    }

    public static Bitmap getBitmapNative(Context context, int resId) {
        return getBitmapNative(context, resId, null, null, null, false, 0, 0, 0);
    }

    /**
     * 从res 获取资源，指定最大尺寸
     */
    public static Bitmap getBitmapNative(Context context, int resId, int maxWidth, int maxHeigh) {
        return getBitmapNative(context, resId, null, null, null, false, 0, maxWidth, maxHeigh);
    }

    /***
     * 从res 获取资源，最大不超过屏幕尺寸
     *
     * @param resId
     * @return
     */
    public static Bitmap getBitmapNativeDisplayMetrics(Context context, int resId) {
        DisplayMetrics defaultMetrics = getDefaultDisplayMetrics(context);
        return getBitmapNative(context, resId, null, null, null, false, 0,
                defaultMetrics.widthPixels, defaultMetrics.heightPixels);
    }

    /**
     * 采用原图尺寸
     */
    public static Bitmap getBitmapNative(Context context, String fullPath, float densityScale) {
        return getBitmapNative(context, fullPath, 0, 0, densityScale);
    }

    /***
     *
     * @param fullPath
     *            file path
     * @param width
     *            bitmap width
     * @param height
     *            bitmap height
     * @return
     */
    public static Bitmap getBitmapNative(Context context, String fullPath, int width, int height) {
        return getBitmapNative(context, fullPath, width, height, 0);
    }

    /***
     *
     * @param fullPath
     *            file fullPath
     * @param width
     *            bitmap width
     * @param height
     *            bitmap height
     * @return 指定尺寸头像
     */
    public static Bitmap getBitmapNative(Context context, String fullPath, int width, int height,
                                         float densityScale) {
        return getBitmapNative(context, 0, fullPath, null, null,
                false, densityScale, width, height);
    }

    /***
     *
     * @param uri
     *            file uri
     * @param width
     *            bitmap width
     * @param height
     *            bitmap height
     *
     * @return 指定尺寸头像
     */
    public static Bitmap getBitmapNative(Context context, Uri uri, int width,
                                         int height, float densityScale) {
        return getBitmapNative(context, 0, null, null, uri, false, densityScale, width, height);
    }

    /***
     *
     * @param uri
     *            file uri
     *
     * @return 指定尺寸头像
     */
    public static Bitmap getBitmapNative(Context context, Uri uri, float densityScale) {
        return getBitmapNative(context, 0, null, null, uri, false, densityScale, 0, 0);
    }

    /***
     *
     * @param uri
     * @param width
     * @param height
     * @return 指定尺寸图像
     */
    public static Bitmap getBitmapNative(Context context, Uri uri, int width, int height) {
        return getBitmapNative(context, uri, width, height, 0);
    }

    /***
     *
     * @param uri
     *            图片uri
     * @return 原图尺寸Bitmap
     */
    public static Bitmap getBitmapNative(Context context, Uri uri) {
        return getBitmapNative(context, uri, 0, 0);
    }

    public static Bitmap extractThumbNailAssets(Context context, String assetPath, int width,
                                                int heigh) throws
            IOException {
        return getBitmapNative(context, 0, assetPath, null, null, true, 0, width, heigh);
    }

    /**
     * decode bitmap
     */
    private static Bitmap getBitmapNative(Context context, int resourceId, String fullPath, byte[] data, Uri uri,
                                          boolean isAssets, float densityScale, int width, int height) {
        Bitmap bm = null;
        int inSampleSize = 0;
        boolean decodeAll = width == 0 && height == 0;
        if (0 > width || 0 > height) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (densityScale != 0) {
                options.inDensity = (int) (densityScale * DisplayMetrics.DENSITY_DEFAULT);
            }
            if (!decodeAll) {
                options.inJustDecodeBounds = true;
                decodeBitmap(context, options, data, fullPath, uri, isAssets, resourceId);
                int inWidth = options.outWidth;
                int inHeight = options.outHeight;
                options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                if ((inWidth > width) || (inHeight > height)) {
                    float widthRatio = ((float) inWidth) / width;
                    float heightRatio = ((float) inHeight) / height;
                    float maxRatio = Math.max(widthRatio, heightRatio);
                    // 1.换算合适的图片缩放值，以减少对JVM太多的内存请求。
                    options.inSampleSize = (int) maxRatio;
                    inSampleSize = options.inSampleSize;
                }
            }
//            bindlowMemeryOption(options);
            bm = decodeBitmap(context, options, data, fullPath, uri, isAssets, resourceId);
        } catch (IncompatibleClassChangeError e) {
//            Log.printErrStackTrace("Crash", e, "May cause dvmFindCatchBlock crash!");
            throw (IncompatibleClassChangeError) (new IncompatibleClassChangeError(
                    "May cause dvmFindCatchBlock crash!").initCause(e));
        } catch (Throwable e) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (densityScale != 0) {
                options.inDensity = (int) (densityScale * DisplayMetrics.DENSITY_DEFAULT);
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            if (!decodeAll && inSampleSize != 0) {
                options.inSampleSize = inSampleSize;
            }
            try {
                bm = decodeBitmap(context, options, data, fullPath, uri, isAssets, resourceId);
            } catch (IncompatibleClassChangeError e2) {
//                Log.printErrStackTrace("Crash", e2, "May cause dvmFindCatchBlock crash!");
                throw (IncompatibleClassChangeError) (new IncompatibleClassChangeError(
                        "May cause dvmFindCatchBlock crash!").initCause(e2));
            } catch (Throwable e2) {
                bm = null;
            }
        }
        return bm;
    }

    private static Bitmap decodeBitmap(Context context,
                                       BitmapFactory.Options options,
                                       byte[] data,
                                       String fullPath,
                                       Uri uri,
                                       boolean isAssets,
                                       int resourceId) throws Exception {
        Bitmap bitmap = null;
        if ((data == null || data.length <= 0)
                && TextUtils.isEmpty(fullPath)
                && uri == null
                && resourceId <= 0) {
            return bitmap;
        }
        boolean decodeBytes = data != null && data.length > 0;
        if (decodeBytes) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } else if (uri != null || isAssets) {
            InputStream is = null;
            if (isAssets) {
                if (TextUtils.isEmpty(fullPath)) {
                    return null;
                }
                is = context.getAssets().open(fullPath);
            } else {
                is = context.getContentResolver().openInputStream(uri);
            }
            bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } else if (resourceId > 0) {
            bitmap = decodeResource(context.getResources(), resourceId, options);
        } else {
            bitmap = BitmapFactory.decodeFile(fullPath, options);
        }
        return bitmap;
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
        Bitmap bm = null;
        InputStream is = null;

        try {
            final TypedValue value = new TypedValue();
            is = res.openRawResource(id, value);

            try {
                bm = BitmapFactory.decodeResourceStream(res, value, is, null, opts);
            } catch (Exception e) {
                // nothing
            }
            if (bm == null) {
                bm = BitmapFactory.decodeStream(is);
            }

        } catch (Exception e) {
            // nothing
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // Ignore
            }
        }

        if (bm == null && opts != null) {
            throw new IllegalArgumentException("Problem decoding into existing bitmap");
        }

        return bm;
    }

    private static DisplayMetrics metrics = null;

    public static DisplayMetrics getDefaultDisplayMetrics(Context context) {
        if (metrics == null) {
            metrics = context.getResources().getDisplayMetrics();
        }
        return metrics;
    }

    /**
     * 处理图片，如果图片exif标识该图片的rotate不为0 则手动旋转后更新本地图片
     *
     * @param path 原图地址
     */
    public static Bitmap rotateBitmapByExif(String path, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            int orientation =
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate += 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate += 90;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate += 90;
                default:
                    break;
            }
            if (rotate != 0) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION,
                        String.valueOf(ExifInterface.ORIENTATION_NORMAL));
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                Bitmap rotatedBitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                matrix, true);
                bitmap.recycle();
                bitmap = rotatedBitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String save(Bitmap bitmap, String key) {
        String imagePath =
                "" + File.separator + key + ".png";
        FileOutputStream out = null;
        try {
            if (bitmap != null) {
                out = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    public static Bitmap maskColor(Bitmap bitmap, int color) {
        Bitmap result = Bitmap.createBitmap(
                bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        canvas.drawColor(color);
        canvas.setBitmap(null);

        bitmap.recycle();

        return result;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, float ratio) {
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        return Bitmap.createScaledBitmap(bitmap, (int) (inWidth * ratio), (int) (inHeight * ratio), false);
    }


    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float scale, float radius, boolean recycleOrigin) {
        return blurBitmap(context, scaleBitmap(bitmap, scale), radius, recycleOrigin);
    }

    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius, boolean recycleOrigin) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        // final Allocation output = Allocation.createTyped(rs, input.getType());

        //Set the radius of the blur: 0 < radius <= 25
        if (radius > 5) {
            radius = 25;
        } else if (radius < 0) {
            radius = 0;
        }
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        if (recycleOrigin) {
            bitmap.recycle();
        }

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }
}



