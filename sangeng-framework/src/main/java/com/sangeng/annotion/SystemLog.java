package com.sangeng.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//本注解生命周期包含java文件、字节码文件和内存中的字节码，因为要在运行时动态获取信息
@Retention(RetentionPolicy.RUNTIME)
//本注解加载在方法上
@Target(ElementType.METHOD)
public @interface SystemLog {
    String businessName();
}
