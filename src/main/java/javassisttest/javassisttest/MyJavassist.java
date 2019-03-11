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

    public static void premain(String args, Instrumentation instrumentation){




        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if(className!=null&&className.equals("javassisttest/javassisttest/service")){
                    ClassPool classPool =new ClassPool();
                    classPool.insertClassPath(new LoaderClassPath(loader));
                    try {
                        CtClass ctClass=classPool.get("javassisttest.javassisttest.service");


                        CtMethod ctMethod=ctClass.getDeclaredMethods()[0];
                        //ctMethod.insertBefore("System.out.println(System.currentTimeMillis());");
                        ctMethod.instrument(new ExprEditor() {
                            public void edit(MethodCall m) throws CannotCompileException {
                                m.replace("{ long stime = System.currentTimeMillis(); $_ = $proceed($$);System.out.println(\""
                                        + m.getClassName() + "." + m.getMethodName()
                                        + " cost:\" + (System.currentTimeMillis() - stime) + \" ms\");}");
                            }
                        });

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
