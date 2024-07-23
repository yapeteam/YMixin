package cn.yapeteam.ymixin.example;

import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.YMixin;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

// 这里是使用YMixin的示例代码，主要是展示如何使用YMixin进行字节码修改，并通过自定义ClassLoader加载修改后的字节码，或者调用JVMTI进行热修改。
public class Main {
    private static byte[] readStream(InputStream inStream) throws Exception {
        val outStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    private static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(byte[] bytes) {
            try {
                val defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                defineClassMethod.setAccessible(true);
                return (Class<?>) defineClassMethod.invoke(this, null, bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void main(String[] args) throws Throwable {
        // 这里的init方法用来初始化YMixin，传入三个个参数，第一个参数是获取目标类的回调函数，第二个参数是获取目标类字节码的回调函数，第三个参数是日志的回调函数
        YMixin.init(
                clazz -> {
                    try {
                        return Class.forName(clazz);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }, clazz -> {
                    try {
                        return readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/" + clazz.getName().replace(".", "/") + ".class")));
                    } catch (Exception e) {
                        return null;
                    }
                }, null,
                null, //这里的 MappingReader 设置为 null，因为目标类没有经过混淆
                null //这里的 MappingContent 设置为 null ，因为目标类没有经过混淆
        );
        //若 MappingContent 不为 null，且 MappingReader 为 null，YMixin将自动选择SrgMappingReader
        //只要 MappingContent 为 null YMixin就会认为无需进行重映射
        //实例化一个Transformer，用于修改字节码
        Transformer transformer = new Transformer();
        transformer.addMixin(MixinTargetClass.class);
        // transformer.transform(); 返回的Map中，key为目标类的全限定名，value为字节码
        // 生成的字节码可以通过自定义ClassLoader加载，或者调用JVMTI进行热修改
        Class<?> targetClass = Objects.requireNonNull(new CustomClassLoader().defineClass(transformer.transform().get("cn.yapeteam.ymixin.example.TargetClass")));
        Method targetMethod = targetClass.getMethod("run");
        System.out.println("Example:");
        targetMethod.invoke(targetClass.newInstance());
    }
}
