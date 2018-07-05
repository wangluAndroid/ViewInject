package com.serenity.viewinject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by serenitynanian on 2018/7/5.
 * 定义在注解上的注解
 * 此注解来说明----->监听事件具备的基本信息
 *
 * button.setOnClickListener(new View.OnClickListener() {
 *      @Override
 *      public void onClick(View v) {
 *
 *      }
 * });
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ListenerBaseInfo {
    /**
     * 设置监听事件的名字
     * @return
     */
    String setOnXXXListenerName();

    /**
     * 监听事件的类型
     * @return
     */
    Class<?> onXXXListenerType() ;

    /**
     * 监听事件被回调的方法名
     * @return
     */
    String onXXXMethodName();
}
