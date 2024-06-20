package cn.yapeteam.ymixin;

public interface Logger {
    void error(String str, Object... o);

    void info(String str, Object... o);

    void warn(String str, Object... o);

    void success(String str, Object... o);

    void exception(Throwable ex);
}
