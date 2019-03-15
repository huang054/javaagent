package javassisttest.javassisttest;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.util.List;

public class MyJavassist {
    final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

    public static void premain(String args, Instrumentation instrumentation){





        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                /*if(className.startsWith("java") || className.startsWith("sun")){
                    return null;
                }*/
                /**
                 * 注意包名不可以java和sun开头
                 */
                if(className!=null&&className.equals("javassisttest/javassisttest/service")){
                    ClassPool classPool =new ClassPool();
                    classPool.insertClassPath(new LoaderClassPath(loader));
                    try {
                        CtClass ctClass=classPool.get("javassisttest.javassisttest.service");
                       /* Class<?> clazz = Class.forName("javassisttest.javassisttest.service");
                        Method[] declaredMethods = clazz.getDeclaredMethods();
                        Class<?> returnType = declaredMethods[0].getReturnType();
*/                     RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                        List<String> arguments = runtimeMXBean.getInputArguments();
                        System.out.println("arguments:"+arguments);
                        System.out.println("在哪个方法调用"+System.getProperty("sun.java.command"));
                       // Class<?>[] parameterTypes = declaredMethods[0].getParameterTypes();
                        CtMethod ctMethod=ctClass.getDeclaredMethods()[0];
                        ctMethod.getMethodInfo();
                        MethodInfo info =ctMethod.getMethodInfo();

                        CodeAttribute codeAttribute = info.getCodeAttribute();
                        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                        String[] variableNames = new String[ctMethod.getParameterTypes().length];
                        System.out.println("返回类型:"+ctMethod.getReturnType());
                        System.out.println("方法:"+ctMethod.getLongName());
                        int staticIndex = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                        for (int i = 0; i < variableNames.length; i++)  {
                            variableNames[i] = attr.variableName(i + staticIndex);
                            if(variableNames[i].equals("this")){
                                variableNames[i] = attr.variableName(i + (++staticIndex) );

                            }
                        }
                       /* for (int i = 0; i < variableNames.length; i++){
                            System.out.println(variableNames[i]+":"+variableNames[i]);
                        }*/
                      //  System.out.println(variableNames.toString());
                        //ctMethod.insertBefore("System.out.println(System.currentTimeMillis());");
                        String methodName = ctMethod.getName();
                        String newMethodName = methodName + "$old";// 新定义一个方法叫做比如sayHello$old
                        ctMethod.setName(newMethodName);// 将原来的方法名字修改
                        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
                        ctMethod.addCatch("{ System.out.println(\"We have caught the error via Transformer\"); "

                                + "throw $e; }", etype);
                        // 创建新的方法，复制原来的方法，名字为原来的名字

                        CtMethod newMethod = CtNewMethod.copy(ctMethod, methodName, ctClass, null);
                        String type = ctMethod.getReturnType().getName();
                        // 构建新的方法体
                        StringBuilder bodyStr = new StringBuilder();
                        bodyStr.append("{");
                        for (int i = 0; i < variableNames.length; i++){
                            int j=i+1;

                            bodyStr.append("System.out.println(\"==============param: " +variableNames[i]+":\"+$"+j+"+\"" + " ==============\");");
                        }
                      // bodyStr.append("System.out.println(\"==============param: " +"\"+$1+\"" + " ==============\");");
                        bodyStr.append("System.out.println(\"==============Enter Method: " + className + "." + methodName + " ==============\");");
                        bodyStr.append(prefix);
                        if(!"void".equals(type)) {
                            bodyStr.append(type + " result = ");
                                }
                        bodyStr.append(newMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                    //    bodyStr.append("return ($R);\n");
                        bodyStr.append(postfix);
                        bodyStr.append("System.out.println(\"==============Exit Method: " + className + "." + methodName + " Cost:\" +(endTime - startTime) +\"ms " + "===\");");
                        bodyStr.append("System.out.println(\"==============return result: " +"\"+result+\"" + " ==============\");");
                        if(!"void".equals(type)) {
                            bodyStr.append("return result;\n");
                        }
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
