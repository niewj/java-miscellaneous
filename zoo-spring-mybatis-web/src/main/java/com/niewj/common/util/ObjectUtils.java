package com.niewj.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * Created by niewj on 2017/12/20.
 */
public class ObjectUtils {

    /**
     * 判断该对象是否所有字段都是空的?
     * 返回ture表示所有属性为null  返回false表示不是所有属性都是null
     *  demo： Data{} -true
     *          Data{"name":"niewj"} - false
     *
     * @param obj                 判断的对象
     * @param ignoreFieldsByComma 需要忽略的字段名[可以包含空格的逗号分隔字段列表]
     * @return
     * @throws Exception
     */
    public static boolean isAllFieldNull(Object obj, String ignoreFieldsByComma) throws Exception {
        boolean flag = true; // 初始化非全null

        Class stuCla = (Class) obj.getClass();// 得到类对象
        Field[] fields = stuCla.getDeclaredFields();//得到属性集合
        for (Field f : fields) {

            // 设置属性是可以访问的(私有的也可以)
            f.setAccessible(true);
            // 得到此属性的值
            Object val = f.get(obj);
            //只要有1个属性不为空,那么就不是所有的属性值都为空
            if (StringUtils.isNotBlank(ignoreFieldsByComma) && ArrayUtils.contains(ignoreFieldsByComma.replaceAll("[\\s*\t\n\r]", "").split(","), f.getName())) {
                continue;
            } else {
                // 不在忽略列表，再做判断
                if (val != null) {
                    flag = false; // 有一个字段非null，改变为false, 否则最终返回true
                    break;
                }
            }
        }

        return flag;
    }

}
