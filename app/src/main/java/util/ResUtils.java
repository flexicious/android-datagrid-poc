package util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SoftSuave-PC on 1/9/2018.
 */

public class ResUtils {

    public static String getStringFromResource(final Context mContext, int resouceId) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(resouceId)));
        StringBuilder builder = new StringBuilder();
        String aux = "";
        try {
            while ((aux = reader.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    public static InputStream getResourceInputStream(final Context mContext, int resouceId) {
        return mContext.getResources().openRawResource(resouceId);
    }

    public static Drawable getDrawable(Context context, int resourceId) {
        return context.getResources().getDrawable(resourceId, null);
    }
}
