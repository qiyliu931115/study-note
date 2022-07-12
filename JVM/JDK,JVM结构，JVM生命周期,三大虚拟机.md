JDK - JRE -JVM

前端编译器 javac编译器 .java 转成.class
前端javac编译器是转成字节码 后端jit编译器转成机器指令


![img_1.png](img/img_10.png)


类加载器-》运行时数据区 《-》 本地方法接口 JNI <-本地方法库

-》执行引擎


![img_2.png](img/img_11.png)

执行引擎：

解释器（interpreter） 
JIT即时编译器 （JIT compiler）
垃圾回收器（GC）



![img.png](img/img13.png)

二次编译 源文件编译成字节码文件，再通过JIT转成机器指令，对于热点 频繁调用的指令，JIT会缓存起来

![img_1.png](img/img_12.png)

基于栈式架构（Java虚拟机） 
跨平台性好，指令集小，但是指令多
执行性能比寄存器差

和寄存器架构（PC，Android）
寄存器 的指令更少

JVM启动-执行-结束

启动是通过类引导器创建初始类完成

![img.png](img/img14.png)

初始类不是object类，object也是引导类加载器加载的

![img.png](img/img15.png)

最早的是sun classic vm

![img.png](img/img16.png)

![img_1.png](img/img17.png)

hotspot 
热点探测 计数器探测 多处引用 被循环调用的方法
将其编译成机器指令缓存到方法区的Code Cache
![img.png](img/img18.png)

![img_1.png](img/img_19.png)

![img.png](img/img20.png)