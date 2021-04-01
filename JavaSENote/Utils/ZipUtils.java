package com.zuahua;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @author zhanghua
 * 文件压缩工具类
 * zip4j 版本：2.7.0
 * @createTime 2021/4/1 9:39
 */
public class ZipUtils {
    private static String zipExt = ".zip";

    /**
     * 通用文件压缩 自动生成压缩文件名
     *
     * @param fileList 压缩文件列表
     * @param desPath  压缩路径
     * @return 压缩文件完整路径 null 表示压缩失败
     */
    public static String compress(List<File> fileList, String desPath) {
        File file = new File(desPath, UUID.randomUUID().toString() + zipExt);
        ZipFile zipFile = new ZipFile(file);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);

        String zipPath = file.getAbsolutePath();
        try {
            zipFile.addFiles(fileList, parameters);
        } catch (Exception e) {
            zipPath = null;
            e.printStackTrace();
        }
        return zipPath;
    }

    /**
     * 加密压缩 自动生成压缩文件名
     *
     * @param fileList 压缩文件列表
     * @param desPath  压缩路径
     * @param password 压缩密码
     * @return 压缩文件完整路径 null 表示压缩失败
     */
    public static String compressEncrypt(List<File> fileList, String desPath, String password) {
        if (StringUtils.isEmpty(password)) {
            return null;
        }

        File file = new File(desPath, UUID.randomUUID().toString() + zipExt);
        String zipPath = file.getAbsolutePath();

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        // 加密
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.AES);
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

        ZipFile zipFile = new ZipFile(file, password.toCharArray());
        try {
            zipFile.addFiles(fileList, parameters);
        } catch (Exception e) {
            zipPath = null;
            e.printStackTrace();
        }
        return zipPath;
    }


    /**
     * 通用文件解压 在压缩文件目录创建同名目录 解压到此目录
     *
     * @param zipFile  zip文件
     * @param password 密码 null 表示压缩文件未加密
     * @return 解压缩目录 null 表示解压失败
     */
    public static String extract(File zipFile, String password) {
        if (zipFile == null || !zipFile.exists()) {
            return null;
        }

        // 创建解压目录
        String fileAbsolutePath = zipFile.getAbsolutePath();
        String exFolder = fileAbsolutePath.substring(0, fileAbsolutePath.lastIndexOf("."));
        File folderFile = new File(exFolder);
        if (!folderFile.isDirectory() || !folderFile.exists()) {
            folderFile.mkdir();
        }

        ZipFile zipFile1 = new ZipFile(zipFile);

        // 是否加密
        try {
            if(zipFile1.isEncrypted()) {
                zipFile1 = new ZipFile(zipFile, password.toCharArray());
            }
        } catch (ZipException e) {
            e.printStackTrace();
            return null;
        }

        if (!zipFile1.isValidZipFile()) {
            return null;
        }

        try {
            zipFile1.extractAll(exFolder);
        } catch (ZipException e) {
            e.printStackTrace();
            return null;
        }
        return exFolder;
    }
}
