package cn.yapeteam.ymixin.example;

// 这是目标类
public class TargetClass {
    @SuppressWarnings("unused")
    public void run() {
        System.out.println("targetMethod1():");
        targetMethod1();
        System.out.println("targetMethod2():");
        targetMethod2();
        System.out.println("targetMethod3():");
        targetMethod3("Hello, World!");
        System.out.println("targetMethod4():");
        targetMethod4();
        System.out.println("targetMethod5():");
        targetMethod5();
        System.out.println("targetMethod6():");
        targetMethod6();
    }

    public void targetMethod1() {
        System.out.println("Hello, World!");
    }

    public void targetMethod2() {
        System.out.println("Hello, World!");
    }

    public void targetMethod3(String str) {
        System.out.println(str);
    }

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private String prefix = "[Prefix]";

    public void targetMethod4() {
        System.out.println(prefix + "Hello, World!");
    }

    public void targetMethod5() {
        System.out.println("Hello, World1!");
        int i = 2;
        while (i < 5) {
            System.out.println("Hello, World" + i + "!");
            i++;
        }
        System.out.println("Hello, World5!");
    }

    public void targetMethod6() {
        System.out.println("Hello, World1!");
        System.out.println("Hello, World2!");
        prefix = "[Prefix2]";
        System.out.println("Hello, World3!");
    }
}
