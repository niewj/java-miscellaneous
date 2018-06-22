//package com.niewj.common.util;
//
//import com.alibaba.fastjson.JSON;
//import com.xxxxx.datasource.common.ResponseCodeEnum;
//import com.xxxxx.datasource.http.niewjHttpClient;
//import com.xxxxx.datasource.http.niewjHttpResponse;
//import com.xxxxx.datasource.thirdpart.exception.ThirdPartException;
//import com.xxxxx.datasource.thirdpart.utils.FileUtils;
//import com.xxxxx.datasource.thirdpart.utils.StringMsg;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.*;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.entity.ContentType;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import sun.misc.BASE64Decoder;
//
//import javax.annotation.PostConstruct;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.SocketTimeoutException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * Created by  on 2017/5/9.
// */
//@Component
//public class FtpHelper {
//
//    private static final Logger logger = LoggerFactory.getLogger(FtpHelper.class);
//    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 20; //设置默认连接超时为2s
//    private static final int DEFAULT_SO_TIMEOUT = 1000 * 600;        //设置默认读取超时为600s
//    private static final String DEFAULT_CHARSET = "UTF-8";
//
//    @Value("${ftp.image.url}")
//    String imageFtpUrl1;
//    @Value("${ftp.image.path}")
//    String simpleImagePath1;
//
//    private static String simpleImagePath;
//
//    private static String imageFtpUrl;
//
//    @PostConstruct
//    public void init() {
//        simpleImagePath = simpleImagePath1;
//        imageFtpUrl = imageFtpUrl1;
//    }
//
//    public static String uploadFileToFtp(String imageSequenceCode, String serviceCode) {
//
//        if (StringUtils.isBlank(serviceCode)) {
//            serviceCode = "";
//        }
//
//        byte[] imageByteArray;
//        try {
//            imageByteArray = new BASE64Decoder().decodeBuffer(imageSequenceCode);
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "decode image file base64 exception"), e);
//        }
//
//        File tmpDir = new File(simpleImagePath + File.separator + "tmp");
//        if(!tmpDir.exists()){
//            tmpDir.mkdirs();
//        }
//        String file1Name = simpleImagePath + File.separator + "tmp" + File.separator + FileUtils.makeUnitKey() + ".";
//        File file = new File(file1Name + "png");
//
//        try {
//            FileUtils.write(file, new ByteArrayInputStream(imageByteArray));
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg("create image file exception"), e);
//        }
//
//        niewjHttpResponse uploadRes;
//        try {
//            Map<String, String> otherParam = new HashMap<>();
//            otherParam.put("bizCode", NciicHelper.simpleImageFtpBizCode);
//            otherParam.put("fileCategory", NciicHelper.simpleImageFtpCategory);
//            otherParam.put("serialNo", UUID.randomUUID().toString().replaceAll("-", ""));
//            uploadRes = niewjHttpClient.fileUpload(file, otherParam, imageFtpUrl + "/file/upload");
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + " upload image exception", e));
//        }
//
//        if (uploadRes.getHttpStatus() != 200)
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + " upload image exception : HttpStatueCode " + uploadRes.getHttpStatus()));
//
//        Map contentMap;
//        try {
//            contentMap = JSON.parseObject(uploadRes.getContent(), Map.class);
//        } catch (Exception e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "JSON.parseObject error!"), e);
//        }
//
//        Object fileId = contentMap.get("fileId");
//        if (fileId == null || StringUtils.isBlank(fileId.toString()))
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "upload image success, but fileId incorrect, uploadResult:" + uploadRes));
//
//        return fileId.toString();
//    }
//
//    public static File getFile(String fileId) {
//        long startTime = System.currentTimeMillis();
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("bizCode", NciicHelper.simpleImageFtpBizCode);
//        params.put("fileId", fileId);
//        File file = postWithDownload(imageFtpUrl + "/file/download", params);
//        logger.info("*影像服务获取图片数据耗时 : fileId:{},spentTime:{}", fileId, System.currentTimeMillis() - startTime);
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
//     * CentOS6.5下是/tmp,CentOS6.5下的Tomcat中是/app/tomcat/temp,Win7下是C:\Users\Jadyer\AppData\Local\Temp\
//     * 7)下载的文件若比较大,可能导致程序假死或内存溢出,此时可考虑在本方法内部直接输出流
//     *
//     * @param reqURL 请求地址
//     * @param params 请求参数,无参数时传null即可
//     * @return 应答Map有两个key, isSuccess--yes or no,fullPath--isSuccess为yes时返回文件完整保存路径,failReason--isSuccess为no时返回下载失败的原因
//     */
//    public static File postWithDownload(String reqURL, Map<String, String> params) {
//        Map<String, String> resultMap = new HashMap<String, String>();
//        HttpClient httpClient = new DefaultHttpClient();
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
//        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
//        HttpPost httpPost = new HttpPost(reqURL);
//        HttpEntity entity = null;
//        try {
//            //由于下面使用的是new UrlEncodedFormEntity(....),所以这里不需要手工指定CONTENT_TYPE为application/x-www-form-urlencoded
//            //因为在查看了HttpClient的源码后发现,UrlEncodedFormEntity所采用的默认CONTENT_TYPE就是application/x-www-form-urlencoded
//            //httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + encodeCharset);
//            if (null != params) {
//                java.util.List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//                for (Map.Entry<String, String> entry : params.entrySet()) {
//                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//                }
//                httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
//            }
//            HttpResponse response = httpClient.execute(httpPost);
//            entity = response.getEntity();
//            if (null != entity && entity.getContentType().getValue().startsWith(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
//                String filename = null;
//                for (Header header : response.getAllHeaders()) {
//                    if (header.toString().startsWith("Content-Disposition")) {
//                        filename = header.toString().substring(header.toString().indexOf("filename=") + 10);
//                        filename = filename.substring(0, filename.length() - 1);
//                        break;
//                    }
//                }
//                if (StringUtils.isBlank(filename)) {
//                    Header contentHeader = response.getFirstHeader("Content-Disposition");
//                    if (null != contentHeader) {
//                        HeaderElement[] values = contentHeader.getElements();
//                        if (values.length == 1) {
//                            NameValuePair param = values[0].getParameterByName("filename");
//                            if (null != param) {
//                                filename = param.getValue();
//                            }
//                        }
//                    }
//                }
//                logger.info("get file from 影像系统 is success！the file name is {}", filename);
//                filename = UUID.randomUUID().toString().replaceAll("-", "");
//                File _file = new File(System.getProperty("java.io.tmpdir") + "/" + filename);
//                org.apache.commons.io.FileUtils.copyInputStreamToFile(entity.getContent(), _file);
//                resultMap.put("isSuccess", "yes");
//                resultMap.put("fullPath", _file.getCanonicalPath());
//                logger.info("*文件下载成功，url:{},params:{}", reqURL, JSONUtil.safeToJson(params));
//                return _file;
//            } else {
//                logger.warn("*文件下载失败，url:{},params:{}", reqURL, JSONUtil.safeToJson(params));
//            }
//        } catch (ConnectTimeoutException cte) {
//            logger.error("*请求通信[" + reqURL + "]时连接超时", cte);
//        } catch (SocketTimeoutException ste) {
//            logger.error("*请求通信[" + reqURL + "]时读取超时", ste);
//        } catch (Exception e) {
//            logger.error("*请求通信[" + reqURL + "]时遇到异常", e);
//        } finally {
//            try {
//                EntityUtils.consume(entity);
//            } catch (IOException e) {
//                logger.error("*请求通信[" + reqURL + "]时关闭远程应答文件流时发生异常,堆栈轨迹如下", e);
//            }
//            httpClient.getConnectionManager().shutdown();
//        }
//        return null;
//    }
//
////    public static void main(String[] args) {
////        String imgPath = "http://filesystem.xxxxx.lotest";
////        long startTime = System.currentTimeMillis();
////        Map<String, String> params = new HashMap<String, String>();
////        params.put("bizCode", "103002");
////        params.put("fileId", "104127802");
////        File file = postWithDownload(imgPath + "/file/download", params);
////        logger.info("*影像服务获取图片数据耗时 : fileId:{},spentTime:{}", "104127802", System.currentTimeMillis() - startTime);
////        System.out.println(file.getAbsolutePath());
////    }
//
//    public static String uploadFileToFtp(String imageSequenceCode, String applyNo, String serviceCode) {
//        return uploadFileToFtp(imageSequenceCode, applyNo, serviceCode, null);
//    }
//
//    /**
//     *  上传文件
//     * @param imageSequenceCode base64编码的文件字符串
//     * @param applyNo
//     * @param serviceCode   N:公安,P:本人照片,F:身份证正面照 D:人脸识别照片
//     * @param suffix 后缀名，如果不指定，png是也
//     * @return
//     */
//    public static String uploadFileToFtp(String imageSequenceCode, String applyNo, String serviceCode, String suffix) {
//
//        if (StringUtils.isBlank(serviceCode)) {
//            serviceCode = "";
//        }
//
//        byte[] imageByteArray;
//        try {
//            imageByteArray = new BASE64Decoder().decodeBuffer(imageSequenceCode);
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "decode image file base64 exception"), e);
//        }
//
//        File tmpDir = new File(simpleImagePath + File.separator + "tmp");
//        if(!tmpDir.exists()){
//            tmpDir.mkdirs();
//        }
//        String file1Name = simpleImagePath + File.separator + "tmp" + File.separator + FileUtils.makeUnitKey() + ".";
//
//        File file = null;
//        if (StringUtils.isBlank(suffix)) {
//            file = new File(file1Name + "png");
//        } else { // 如果指定后缀名，就使用指定的。
//            file = new File(file1Name + suffix);
//        }
//        try {
//            FileUtils.write(file, new ByteArrayInputStream(imageByteArray));
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg("create image file exception"), e);
//        }
//
//        niewjHttpResponse uploadRes;
//        try {
//            Map<String, String> otherParam = new HashMap<>();
//            otherParam.put("bizCode", NciicHelper.simpleImageFtpBizCode);
//            otherParam.put("fileCategory", serviceCode);
//            otherParam.put("serialNo", UUID.randomUUID().toString().replaceAll("-", ""));
//            otherParam.put("bizNo", applyNo);
//            uploadRes = niewjHttpClient.fileUpload(file, otherParam, imageFtpUrl + "/file/upload");
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + " upload image exception", e));
//        }
//
//        if (uploadRes.getHttpStatus() != 200)
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + " upload image exception : HttpStatueCode " + uploadRes.getHttpStatus()));
//
//        Map contentMap;
//        try {
//            contentMap = JSON.parseObject(uploadRes.getContent(), Map.class);
//        } catch (Exception e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "JSON.parseObject error!"), e);
//        }
//
//        Object fileId = contentMap.get("fileId");
//        if (fileId == null || StringUtils.isBlank(fileId.toString()))
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(serviceCode + "upload image success, but fileId incorrect, uploadResult:" + uploadRes));
//
//        return fileId.toString();
//    }
//
//    /**
//     * ifs綁定申請單號
//     *
//     */
//    public static void bondApplyNo(String applyNo, String fileId, String fileCategory) throws Exception {
//        String url = imageFtpUrl + "/file/link?fileCategory=" + fileCategory + "&bizCode=" + NciicHelper.simpleImageFtpBizCode + "&bizNo=" + applyNo + "&fileId=" + fileId;
//        logger.info("bondApplyNo the url is {} ", url);
//        niewjHttpResponse niewjHttpResponse = niewjHttpClient.get(url, 10000, 10000);
//        logger.info("bondApplyNo the response is {},status is {}", niewjHttpResponse.getContent(), niewjHttpResponse.getHttpStatus());
//    }
//
//
//    /**
//     *  远程下载并上传文件
//     * @param strUrl 图片URL
//     * @param fileCategory 文件属性
//     * @param bizNo 业务编号
//     * @return
//     */
//    public static String downAndUpFileToFtp(String strUrl, String fileCategory, String bizNo) {
//
//        if (!StringUtils.isNotBlank(strUrl)){
//            return null;
//        }
//
//        File tmpDir = new File(simpleImagePath+ File.separator + "tmp");
//        if(!tmpDir.exists()){
//            tmpDir.mkdirs();
//        }
//
//        String file1Name = simpleImagePath + File.separator + "tmp" + File.separator + FileUtils.makeUnitKey() + ".";
//        File file = new File(file1Name + "png");
//
//        getImg(strUrl,file1Name + "png");
//
////        InputStream inputStream = FtpHelper.getInputStreamByGet(strUrl);
////        FtpHelper.saveData(inputStream, file);
//
//        niewjHttpResponse uploadRes;
//        try {
//            Map<String, String> otherParam = new HashMap<>();
//            otherParam.put("bizCode", NciicHelper.simpleImageFtpBizCode);
//            otherParam.put("fileCategory", fileCategory);
//            otherParam.put("serialNo", UUID.randomUUID().toString().replaceAll("-", ""));
//            otherParam.put("bizNo", bizNo);
//            uploadRes = niewjHttpClient.fileUpload(file, otherParam, imageFtpUrl + "/file/upload");
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(fileCategory + " upload image exception", e));
//        }
//
//        if (uploadRes.getHttpStatus() != 200)
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(fileCategory + " upload image exception : HttpStatueCode " + uploadRes.getHttpStatus()));
//
//        Map contentMap;
//        try {
//            contentMap = JSON.parseObject(uploadRes.getContent(), Map.class);
//        } catch (Exception e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(fileCategory + "JSON.parseObject error!"), e);
//        }
//
//        Object fileId = contentMap.get("fileId");
//        if (fileId == null || StringUtils.isBlank(fileId.toString()))
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg(fileCategory + "upload image success, but fileId incorrect, uploadResult:" + uploadRes));
//
//        return fileId.toString();
//    }
//
//    private static void getImg(String url, String fileName){
//        try {
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            try {
//                HttpGet httpGet = new HttpGet(url);
//                CloseableHttpResponse response = httpclient.execute(httpGet);
//                try {
//                    HttpEntity entity = response.getEntity();
//                    InputStream inStream = entity.getContent();
//                    FileOutputStream fw = new FileOutputStream(fileName, false);
//                    int b = inStream.read();
//                    while (b != -1) {
//                        fw.write(b);
//                        b = inStream.read();
//                    }
//                    fw.close();
//                    EntityUtils.consume(entity);
//                } finally {
//                    response.close();
//                }
//            }finally {
//                httpclient.close();
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 通过get请求得到读取器响应数据的数据流
//     * @param url URL
//     * @return
//     * @throws IOException
//     */
//    public static InputStream getInputStreamByGet(String url) {
//        try {
//            HttpURLConnection conn = (HttpURLConnection) new URL(url)
//                    .openConnection();
//            conn.setReadTimeout(5000);
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = conn.getInputStream();
//                return inputStream;
//            }
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg("通过get请求得到读取器响应数据的数据流失败"), e);
//        }
//        return null;
//    }
//
//    /**
//     * 将服务器响应的数据流存到本地文件
//     * @param is
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    public static void saveData(InputStream is, File file) {
//        try (BufferedInputStream bis = new BufferedInputStream(is);
//             BufferedOutputStream bos = new BufferedOutputStream(
//                     new FileOutputStream(file));) {
//            byte[] buffer = new byte[1024];
//            int len = -1;
//            while ((len = bis.read(buffer)) != -1) {
//                bos.write(buffer, 0, len);
//                bos.flush();
//            }
//        } catch (IOException e) {
//            throw new ThirdPartException(ResponseCodeEnum.INTERNAL_HANDLE_ERROR, StringMsg.fmtMsg("将服务器响应的数据流存到本地文件失败"), e);
//        }
//    }
//
//
////    public static void main(String[] args)
////    {
////        getImg("https://creditloan.oss-cn-hangzhou.aliyuncs.com/loanUser/2017/09/22/095749_4859.jpg","D:/1.jpg");
////    }
//
//}
