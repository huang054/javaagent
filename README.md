# javaagent
字节码修改
vm  -javaagent:D:\ChromeCoreDownloads\ssittest\target\ssittest-1.0-SNAPSHOT.jar=tom -Dfile.encoding=UTF-8
http://www.javassist.org/html/index.html
http://www.javassist.org/tutorial/tutorial2.html#limit
https://www.jianshu.com/p/43424242846b
javaagent javassist的学习
最近看了下javaagent，感觉很强大，零侵入式的通过字节码修改代码，当然也遇见了很多坑
本文也只是粗略的介绍下要做到分布式链路追踪这样感觉还有很长的一段路程
![方法](https://github.com/huang054/javaagent/blob/master/javassist1.jpg)
![运行main](https://github.com/huang054/javaagent/blob/master/javassist2.png)
![效果图](https://github.com/huang054/javaagent/blob/master/javassist3.png)
那么是怎么实现的了，其实是在类加载器拿到类的信息，然后通过修改字节码并且返回字节码这样无感知的修改了运行效果
开始我们来编写一个premain，这个是javaagent的核心方法效果如下图，注意这里的Instrumentation是关键，我们要通过Instrumentation实现获取信息
![peimain](https://github.com/huang054/javaagent/blob/master/javassist4.png)
然后我们要通过Instrumentation的addTransformer方法来实现我们需要的功能
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist5.png)
然后通过className判断是否是我们需要修改字节码的类，ClassPool从类加载器找倒这个类，这里如何要监听tomcat jboss这样的容器需要对
容器的类加载器非常熟悉，可以从runtime获取运行中的信息，包括运行的参数已经运行的类方法
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist6.png)
可以通过CtMethod.getMethodInfo()获取方法的信息getParameterTypes()获取参数的类型可以得到一个数组
注意数组的第一个是this就是对象自己，可以自己处理下
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist7.png)
比如你要打印时间你就可以insert after 自己的代码，但是这种是在代码块里，并不能计算出方法执行的时间
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist8.png)
这里怎么办了，我们这里只能在原来方法通过copy一个新方法然后在新方法里拼接字节码实现我们需要的功能然后返回字节码
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist9.png)
然后通过maven打包，记得要指定premain的路径生成MANIFEST.MF文件
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist10.png)
最后在我们运行的main方法vm中加上javaagent参数，还有pom文件要引入javassist的jar包
![addTransformer](https://github.com/huang054/javaagent/blob/master/javassist11.png)
