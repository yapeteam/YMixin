package cn.yapeteam.ymixin;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class YMixin {
    public static ClassProvider classProvider;
    public static ClassBytesProvider classBytesProvider;
    public static Logger Logger;

    public static void init(ClassProvider provider, ClassBytesProvider bytesProvider, @Nullable Logger logger) {
        classProvider = provider;
        classBytesProvider = bytesProvider;
        if (logger == null) Logger = new Logger() {
            @Override
            public void error(String str, Object... o) {
                System.err.println(str + " " + Arrays.toString(o));
            }

            @Override
            public void info(String str, Object... o) {
                System.out.println(str + " " + Arrays.toString(o));
            }

            @Override
            public void warn(String str, Object... o) {
                System.out.println(str + " " + Arrays.toString(o));
            }

            @Override
            public void success(String str, Object... o) {
                System.out.println(str + " " + Arrays.toString(o));
            }

            @Override
            public void exception(Throwable ex) {
                ex.printStackTrace();
            }
        };
        else Logger = logger;
    }
}
