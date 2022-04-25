package com.fuwei.android.libcommon.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 写文件工具类
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    private static File cachedStorageDir = null;
    private static ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

    public static String getCacheAbsolutePath(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        boolean shouldUseExternalCache = Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable();

        File fileCacheDir = shouldUseExternalCache ? getExternalCacheDir(context)
                : context.getCacheDir();

        if (fileCacheDir == null) {
            fileCacheDir = context.getCacheDir();
        }

        final String cachePath = fileCacheDir.getPath();

        return cachePath + File.separator;
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (cachedStorageDir == null) {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())) {
                cachedStorageDir = context.getExternalCacheDir();
            }
        }

        return cachedStorageDir;
    }

    /**
     * 读取文本文件内容
     *
     * @param filename
     * @return
     */
    public static String readFileContent(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileReader fr = null;
        BufferedReader br = null;
        StringBuilder content = new StringBuilder();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }


    public static byte[] readFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileInputStream fileInputStream = null;

        int bufferSize = (int) file.length();
        byte[] buffer = new byte[bufferSize];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(buffer, 0, bufferSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return buffer;
    }

    /**
     * 将文本写入到文件中. (异步接口)
     *
     * @param filePath
     * @param content
     * @param isAppend 是否追加
     */
    public static void saveStringToFile(final String filePath, final String content, final boolean isAppend) {
        mSingleThreadExecutor.submit(new Runnable() {
            @Override
            public synchronized void run() {
                if (TextUtils.isEmpty(filePath) || filePath.matches("null\\/.*")) {
                    Log.w(TAG, "The file to be saved is null! filePath:" + filePath);
                    return;
                }
                FileWriter fw = null;
                try {
                    File file = new File(filePath);
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    fw = new FileWriter(file, isAppend);
                    fw.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fw != null) {
                            fw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 将文本写入到文件中. (同步接口)
     *
     * @param filePath
     * @param content
     * @param isAppend 是否追加
     */
    public static void syncSaveStringToFile(final String filePath, final String content, final boolean isAppend) {
        syncSaveStringToFile(filePath, content, isAppend, true);
    }

    /**
     * 将文本写入到文件中. (同步接口)
     *
     * @param filePath
     * @param content
     * @param isAppend 是否追加
     * @param optimization 是否优化写文件
     */
    public static HashMap<String, BufferedWriter> writerHashMap = new HashMap<>();
    public static long flushTimestamp;

    public static void syncSaveStringToFile(final String filePath, final String content, final boolean isAppend, final boolean optimization) {
        if (TextUtils.isEmpty(filePath) || filePath.matches("null\\/.*")) {
            Log.w(TAG, "The file to be saved is null! filePath:" + filePath);
            return;
        }
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            BufferedWriter bw = writerHashMap.get(filePath);

            boolean exist = file.exists();

            if (!exist) {
                file.createNewFile();
                if (bw != null) {
                    bw.close();
                    bw = null;
                    writerHashMap.remove(filePath);
                }
            }

            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(filePath, isAppend));
                writerHashMap.put(filePath, bw);
            }

            bw.write(content);
            if (!optimization || //不做优化
                    System.currentTimeMillis() - flushTimestamp > 10000) {//10s刷新一次
                flushTimestamp = System.currentTimeMillis();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 将文本写入到文件中
     *
     * @param filePath
     * @param content
     * @param isAppend 是否追加
     */
    public static void saveStringToFile(final String filePath, final String content, final boolean isAppend, final OnFileCreatedListener listener) {
        mSingleThreadExecutor.submit(new Runnable() {
            @Override
            public synchronized void run() {
                FileWriter fw = null;
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                    fw = new FileWriter(file.getAbsoluteFile());

                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(content);

                    bw.close();
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError();
                    }
                } finally {
                    try {
                        if (fw != null) {
                            fw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface OnFileCreatedListener {
        void onSuccess();

        void onError();
    }

    public static ExecutorService getSaveFileThreadPool() {
        return mSingleThreadExecutor;
    }

    /**
     * 删除目录下所有文件
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 校验md5
     *
     * @param is
     * @param file
     * @return
     */
    public static boolean checkMD5(final InputStream is, File file) {
        if (file.exists()) {
            try {
                FileInputStream destFis = new FileInputStream(file);
                byte[] md5_1 = getFileMD5String(destFis);
                byte[] md5_2 = getFileMD5String(is);
                boolean same = true;
                int minLength = (md5_1.length > md5_2.length) ? md5_2.length
                        : md5_1.length;
                for (int k = 0; k < minLength; k++) {
                    if (md5_1[k] != md5_2[k]) {
                        same = false;
                        break;
                    }
                }

                destFis.close();

                return same;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * get File MD5 codes string
     *
     * @param in inputstream
     * @return
     */
    public static byte[] getFileMD5String(InputStream in) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[100 * 1024];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, length);
            }
            return messagedigest.digest();

        } catch (NoSuchAlgorithmException nsaex) {
            nsaex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    /**
     * 检查文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean isExits(String file) {
        return new File(file).exists();
    }
}
