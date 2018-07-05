# ViewInject
此类库只实现了View的注解模块，实现布局加载注入，view注入，view监听事件注入；

#### 一、实现功能
* 1.布局注入
* 2.View注入
* 3.View监听事件注入

#### 二、实现思路
* 这三种实现的原理都是通过[IOC](https://baike.baidu.com/item/控制反转/1158025?fr=aladdin)思想来实现；
* 功能1和2通过反射获取到自定义的注解，解析注解，通过反射调用设置布局的setContentView和findViewById方法来实现；
* 功能3设置监听事件，除了需要使用反射获取信息外，还需要运用动态代理，来代理view对象，才能调用被我们自己写的事件注解的方法；


##### 三、布局注入的具体实现

```
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
    
```
从上面代码看，我们使用了一个LayoutInject的注解，此注解如下：

```
/**
 * Created by serenitynanian on 2018/7/5.
 * 用来表示---布局注入----的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LayoutInject {
    int value();
}
```
此注解使用时，只能在类上面使用，里面需要一个参数是布局的id，我们通过反射所传的对象，拿到此注解上的布局id，通过反射调用setContentView方法，即可实现；

##### 四、View注入的具体实现
```
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
```
View的注入原理和布局注入的原理几乎一样，使用了一个自定义注解ViewInject：

```

/**
 * Created by serenitynanian on 2018/7/5.
 * View注入-----findViewById（）
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ViewInject {
    int value();
}
```
通过注解取得view控件的id，通过反射调用findViewById方法即可实现；


##### 五、监听事件注入的具体实现

```
 /**
     * 监听事件监听注入
     * @param object
     */
    public static void listenerInject(Object object) {

        Class<?> aClass = object.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
        	//不能使用下面的原因，我们这个方法是为了实现不止onClickListen监听，而是为了实现全部的，做到可扩展，
            //不能直接将要实现的监听固定写死；
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
```

事件监听大概有23种（可能更多），我们为了实现好的扩展性，不能将想要的监听事件固定在我们上面的代码中，这就需要我们动态获取想要实现的监听，因此我们需要一个注解ListenerBaseInfo来表明实现监听的基本信息；

```
ListenerBaseInfo.class注解

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

```
ListenerBaseInfo注解来说明监听事件的基本信息，比如onClickListener事件，这个注解被用来注解其他监听事件的注解，来告知其他注解到底自己要实现哪种监听的；

___

被这个注解OnClickInject注解的方法：是用来表明view需要的onClickLister监听事件

```
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
```

在事件监听注解的实现方法里，我们也是反射获取到被事件监听注解（OnClickInject）注解的方法，然后获取注解的类类型【 Class<? extends Annotation> aClass1 = annotation.annotationType() 】，再通过反射获取到此注解的注解（ListenerBaseInfo），拿到监听事件的基本信息；再通过上面得到的那个类类型，反射获取到value值，也就是需要绑定监听事件的viewId数组，通过这个数组，反射获取到view对象，通过动态代理的方式，将此view的对象的setOnXXXListener方法代理；

代理如下：

```
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
```
#### 六、[类库代码和使用Demo已经上传到github，如需参考请点击](https://github.com/wangluAndroid/ViewInject/tree/master)


    