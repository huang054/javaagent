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
![运行main](https://github.com/huang054/javaagent/blob/master/javassist2.jpg)
![效果图](https://github.com/huang054/javaagent/blob/master/javassist3.jpg)

