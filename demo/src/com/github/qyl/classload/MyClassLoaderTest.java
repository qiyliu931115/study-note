package com.github.qyl.classload;

public class MyClassLoaderTest {

    public static void main(String[] args) {
        MyClassLoader myClassLoader = new MyClassLoader("f:/");
        try {
            Class<?> demo = myClassLoader.loadClass("Demo");

            System.out.println("类加载器为" +  demo.getClassLoader());
            System.out.println("类加载器的父类为" +  demo.getClassLoader().getParent());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
