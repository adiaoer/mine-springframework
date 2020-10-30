package com.yankaizhang.springframework.aop.support;

import com.yankaizhang.springframework.aop.AopProxy;
import com.yankaizhang.springframework.aop.SpringProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Aop工具类
 */
public class AopUtils {

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 判断是否为Aop代理对象
     */
    public static boolean isAopProxy(Object object) {
        return (object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) ||
                object.getClass().getName().contains(CGLIB_CLASS_SEPARATOR)));
    }

    /**
     * 判断代理类型是否为Jdk动态代理
     */
    public static boolean isJdkDynamicProxy(Object object) {
        return (object instanceof SpringProxy && Proxy.isProxyClass(object.getClass()));
    }

    /**
     * 判断代理类型是否为CGLib动态代理
     */
    public static boolean isCglibProxy(Object object) {
        return (object instanceof SpringProxy &&
                object.getClass().getName().contains(CGLIB_CLASS_SEPARATOR));
    }

    /**
     * 获取代理对象的目标类对象
     */
    public static Class<?> getAopTarget(Object object){
        Class<?> result = null;
        // 注意：存在代理嵌套的情况，所以需要加循环

        try {
            if (isJdkDynamicProxy(object)){
                // Jdk
                Field h = object.getClass().getSuperclass().getDeclaredField("h");
                h.setAccessible(true);
                AopProxy jdkDynamicAopProxy = (AopProxy) h.get(object);

                Field config = jdkDynamicAopProxy.getClass().getDeclaredField("config");
                config.setAccessible(true);
                AdvisedSupport advisedSupport = (AdvisedSupport) config.get(jdkDynamicAopProxy);

                Field targetClass = advisedSupport.getClass().getDeclaredField("targetClass");
                targetClass.setAccessible(true);
                result = (Class<?>) targetClass.get(advisedSupport);

            }else if (isCglibProxy(object)){
                // CGLib
                // 这里其实就是在一层层的深入获取属性
                Field h = object.getClass().getDeclaredField("CGLIB$CALLBACK_0");
                h.setAccessible(true);

                Object obj = h.get(object);

                Field advisedSupport = obj.getClass().getDeclaredField("config");
                advisedSupport.setAccessible(true);
                Object temp = advisedSupport.get(obj);  // 获取真正的advisedSupport对象
                // 获取目标类的Class
                Field targetClass = temp.getClass().getDeclaredField("targetClass");
                targetClass.setAccessible(true);
                result = (Class<?>) targetClass.get(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}