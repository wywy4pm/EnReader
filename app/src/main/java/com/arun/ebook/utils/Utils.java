package com.arun.ebook.utils;

import android.text.TextUtils;

import com.arun.ebook.bean.BookBean;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Utils {
    //要获取的文件夹的所在位置
    public static void getAllTxtFile(List<BookBean> books, File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        getAllTxtFile(books, f);
                    }
                }
            } else if (file.exists()) {
                if (!TextUtils.isEmpty(getExtensionName(file.getName()))
                        && getExtensionName(file.getName()).equals("txt")) {
                    BookBean bean = new BookBean();
                    bean.txtFile = file;
                    books.add(bean);
                }
            }
        }
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


    *//**
     * 乱码字符去除
     * @param in
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
}
