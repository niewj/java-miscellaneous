package com.niewj.basic.util.encrypt;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Base64加密解密
 * Base64把原始的数据转换成另一种编码格式保存起来(以至于甚至不能算一种加密技术而是一种格式转换)
 *
 * @see BASE64Encoder#encodeBuffer(byte[])
 * @see BASE64Decoder#decodeBuffer(String)
 */
public class Base64Util {
    private static Logger logger = LoggerFactory.getLogger(Base64Util.class);
    public static final String IMAGE_PNG = "png";
    public static final String IMAGE_JPEG = "jpeg";
    public static final String IMAGE_JPG = "jpg";
    public static final String BASE_IMAGE_PNG = "data:image/png;base64,";
    public static final String BASE_IMAGE_JPG = "data:image/jpg;base64,";

    // ---------------------encrypt operations -------------------------

    /**
     * 字符串Base64加密
     *
     * @param msg 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptBase64(String msg) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encodeBuffer(msg.getBytes());
    }

    /**
     * 将文件转换成字符串
     *
     * @param file 目标文件
     * @return 结果字符串Base64表示
     * @throws IOException
     */
    public static String encryptFileBase64(File file) {
        return encryptFileBase64(file, null);
    }

    /**
     * @param file
     * @param suffixType
     * @return
     * @throws IOException
     */
    public static String encryptFileBase64(File file, String suffixType) {
        if (file == null && !file.isFile()) {
            try {
                throw new FileNotFoundException("File not exist!");
            } catch (FileNotFoundException e) {
                logger.error("file {} is not exist", file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        StringBuffer sbuff;
        if (IMAGE_PNG.equalsIgnoreCase(suffixType)) {
            sbuff = new StringBuffer(BASE_IMAGE_PNG);
        } else if (IMAGE_JPEG.equalsIgnoreCase(suffixType) || IMAGE_JPG.equalsIgnoreCase(suffixType)) {
            sbuff = new StringBuffer(BASE_IMAGE_JPG);
        } else {
            logger.info("default type of image type is jpg");
            sbuff = new StringBuffer(BASE_IMAGE_JPG);
        }

        BASE64Encoder encoder = new BASE64Encoder();

        byte[] fileBytes = new byte[0];
        try {
            fileBytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sbuff.append(encoder.encodeBuffer(fileBytes).replaceAll("[\\s*\n\r\t]", ""));
        return sbuff.toString();
    }

    // --------------------------------decrypt operations -------------------

    /**
     * 字符串Base64解密
     *
     * @param msgEncrypted 待解密的字符串
     * @return 解密后的字符串
     */
    public static String decryptBase64(String msgEncrypted) {
        if (StringUtils.isBlank(msgEncrypted)) {
            return null;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(msgEncrypted));
        } catch (IOException e) {
            logger.error("encrypted string: {} decrypt error", msgEncrypted);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Base64编码的文件解码为文件
     *
     * @param encryptedString 编码的文件字符串
     * @param destFilePath    目标文件名(无后缀)
     * @param suffix          后缀名
     * @return
     * @throws IOException
     */
    public static File decryptFileBase64(String encryptedString, String destFilePath, String suffix) {
        if (StringUtils.isBlank(encryptedString)) {
            return null;
        }
        if (encryptedString.contains(BASE_IMAGE_PNG)) {
            encryptedString = encryptedString.replace(BASE_IMAGE_PNG, "");
        }
        if (encryptedString.contains(BASE_IMAGE_JPG)) {
            encryptedString = encryptedString.replace(BASE_IMAGE_JPG, "");
        }
        BASE64Decoder decoder = new BASE64Decoder();
        File destFile = new File(destFilePath + "." + suffix);

        byte[] bytes = new byte[0];
        try {
            bytes = decoder.decodeBuffer(encryptedString);
            FileUtils.writeByteArrayToFile(destFile, bytes);
        } catch (IOException e) {
            logger.error("解密字符串为文件： {}异常！", destFilePath, e);
            e.printStackTrace();
        }

        return destFile;
    }

    /**
     * 将路径下文件转换成字符串
     *
     * @param path 文件路径
     * @return 结果字符串Base64表示
     * @throws IOException
     */
    public static String encryptFileBase64(String path) {
        File file = new File(path);
        return encryptFileBase64(file);
    }

}
