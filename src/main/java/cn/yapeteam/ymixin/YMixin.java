package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.map.IMappingReader;
import cn.yapeteam.ymixin.map.impl.SrgMappingReader;
import cn.yapeteam.ymixin.utils.Mapper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class YMixin {
    public static ClassProvider classProvider;
    public static ClassBytesProvider classBytesProvider;
    public static Logger Logger;
    public static IMappingReader mappingReader;
    public static boolean hasMapping;

    public static void init(ClassProvider provider, ClassBytesProvider bytesProvider) {
        init(provider, bytesProvider, null, null, null);
    }

    public static void init(ClassProvider provider, ClassBytesProvider bytesProvider, Logger logger) {
        init(provider, bytesProvider, logger, null, null);
    }

    public static void init(ClassProvider provider, ClassBytesProvider bytesProvider, Logger logger, String mappingContent) {
        init(provider, bytesProvider, logger, null, mappingContent);
    }

    public static void init(ClassProvider provider, ClassBytesProvider bytesProvider, @Nullable Logger logger, @Nullable IMappingReader mappingReader, @Nullable String mappingContent) {
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
        if (mappingContent != null) {
            hasMapping = true;
            if (mappingReader == null)
                YMixin.mappingReader = new SrgMappingReader();
            Mapper.readMapping(mappingContent);
        } else hasMapping = false;
    }
}
