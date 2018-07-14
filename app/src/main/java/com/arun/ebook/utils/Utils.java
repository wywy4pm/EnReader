package com.arun.ebook.utils;

import android.content.Context;
import android.text.TextUtils;

import com.arun.ebook.bean.BookBean;
import com.arun.ebook.common.Constant;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Utils {
    //要获取的文件夹的所在位置
    public static void getAllTxtFile(Context context, List<BookBean> books, File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        getAllTxtFile(context, books, f);
                    }
                }
            } else if (file.exists()) {
                if (!TextUtils.isEmpty(getExtensionName(file.getName()))
                        && getExtensionName(file.getName()).equals("txt")) {
                    BookBean bean = new BookBean();
                    String configString = SharedPreferencesUtils.getConfigString(context, getFileKey(file));
                    if (!TextUtils.isEmpty(configString)) {
                        String[] configs = configString.split("_");
                        bean.lastReadTime = longToDate(Long.valueOf(configs[0]));
                        bean.readProgress = configs[1];
                        bean.light = Integer.parseInt(configs[2]);
                        bean.bgColor = Integer.parseInt(configs[3]);
                        bean.spSize = Integer.parseInt(configs[4]);
                        bean.textColor = Integer.parseInt(configs[5]);
                        bean.lineSpace = Integer.parseInt(configs[6]);
                        bean.edgeSpace = Integer.parseInt(configs[7]);
                        bean.paraSpace = Integer.parseInt(configs[8]);
                    }
                    bean.txtFile = file;
                    books.add(bean);
                }
            }
        }
    }


    public static String getFileKey(File file) {
        return file.getAbsolutePath() + Utils.getCreateTime(file.getAbsolutePath());
    }

    /**
     * 读取文件创建时间
     */
    public static String getCreateTime(String filePath) {
        String strTime = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd /C dir "
                    + filePath
                    + "/tc");
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(".txt")) {
                    strTime = line.substring(0, 17);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < (filename.length() - 1)) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     * @Description: long类型转换成日期
     */
    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sd.format(date);
    }


    /*   *//**
     * 读写文件内容
     * @param filePath
     *//*
    public static void readWriteFile(String filePath) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            String line = null;
            long lastPoint = 0;
            while ((line = raf.readLine()) != null) {
                String str = verifyXML(line);
                long ponit = raf.getFilePointer();
                raf.seek(lastPoint);
                raf.writeBytes(str);
                lastPoint = ponit;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    */

    /**
     * 乱码字符去除
     *
     * @param
     * @return
     *//*
    public static String verifyXML(String in) {
        StringBuffer out = new StringBuffer();
        char current;
        if (in == null || ("".equals(in)))
            return "";
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current==0x9)||(current==0xA)||(current==0xD)
                    ||((current>=0x20)&&(current<=0xD7FF))
                    ||((current>=0xE000)&&(current<=0xFFFD))
                    ||((current>=0x10000)&&(current<=0x10FFFF))){
                out.append(current);
            }
        }
        return out.toString();
    }*/
    public static String getEncoding(File book) {
        UniversalDetector detector = new UniversalDetector(null);
        byte[] bytes = new byte[1024];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(book));
            int length;
            while ((length = bufferedInputStream.read(bytes)) > 0) {
                detector.handleData(bytes, 0, length);
            }
            detector.dataEnd();
            bufferedInputStream.close();
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return detector.getDetectedCharset();
    }

    public static File[] readFontsFile(Context context) {
        File[] fileNames = null;
        File dir = new File(Constant.PATH_FONT);
        if (!dir.exists()) {
            try {
                String[] fontNames = context.getAssets().list("fonts");
                int writeCount = 0;
                for (int i = 0; i < fontNames.length; i++) {
                    InputStream inputStream = context.getAssets().open("fonts/" + fontNames[i]);
                    boolean isWrite = writeFontFileToDisk(inputStream, fontNames[i]);
                    if (isWrite) {
                        writeCount++;
                    }
                }
                if (writeCount == fontNames.length) {
                    fileNames = readFontFileFromDisk();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fileNames = readFontFileFromDisk();
        }
        return fileNames;
    }

    private static File[] readFontFileFromDisk() {
        File[] files = null;
        try {
            File dir = new File(Constant.PATH_FONT);
            if (dir.exists()) {
                files = dir.listFiles();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    private static boolean writeFontFileToDisk(InputStream inputStream, String fileName) {
        try {
            final File file = createDirAndFile(Constant.PATH_FONT, fileName);
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static File createDirAndFile(String path, String fileName) {
        File dir = new File(path);
        File file = new File(path + File.separator + fileName);
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
