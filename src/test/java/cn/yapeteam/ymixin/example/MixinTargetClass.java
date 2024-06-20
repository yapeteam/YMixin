package cn.yapeteam.ymixin.example;

import cn.yapeteam.ymixin.annotations.*;

// 这里的Mixin注解用于指定目标类
@Mixin(TargetClass.class)
public class MixinTargetClass {
    /**
     * 这里的Inject注解用于注入代码到目标方法中
     * method参数用于指定目标方法的名称
     * desc参数用于指定目标方法的描述符
     * target参数用于指定注入代码的位置，可以是 HEAD、RETURN 或者 org.objectweb.asm.Opcodes 中的其他代表操作的字段名
     * 注入代码可以是任意的Java代码，包括方法调用、赋值语句、if语句等等
     */

    @Inject(method = "targetMethod1", desc = "()V", target = @Target("HEAD"))
    public void test1() {
        System.out.println("Hello, TargetClass!");
    }


    @Inject(method = "targetMethod2", desc = "()V", target = @Target("RETURN"))
    public void test2() {
        System.out.println("Hello, TargetClass!");
    }

    @Inject(method = "targetMethod3", desc = "(Ljava/lang/String;)V", target = @Target("HEAD"))
    public void test3(
            @Local(source = "str0", index = 1) String str0
            // 这里的Local注解用于获取目标方法的 局部变量/参数 的值
            // source参数用于指定局部变量/参数的名称
            // index参数用于指定参数的索引，可以使用recaf等工具获取
    ) {
        System.out.println("Hello, str: " + str0.hashCode() + "!");
    }

    @Shadow
    private String prefix;
    // 这里的Shadow注解用于访问目标类的成员变量的值

    @Inject(method = "targetMethod4", desc = "()V", target = @Target("HEAD"))
    public void test4() {
        System.out.println("Hello, prefix: " + prefix.hashCode() + "!");
        prefix = "[Prefix from Mixin]";
    }

    @Inject(
            method = "targetMethod5",
            desc = "()V",
            target = @Target(
                    value = "INVOKEVIRTUAL",
                    target = "java/io/PrintStream.println(Ljava/lang/String;)V",
                    shift = Target.Shift.BEFORE,
                    ordinal = 1
            )
    )
    // 这里的Target注解用于指定注入代码的位置。可以使用recaf等工具获取
    // value参数的值为org.objectweb.asm.Opcodes中的代表操作的字段名，用于指定目标操作
    // target参数的值为目标操作的描述符
    // shift用于指定注入代码的位置
    // ordinal用于指定目标操作的序号，在含有多个相同操作的情况下，指定目标操作的位置。从0开始
    // 这里的ordinal参数的值为1，表示在targetMethod5的第二个println调用之前注入代码
    public void test5() {
        System.out.println("Hello, PrintStream.println!");
    }

    @Inject(
            method = "targetMethod6",
            desc = "()V",
            target = @Target(
                    value = "PUTFIELD",
                    target = "cn/yapeteam/ymixin/example/TargetClass.prefix Ljava/lang/String;",
                    shift = Target.Shift.BEFORE
            )
    )
    // 这里演示了如何注入代码到目标类的成员变量的赋值操作之前
    public void test6() {
        System.out.println("Hello, Field prefix!");
    }
}
