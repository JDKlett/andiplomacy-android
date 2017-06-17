package app.android.vdiplomacy.org.andiplomacy;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jdklett on 17/06/2017.
 */

public class CookieManager {
    private final static String cookieFileName = "cookie.info";
    private static String cookie = null;

    protected static void resetCookie(Context context) throws IOException {
        File path = context.getFilesDir();
        File file = new File(path, cookieFileName);
        file.delete();
    }

    protected static void saveCookie(Context context, String cookie) throws IOException {
        File path = context.getFilesDir();
        File file = new File(path, cookieFileName);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(("cookie="+cookie).getBytes());
        } finally {
            stream.close();
        }
    }

    protected static String loadCookie(Context context) throws IOException{
        if(cookie!=null){
            return cookie;
        }
        File path = context.getFilesDir();
        File file = new File(path, cookieFileName);
        if(!file.exists()){
            return null;
        }
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } finally {
            in.close();
        }
        String[] contents = new String(bytes).split("=");
        return contents[1]+"="+contents[2];
    }

}
