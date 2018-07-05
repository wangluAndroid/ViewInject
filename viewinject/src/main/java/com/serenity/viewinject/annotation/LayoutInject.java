package com.serenity.viewinject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by serenitynanian on 2018/7/5.
 * 用来表示---布局注入----的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LayoutInject {
    int value();
}
