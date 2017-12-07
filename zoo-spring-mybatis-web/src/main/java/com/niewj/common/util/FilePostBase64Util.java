package com.niewj.common.util;

import com.niewj.common.util.http.HttpClientFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;

/**
 * @author niewj
 * @see //FilePostBase64Util#fetchRemoteFile(String, String, String, String, String) httpclient下载远程文件
 * @see FilePostBase64Util#encodeFileToBase64(File) 将给定的文件编码成base64字符串
 * @see FilePostBase64Util#decodeBase64ToFile(String, String, String, String) 解码base64字符串并生成文件
 * <p/>
 * http文件下载、base64编解码工具
 * Created by weijun.nie on 2017/11/1.
 */
public class FilePostBase64Util {

    private static final Logger logger = LoggerFactory.getLogger(FilePostBase64Util.class);

    public final static String UTF8 = "UTF-8";
    public final static int CONNECTION_REQUEST_TIMEOUT = 5 * 1000;
    public final static int CONNECTION_TIMEOUT = 5 * 1000; //设置默认连接超时为5s
    public final static int SOCKET_TIME_OUT = 60 * 1000; //设置默认读取超时为60s

    public final static String PARAM_BIZ_CODE = "bizCode";
    public final static String PARAM_FILE_ID = "fileId";

    //从连接池获取连接、连接建立、读取数据超时时间都设置为5s,且不会重试,默认编码UTF-8
    private static HttpClient httpClient = HttpClientFactory.create();

    /**
     * 从给定的IFS地址请求获取指定fileId的文件
     *
     * @param url     下载IFS文件的地址url
     * @param bizCode 文件对应的bizCode
     * @param fileId  文件的fileId
     * @param dir     文件下载的目录(最好是有清理的临时目录)
     * @param fname   下载后文件名(包括后缀扩展名-如果需要的话)
     * @return 生成的文件对象
     * @throws Exception
     */
//    public static File fetchRemoteFile(String url, String bizCode, String fileId, String dir, String fname) {
//        long startTime = System.currentTimeMillis();
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(PARAM_BIZ_CODE, bizCode);
//        params.put(PARAM_FILE_ID, fileId);
//        File file = postWithDownload(url, params, dir, fname);
//        logger.info("*从IFS影像服务拉取图片fileId: {} 数据耗时: {}", fileId, System.currentTimeMillis() - startTime);
//        return file;
//    }
//
//    /**
//     * 发送下载文件的HTTP_POST请求
//     * 1)该方法用来下载文件
//     * 2)该方法会自动关闭连接,释放资源
//     * 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
//     * 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
//     * 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html; charset=GBK]的charset值
//     * 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
//     * 6)下载的文件会保存在java.io.tmpdir环境变量指定的目录中
//     * CentOS6.5下是/tmp,
//     * CentOS6.5下的Tomcat中是/app/tomcat/temp,
//     * Win7下是C:\Users\Jadyer\AppData\Local\Temp\
//     * 7)下载的文件若比较大,可能导致程序假死或内存溢出,此时可考虑在本方法内部直接输出流
//     *
//     * @param reqURL 请求地址
//     * @param params 请求参数,无参数时传null即可
//     * @return 应答Map有两个key, isSuccess--yes or no,fullPath--isSuccess为yes时返回文件完整保存路径,failReason--isSuccess为no时返回下载失败的原因
//     */
//    public static File postWithDownload(String reqURL, Map<String, String> params, String dir, String fname) {
//        if (params == null || params.isEmpty()) {
//            logger.error("postWithDownload:params is null or empty");
//            return null;
//        }
//
//        // 1. httpClient请求参数
//        List<NameValuePair> nvpParam = Lists.newArrayListWithExpectedSize(params.size());
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            nvpParam.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//
//        HttpResponse response = null;
//        File file = null;
//        HttpEntity entity = null;
//        try {
//            //构造请求配置
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
//                    .setConnectTimeout(CONNECTION_TIMEOUT)
//                    .setSocketTimeout(SOCKET_TIME_OUT)
//                    .build();
//
//            // 构造请求参数
////            HttpEntity pEntity = new UrlEncodedFormEntity(nvpParam, UTF8);
//            //构造请求命令
//            HttpUriRequest request = RequestBuilder.post(reqURL)
//                    .setCharset(Charset.forName(UTF8))
//                    .setConfig(requestConfig)
//                    .setEntity(new UrlEncodedFormEntity(nvpParam, UTF8))
//                    .build();
//
//            //设置请求头
//            Map<String, String> headers = Maps.newHashMapWithExpectedSize(1);
////            if (CollectionUtil.notEmpty( )) {
////                for (String key : headers.keySet()) {
////                    request.setHeader(key, headers.get(key));
////                }
////            }
//
//            //执行请求
//            response = httpClient.execute(request);
//
//            //判断请求返回码
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (response == null || statusCode != HttpStatus.SC_OK) {
//                return null;
//            }
//
//            entity = response.getEntity();
//
//            if (null == entity || !entity.getContentType().getValue().startsWith(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
//                logger.warn("*文件下载失败，url:{},params:{}", reqURL, JSONUtil.safeToJson(params));
//                return null;
//            }
//
//            File dirFile = new File(dir);
//            if (!dirFile.exists()) {
//                dirFile.mkdirs();
//            }
//
//            file = new File(dirFile, fname);
//
//            FileUtils.copyInputStreamToFile(entity.getContent(), file); // 赋值文件
//
//            logger.info("fullPath=" + file.getCanonicalPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (response != null && response instanceof CloseableHttpResponse) {
//                try {
//                    ((CloseableHttpResponse) response).close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return file;
//    }


    /**
     * @param file 源文件
     * @return file文件的base64字符串表示
     * @throws IOException
     */
    public static String encodeFileToBase64(File file) {
        logger.info("getFileBase64Desc:file= {}", file.getAbsolutePath());
        StringBuffer _img = new StringBuffer("data:image/jpeg;base64,");
        if (file == null || !file.isFile()) {
            logger.error("file={} is not a file or is null", file.getAbsolutePath());
            return null;
        }

        // 获取二进制输入流
        try {
            _img.append(new BASE64Encoder().encode(FileUtils.readFileToByteArray(file)).replaceAll("[\\s*\t\n\r]", ""));
            return _img.toString();
        } catch (IOException e) {
            logger.error("encode base64文件：{} 异常", file.getAbsolutePath(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解码base64文件对象
     *
     * @param base64FileString 源文件的base64字符串
     * @return 文件对象
     * @throws IOException
     */
    public static File decodeBase64ToFile(String base64FileString, String dir, String fname, String suffix) throws IOException {
        File file = null;
        byte[] fileBytes;
        try {
            fileBytes = new BASE64Decoder().decodeBuffer(base64FileString);
            // 上传位置目录：parentDir
            File parentDir = new File(dir);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            file = StringUtils.isBlank(suffix) ? new File(parentDir, fname) : new File(parentDir, fname + "." + suffix);
            FileUtils.writeByteArrayToFile(file, fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("解码base64文件对象e: {}", e);
        }

        return file;
    }

//    public static void main(String[] args) {
//        try {
//            File f = FilePostBase64Util.fetchRemoteFile("http://filesystem.msxf.lotest/file/download", "666666", "104225573", "d:/aaa", "13233");
//            String s = FilePostBase64Util.encodeFileToBase64(f);
//
//            System.out.println(s);
//            File ff = FilePostBase64Util.decodeBase64ToFile(s, "d:/tmp", "22123", "png");
////            File ff = FilePostBase64Util.decodeBase64ToFile(s, "d:/tmp", "123", null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
