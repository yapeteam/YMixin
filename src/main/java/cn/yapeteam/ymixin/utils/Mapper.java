package cn.yapeteam.ymixin.utils;

import cn.yapeteam.ymixin.YMixin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Mapper {
    @Getter
    @AllArgsConstructor
    public static class Map {
        private final String owner, name, desc, obf;
        private final Type type;
    }

    public enum Type {
        Class, Field, Method
    }

    /**
     * friendly→obf
     **/
    @Getter
    private static final ArrayList<Map> mappings = new ArrayList<>();

    public static void readMapping(String content) {
        YMixin.mappingReader.readMapping(content, getMappings());
    }

    @Getter
    private static final java.util.Map<String, String> cache = new HashMap<>();

    /**
     * @param owner Class except
     * @param name  Name
     * @param desc  Class except
     * @param type  Class, Field or Method
     * @return ObfName
     */
    public static String map(@Nullable String owner, String name, @Nullable String desc, Type type) {
        if (!YMixin.hasMapping) return name;
        if (owner != null) owner = owner.replace('.', '/');
        String identifier = owner + "." + name + " " + desc;
        String value = cache.get(identifier);
        if (value != null) return value;
        String finalOwner = owner;
        val map = mappings.stream().filter(m ->
                m.type == type &&
                        (type == Type.Class || finalOwner == null || m.owner.equals(finalOwner.replace('.', '/'))) &&
                        (m.name.equals(name.replace('.', '/'))) &&
                        (type == Type.Class || desc == null || m.desc.equals(desc))
        ).findFirst().orElse(new Map(owner, name, "null", name, type));
        String result = map.obf;
        cache.put(identifier, result);
        return result;
    }

    public static String mapWithSuper(String owner, String name, String desc, Type type) {
        if (!YMixin.hasMapping) return name;
        owner = owner.replace('.', '/');
        String identifier = owner + "." + name + " " + desc;
        String value = cache.get(identifier);
        if (value != null) return value;
        java.util.Map<String, Map> owners = new HashMap<>();
        mappings.stream().filter(m ->
                m.type == type && m.name.equals(name) && (desc == null || m.desc == null || desc.equals(m.desc))
        ).forEach(m -> owners.put(m.owner, m));
        String mappedOwner = map(null, owner, null, Type.Class);
        Class<?> theClass = YMixin.classProvider.get(mappedOwner);
        List<Class<?>> classes = new ArrayList<>();
        Class<?> superClz = theClass;
        while (superClz != Object.class) {
            if (superClz != null) {
                classes.add(superClz);
                classes.addAll(Arrays.asList(superClz.getInterfaces()));
                superClz = superClz.getSuperclass();
            } else break;
        }
        for (Class<?> clz : classes) {
            java.util.Map.Entry<String, Map> entry = owners.entrySet().stream()
                    .filter(m -> map(null, m.getKey(), null, Type.Class).equals(clz.getName().replace('.', '/')))
                    .findFirst().orElse(null);
            if (entry != null) {
                cache.put(identifier, entry.getValue().obf);
                return entry.getValue().obf;
            }
        }
        return name;
    }

    public static String mapMethodWithSuper(String owner, String name, String desc) {
        return mapWithSuper(owner, name, desc, Type.Method);
    }

    public static String mapFieldWithSuper(String owner, String name, String desc) {
        return mapWithSuper(owner, name, desc, Type.Field);
    }

    public static String getObfClass(String name) {
        return map(null, name, null, Type.Class);
    }
}
