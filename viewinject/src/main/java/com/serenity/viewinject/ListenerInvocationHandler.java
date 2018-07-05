package com.serenity.viewinject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by serenitynanian on 2018/7/5.
 * 动态代理----点击事件方法
 */

public class ListenerInvocationHandler implements InvocationHandler {
    private String callbackMehtodName;
    private Method realMethod ;
    private Object object ;

    public ListenerInvocationHandler(Method realMethod, Object object,String callbackMehtodName) {
        this.realMethod = realMethod;
        this.object = object;
        this.callbackMehtodName = callbackMehtodName ;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(method.getName().equals(callbackMehtodName)){
            return realMethod.invoke(object,args);

        }
        return null ;

    }
}
