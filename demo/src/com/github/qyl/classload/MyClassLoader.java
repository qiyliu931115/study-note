package com.github.qyl.classload;

import java.io.*;

public class MyClassLoader extends ClassLoader{

    private String byteCodePath;

    public MyClassLoader(String name, ClassLoader parent, String byteCodePath) {
        this.byteCodePath = byteCodePath;
    }

    public MyClassLoader(ClassLoader parent, String byteCodePath) {
        super(parent);
        this.byteCodePath = byteCodePath;
    }

    public MyClassLoader(String byteCodePath) {
        this.byteCodePath = byteCodePath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            //获取字节码文件完整路径
            String fileName = byteCodePath + name + ".class";
            //获取输入流
            bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName));
            //获取输出流
            byteArrayOutputStream = new ByteArrayOutputStream();
            //读入数据并写出的过程
            int len;
            byte[] data= new byte[1024];
            while ((len = bufferedInputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }

            //获取内存中完整的字节数组的数据
            byte[] bytes = byteArrayOutputStream.toByteArray();
            //调用defineClass() 将字节数组的数据装欢为class的实例
            Class<?> aClass = defineClass(null, bytes, 0, bytes.length);
            return aClass;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return super.findClass(name);
    }
}
