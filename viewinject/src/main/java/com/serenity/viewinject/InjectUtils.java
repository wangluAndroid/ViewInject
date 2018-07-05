package com.serenity.viewinject;

import android.view.View;

import com.serenity.viewinject.annotation.LayoutInject;
import com.serenity.viewinject.annotation.ListenerBaseInfo;
import com.serenity.viewinject.annotation.ViewInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by serenitynanian on 2018/7/5.
 */

public class InjectUtils {

    protected static boolean debug = false ;

    /**
     * 布局注入方法
     * @param object  布局所在的上下文
     */
    public static void layoutInject(Object object) {
        Class<?> aClass = object.getClass();
        LayoutInject annotation = aClass.getAnnotation(LayoutInject.class);
        if (null != annotation) {
            //1.获取layout_id
            int layoutId = annotation.value();
            //2.反射调用setContentView(layoutId)
            try {
                //不能使用getDeclaredMethod（这个方法只能获取本类中声明的方法)获取setContentView此方法
                // 因为setContentView方法在父类中，使用getDeclaredMethod无法获得
                Method setContentView = aClass.getMethod("setContentView", int.class);
                setContentView.invoke(object, layoutId);
                LogUtils.e("----- Layout  Inject success -----");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * View注入
     * @param object
     * 实现findViewById
     */
    public static void viewInject(Object object) {
        Class<?> aClass = object.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {

            ViewInject annotation = field.getAnnotation(ViewInject.class);
            if (null != annotation) {
                //1.view的id
                int viewId = annotation.value();
                //2.反射调用findViewById
                try {
                    Method findViewById = aClass.getMethod("findViewById", int.class);
                    View view = (View) findViewById.invoke(object, viewId);
                    //3.给此field赋值
                    field.setAccessible(true);
                    field.set(object, view);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 监听事件监听注入
     * @param object
     */
    public static void listenerInject(Object object) {

        Class<?> aClass = object.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
//            OnClickInject annotation = method.getAnnotation(OnClickInject.class);
            //1.获得此方法上所有的注解数组
            Annotation[] annotations = method.getAnnotations();
            if (null != annotations) {
                for (Annotation annotation : annotations) {
                    //2.单独解析一个注解，看这个注解里面是否有ListenerBaseInfo注解
                    //得到注解类，解析这个注解类上的注解----比如：OnClickInject.class
                    Class<? extends Annotation> aClass1 = annotation.annotationType();
                    ListenerBaseInfo annotation1 = aClass1.getAnnotation(ListenerBaseInfo.class);
                    if (null == annotation1) {
                        continue;
                    }
                    //3.走到这一步，说明annotation这个注解上面有ListenerBaseInfo注解，是被监听事件注解的方法

                    //下面得到被注解的监听事件的 基本信息
                    String setOnXXXListenerName = annotation1.setOnXXXListenerName();
                    Class<?> onXXXListenerType = annotation1.onXXXListenerType();
                    String onXXXMethodName = annotation1.onXXXMethodName();

                    //4.查找需要添加事件的view
                    try {
                        Method value = aClass1.getDeclaredMethod("value");
                        int[] viewIdArray = (int[]) value.invoke(annotation);
                        for (int viewId : viewIdArray) {
                            Method findViewById = aClass.getMethod("findViewById", int.class);
                            View view = (View) findViewById.invoke(object, viewId);
                            if (null != view) {
                                Method setOnXXXListenerNameMethod = view.getClass().getMethod(setOnXXXListenerName, onXXXListenerType);
                                //5.使用动态代理  代理被我们注解的方法
                                ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(method, object,onXXXMethodName);
                                Object proxyObject = Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{onXXXListenerType}, listenerInvocationHandler);
                                setOnXXXListenerNameMethod.invoke(view, proxyObject);
                            }
                        }



                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


//                    如果使用下面的方法，只会调用个空方法 ，无法正常回调到被我们注解的方法
//                    try {
//                        method.invoke(object,onXXXListenerType.newInstance());
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    } catch (InstantiationException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }

    }



    public static void setDebug(boolean isDebug) {
        LogUtils.isDebug = isDebug;
    }

    /**
     *             //获取field的基本属性
     //            System.out.println(field.getName());
     //            System.out.println(field.getClass());
     //            System.out.println(field.getDeclaringClass());
     //            System.out.println(field.getType());
     //            System.out.println(field.getGenericType());
     //            System.out.println(field.getModifiers());
     //            System.out.println(Modifier.isPrivate(field.getModifiers()));
     */
}
