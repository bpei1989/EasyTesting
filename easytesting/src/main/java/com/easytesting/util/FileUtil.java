package com.easytesting.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.easytesting.util.StringUtil;
import com.easytesting.util.IOUtil;


public class FileUtil {
	
    /**
     * 判断文件是否存在
     * 
     * @param path 文件路径
     * @return 是否存在(true/false)
     */
    public static boolean exist(String path) {
        return (path == null) ? false : new File(path).exists();
    }
    
    /**
     * 判断文件是否存在，如果<code>file</code>为<code>null</code>，则返回<code>false</code>
     * 
     * @param file 文件
     * @return 如果存在返回<code>true</code>
     */
    public static boolean exist(File file) {
        return (file == null) ? false : file.exists();
    }
    
    
    /**
     * 判断是否为目录，如果<code>path</code>为<code>null</code>，则返回<code>false</code>
     * 
     * @param path 文件路径
     * @return 如果为目录<code>true</code>
     */
    public static boolean isDirectory(String path) {
        return (path == null) ? false : new File(path).isDirectory();
    }
    
    /**
     * 判断是否为目录，如果<code>file</code>为<code>null</code>，则返回<code>false</code>
     * 
     * @param file 文件
     * @return 如果为目录<code>true</code>
     */
    public static boolean isDirectory(File file) {
        return (file == null) ? false : file.isDirectory();
    }

    /**
     * 判断是否为文件，如果<code>path</code>为<code>null</code>，则返回<code>false</code>
     * 
     * @param path 文件路径
     * @return 如果为文件<code>true</code>
     */
    public static boolean isFile(String path) {
        return (path == null) ? false : new File(path).isDirectory();
    }

    /**
     * 判断是否为文件
     * 
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return (file == null) ? false : file.isDirectory();
    }
    
    
    /**
     * 简单工厂
     * 
     * @param filename 文件名
     * @return new File(filename)
     */
    public static File file(String filename) {
        if (filename == null) {
            return null;
        }
        return new File(filename);
    }

    /**
     * 简单工厂
     * 
     * @param parent 父目录
     * @param child 子文件
     * @return <code>new File(parent, child);</code>
     */
    public static File file(File parent, String child) {
        if (child == null) {
            return null;
        }

        return new File(parent, child);
    }
    
    
    /**
     * 获取文件大小（字节数）
     * 
     * @param fileName 文件路径
     * @return 文件大小（字节数），如果文件路径为空或者文件路径不存在 ,则返回0
     */
    public static int getFileSize(String fileName) throws IOException {
        if (StringUtil.isEmpty(fileName)) {
            return 0;
        }

        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                return fis.available();
            }

            return 0;
        } finally {
        	IOUtil.close(fis);
        }

    }

    /**
     * 创建文件，递归式创建，包括目录和文件
     * 
     * @param path 文件路径
     * @return 是否创建成功
     * @throws IOException
     */
    public static boolean createFileRecursively(String path) throws IOException {
        if (path == null) {
            return false;
        }

        File file = new File(path);

        if (file.isDirectory()) {
            return file.mkdirs();
        }

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        return file.createNewFile();
    }
    
    /**
     * 获取目录下所有文件文件
     * 
     * @param f2 路径
     * @return 目录下所有文件的列表
     * @throws IOException
     */
    public static void getFileRecursively(File file, List<File> fileList) throws IOException {
    	File[] filearry = file.listFiles();
    	if (filearry == null)
    		return;
    	for (File f : filearry) {
    		if(f.isDirectory()){
				getFileRecursively(f, fileList);
			} else {
				fileList.add(f);
			}
    	}
    }
    

}
