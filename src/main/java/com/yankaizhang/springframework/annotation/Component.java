package com.yankaizhang.springframework.annotation;


import java.lang.annotation.*;

/**
 * 元注解
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
public @interface Component {
    String value() default "";
}