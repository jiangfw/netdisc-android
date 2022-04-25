package com.fuwei.android.libcommon.encrypt;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author chentao
 */

public class ZipUtil {

    /**
     *
     * @param zipFile
     * @param file
     * @param context
     * @throws Exception
     */
    public static void fileZip(File zipFile, File file, Context context)
            throws Exception {
        if (file.isFile()) {
            ZipOutputStream zos = new ZipOutputStream(context.openFileOutput(
                    zipFile.getName(), Activity.MODE_PRIVATE));
            zos.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fis = context.openFileInput(file.getName());
            byte[] bb = new byte[2048];
            int aa = 0;
            while ((aa = fis.read(bb)) != -1) {
                zos.write(bb, 0, aa);
            }
            fis.close();
            zos.close();

        }
    }

    /**

    /**
     *
     * @param zis
     * @param file
     * @param context
     * @throws Exception
     */
    public static void fileUnZip(ZipInputStream zis, File file, Context context)
            throws Exception {
        ZipEntry zip = zis.getNextEntry();
        while (zip != null) {
            String name = zip.getName();
            File f = new File(file.getAbsolutePath() + "/" + name);
            if (zip.isDirectory()) {
                f.mkdirs();
                fileUnZip(zis, file, context);
            } else {
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                byte b[] = new byte[2048];
                int aa = 0;
                while ((aa = zis.read(b)) != -1) {
                    fos.write(b, 0, aa);
                }
                fos.close();
            }
            zip = zis.getNextEntry();
        }
    }

    /**
     *
     * @param is
     * @return
     */
    public static ZipInputStream unZip(InputStream is) {
        ZipInputStream zis = new ZipInputStream(is);
        try {
            zis.getNextEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zis;
    }
}
