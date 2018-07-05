package com.serenity.viewinject.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by serenitynanian on 2018/7/5.
 * 监听事件的注册
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ListenerBaseInfo(setOnXXXListenerName = "setOnClickListener",
        onXXXListenerType = View.OnClickListener.class,
        onXXXMethodName = "onClick")
public @interface OnClickInject {
    int[] value();
}
