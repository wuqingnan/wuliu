package com.wuliu.client.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 *
 * @author shizhongyong
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean fileExist(String fileName) {
        return fileName == null ? false : fileExist(new File(fileName));
    }

    /**
     * 判断文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean fileExist(File file) {
        boolean result = false;
        if (file != null && file.exists()) {
            result = true;
        }
        file = null;
        return result;
    }

    /**
     * 获取文件大小
     *
     * @param fileName
     * @return
     */
    public static long getSize(String fileName) {
        return fileName == null ? 0 : getSize(new File(fileName));
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getSize(File file) {
        long result = 0;
        if (file != null && file.exists()) {
            result = file.length();
        }
        file = null;
        return result;
    }

    public static void deleteFile(String fileName) {
        if (fileName != null) {
            deleteFile(new File(fileName));
        }
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] flist = file.listFiles();
            int fileLength = 0;
            if (flist != null) {
                fileLength = flist.length;
            }
            for (int i = 0; i < fileLength; i++) {
                if (flist[i].isDirectory()) {
                    deleteFile(flist[i]);
                } else {
                    flist[i].delete();
                }
            }
            file.delete();
        } else if (file.isFile()) {
            file.delete();
        }
    }

    /**
     * 遍历目录下的文件
     *
     * @param fileName
     * @return
     */
    public static String[] listFiles(String fileName) {
        return listFiles(fileName, null);
    }

    /**
     * 遍历目录，获取后缀匹配的文件
     *
     * @param fileName
     * @param suffix
     * @return
     */
    public static String[] listFiles(String fileName, String suffix) {
        if (fileName == null) {
            return new String[0];
        }
        return listFiles(new File(fileName), suffix);
    }

    /**
     * 遍历目录，获取后缀匹配的文件
     *
     * @param file
     * @param suffix
     * @return
     */
    public static String[] listFiles(File file, String suffix) {
        if (file == null || !file.isDirectory()) {
            return new String[0];
        }
        File[] files = file.listFiles();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                if (suffix == null || files[i].getName().endsWith(suffix)) {
                    list.add(files[i].getName());
                }
            }
        }
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        list.clear();
        list = null;
        files = null;
        return result;
    }

    /**
     * 文件重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean rename(String oldName, String newName) {
        File oldFile = new File(oldName);
        File newFile = new File(newName);
        return oldFile.renameTo(newFile);
    }

    /**
     * 检测SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取磁盘空间
     *
     * @param path
     * @return
     */
    public static long getSpace(String path) {
        StatFs sf = new StatFs(path);
        return (long) sf.getBlockCount() * sf.getBlockSize();
    }

    /**
     * 获取磁盘可用空间
     *
     * @param path
     * @return
     */
    public static long getFreeSpace(String path) {
        StatFs sf = new StatFs(path);
        return (long) sf.getAvailableBlocks() * sf.getBlockSize();
    }

    /**
     * SD卡总容量
     * @return
     */
    public static long getSDAllSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * SD卡剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }


    /**
     * 复制文件
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void moveFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            if (!targetFile.exists()) {
                targetFile.getParentFile().mkdirs();
                targetFile.createNewFile();
            }
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
            // 拷贝完毕后把缓存文件删除掉
            sourceFile.delete();
        }
    }

    /**
     * 读取文件
     *
     * @param file 文件
     * @return 文件内容
     */
    public static String readFileToString(File file) {
        if (file == null) {
            return null;
        }
        String result = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            result = new String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 移动文件夹到指定目录
     *
     * @param oldPath 旧目录
     * @param newPath 新目录
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        deleteFile(oldPath);
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath 旧目录
     * @param newPath 新目录
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs();
            File srcPath = new File(oldPath);
            String[] files = srcPath.list();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + files[i]);
                } else {
                    temp = new File(oldPath + File.separator + files[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    output = null;
                    input = null;
                }
                if (temp.isDirectory()) {
                    copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
