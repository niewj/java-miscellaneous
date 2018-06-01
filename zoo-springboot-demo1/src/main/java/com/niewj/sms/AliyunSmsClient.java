package com.niewj.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import com.niewj.db.ConnectionUtil;
import com.niewj.db.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

/**
 * 阿里云短信调用接口客户端-收费
 * Created by weijun.nie on 2018/5/29.
 */
public class AliyunSmsClient {

    private static Logger logger = LoggerFactory.getLogger(AliyunSmsClient.class);

    static String access_key_id; // db
    static String access_key_secret; // db
    static String sign_name; // db
    static String template_code; // db

    // 初始化配置信息
    static {
        // 1.连接池工具
        ConnectionUtil util = ConnectionUtil.DbUtilHolder.getInstance();
        Connection connection = util.getConnection();

        // 2.查询表数据
        DbUtil dbUtil = new DbUtil();
        List<DbUtil.DbKeyValue> list = dbUtil.getConfig(connection);

        // 3. 初始化内容
        logger.info(new Gson().toJson(list));
        System.out.println(new Gson().toJson(list));
        for (DbUtil.DbKeyValue kv : list) {
            switch (kv.getProp_name()) {
                case "access_key_id":
                    access_key_id = kv.getProp_value();
                    break;
                case "access_key_secret":
                    access_key_secret = kv.getProp_value();
                    break;
                case "sign_name":
                    sign_name = kv.getProp_value();
                    break;
                case "template_code":
                    template_code = kv.getProp_value();
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Template1 d1 = new Template1("loveU", "loveU", "loveU", "#loveU#");
        String d1Json = new Gson().toJson(d1);
        String mobileNo = "173xxxxx527";
        try {
            smsSend(mobileNo, d1Json);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private static void smsSend(String mobileNo, String dataGson) throws ClientException {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）

        //替换成你的AK
//        final String access_key_id = access_key_id;//你的accessKeyId,参考本文档步骤2
//        final String access_key_secret = access_key_secret;//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", access_key_id, access_key_secret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(mobileNo);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(sign_name);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(template_code);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(dataGson);
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        System.out.println(new Gson().toJson(sendSmsResponse));
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //请求成功
            System.out.println("====================OK-======================");
        }
        // {"requestId":"C1E7FF77-3F61-4637-A9FF-88061F5D569F","code":"isv.AMOUNT_NOT_ENOUGH","message":"账户余额不足"}
        // {"requestId":"35AA6B10-6BAC-46EC-8B19-909933ED19C9","bizId":"437714627582082579^0","code":"OK","message":"OK"}
    }


    /**
     * 短信模板-1
     */
    private static class Template1 {
        private String temperature;
        private String airQuality;
        private String windPower;
        private String uname;

        public String getTemperature() {
            return temperature;
        }

        public String getAirQuality() {
            return airQuality;
        }

        public String getWindPower() {
            return windPower;
        }

        public String getUname() {
            return uname;
        }

        public Template1() {
        }

        public Template1(String temperature, String airQuality, String windPower, String uname) {
            this.temperature = temperature;
            this.airQuality = airQuality;
            this.windPower = windPower;
            this.uname = uname;
        }
    }
}
