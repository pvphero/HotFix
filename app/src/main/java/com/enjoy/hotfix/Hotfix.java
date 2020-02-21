package com.enjoy.hotfix;

import android.app.Application;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Hotfix {


    /**
     * 安装补丁包
     * 1、获取到当前应用的PathClassloader;
     * <p>
     * 2、反射获取到DexPathList属性对象pathList;
     * <p>
     * 3、反射修改pathList的dexElements
     * 3.1、把补丁包patch.dex转化为Element[]  (patch)
     * 3.2、获得pathList的dexElements属性（old）
     * 3.3、patch+old合并，并反射赋值给pathList的dexElements
     */
    public static void installPatch(Application application, File patch) {

        if (!patch.exists()) {
            return;
        }
        //获取到当前应用的PathClassloader;


        try {
            //2、反射获取到DexPathList属性对象pathList;
            ClassLoader classLoader = application.getClassLoader();
            if (Build.VERSION.SDK_INT >= 24 ) {
                try {
                    classLoader = NewClassLoaderInjector.inject(application, classLoader);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            Field pathListField = findField(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);

            /**
             * 3、反射修改pathList的dexElements
             */
            //3.1 把补丁包patch.dex转化为Element[]  (patch)
            // 3.1.1、获得到makePathElements方法
            Method makePathElementsMethod = findMethod(pathList, "makePathElements",
                    List.class, File.class, List.class);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            ArrayList<File> patchs = new ArrayList<>();
            patchs.add(patch);

            File filesDir = application.getFilesDir();

            Object[] patchElements = (Object[]) makePathElementsMethod.invoke(null,
                    patchs, filesDir, suppressedExceptions);

            //3.2、获得pathList的dexElements属性（old）
            Field dexElementsFiled = findField(pathList, "dexElements");
            Object[] dexElements = (Object[]) dexElementsFiled.get(pathList);

            //3.3、patch+old合并，并反射赋值给pathList的dexElements
            // 创建一个新的Element数组，长度就是 patchElements+dexElements

            // patchElements.getClass(): Element[].class, Element.class
            Object[] newElements = (Object[]) Array.newInstance(patchElements.getClass().getComponentType(),
                    patchElements.length + dexElements.length);

            System.arraycopy(patchElements,0,newElements,0,patchElements.length);
            System.arraycopy(dexElements,0,newElements,patchElements.length,dexElements.length);

            dexElementsFiled.set(pathList,newElements);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> cls = instance.getClass();
        while (cls != null) {
            try {
                Method method = cls.getDeclaredMethod(name, parameterTypes);
                if (method != null) {
                    // 设置访问权限
                    method.setAccessible(true);
                    return method;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            cls = cls.getSuperclass();
        }
        throw new NoSuchMethodException(instance.getClass().getSimpleName() + " not found filed:" + name);
    }

    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class<?> cls = instance.getClass();
        while (cls != null) {
            try {
                Field field = cls.getDeclaredField(name);
                if (field != null) {
                    // 设置访问权限
                    field.setAccessible(true);
                    return field;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            cls = cls.getSuperclass();
        }
        throw new NoSuchFieldException(instance.getClass().getSimpleName() + " not found filed:" + name);
    }
}
