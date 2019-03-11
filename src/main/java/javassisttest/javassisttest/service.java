package javassisttest.javassisttest;

public class service {

    public void say(){
        System.out.println("hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
