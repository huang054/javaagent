package javassisttest.javassisttest;

public class service {

    public int say(String s){
        System.out.println("hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     //   throw new RuntimeException("message fail");
        return 1;
    }
}
