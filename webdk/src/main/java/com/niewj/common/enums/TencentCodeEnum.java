package com.niewj.common.enums;

/**
 * # 1. 枚举类示例
 * # 2. 枚举根据code获取某个枚举对象的值方法 getCodeEnum(code)
 * #     TencentCodeEnum.values()
 * 腾讯反欺诈响应状态代码code
 * Created by weijun.nie on 2017/6/8.
 */
public enum TencentCodeEnum {
    CODE_4000(4000, "请求参数非法"),
    CODE_4100(4100, "身份认证失败"),
    CODE_4101(4101, "未授权访问接口"),
    CODE_4102(4102, "未授权访问资源"),
    CODE_4103(4103, "未授权访问当前接口所操作的资源"),
    CODE_4104(4104, "密钥不存在"),
    CODE_4105(4105, "token错误"),
    CODE_4106(4106, "MFA校验失败"),
    CODE_4110(4110, "其他CAM鉴权失败"),
    CODE_4300(4300, "拒绝访问"),
    CODE_4400(4400, "超过配额"),
    CODE_4500(4500, "重放攻击"),
    CODE_4600(4600, "协议不支持"),
    CODE_5000(5000, "资源不存在"),
    CODE_5100(5100, "资源操作失败"),
    CODE_5200(5200, "资源购买失败"),
    CODE_5300(5300, "余额不足"),
    CODE_5400(5400, "部分执行成功"),
    CODE_5500(5500, "用户资质审核未通过"),
    CODE_6000(6000, "服务器内部错误"),
    CODE_6100(6100, "版本暂不支持"),
    CODE_6200(6200, "接口暂时无法访问");

    private int code;

    private String message;

    private TencentCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据指定的枚举值code，获取指定的枚举对象
     *
     * @param code
     * @return
     */
    public static TencentCodeEnum getCodeEnum(int code) {
        for (TencentCodeEnum e : TencentCodeEnum.values()) {
            if (code == e.getCode()) {
                return e;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getCodeEnum(4000).getMessage());
    }
}
