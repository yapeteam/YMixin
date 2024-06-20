package cn.yapeteam.ymixin;

public interface ClassBytesProvider {
    byte[] getClassBytes(Class<?> clazz) throws Throwable;
}
