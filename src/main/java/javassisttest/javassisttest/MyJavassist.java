package javassisttest.javassisttest;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class MyJavassist {
    final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

    public static void premain(String args, Instrumentation instrumentation){





        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if(className.startsWith("java") || className.startsWith("sun")){
                    return null;
                }
                /**
                 * 注意包名不可以java和sun开头
                 */
                if(className!=null&&className.equals("javassisttest/javassisttest/service")){
                    ClassPool classPool =new ClassPool();
                    classPool.insertClassPath(new LoaderClassPath(loader));
                    try {
                        CtClass ctClass=classPool.get("javassisttest.javassisttest.service");


                        CtMethod ctMethod=ctClass.getDeclaredMethods()[0];
                        //ctMethod.insertBefore("System.out.println(System.currentTimeMillis());");
                        String methodName = ctMethod.getName();
                        String newMethodName = methodName + "$old";// 新定义一个方法叫做比如sayHello$old
                        ctMethod.setName(newMethodName);// 将原来的方法名字修改

                        // 创建新的方法，复制原来的方法，名字为原来的名字
                        CtMethod newMethod = CtNewMethod.copy(ctMethod, methodName, ctClass, null);

                        // 构建新的方法体
                        StringBuilder bodyStr = new StringBuilder();
                        bodyStr.append("{");
                        bodyStr.append("System.out.println(\"==============Enter Method: " + className + "." + methodName + " ==============\");");
                        bodyStr.append(prefix);
                        bodyStr.append(newMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                        bodyStr.append(postfix);
                        bodyStr.append("System.out.println(\"==============Exit Method: " + className + "." + methodName + " Cost:\" +(endTime - startTime) +\"ms " + "===\");");
                        bodyStr.append("}");

                        newMethod.setBody(bodyStr.toString());// 替换新方法
                        ctClass.addMethod(newMethod);// 增加新方法

                        return ctClass.toBytecode();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        });
    }
}
