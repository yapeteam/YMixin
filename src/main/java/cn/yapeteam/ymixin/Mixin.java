package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.Objects;

import static cn.yapeteam.ymixin.YMixin.Logger;

@Getter
public class Mixin {
    private byte[] targetOldBytes = null;
    private final ClassNode source;
    private ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, ClassBytesProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = Objects.requireNonNull(cn.yapeteam.ymixin.annotations.Mixin.Helper.getAnnotation(source)).value();
        targetName = targetClass.getName().replace('.', '/');
        Logger.info("Loading mixin {} target class {}", source.name, targetName);
        int try_count = 0;
        while (target == null && try_count < 10) {
            try {
                targetOldBytes = provider.getClassBytes(targetClass);
                target = ASMUtils.node(targetOldBytes);
            } catch (Throwable ignored) {
                try_count++;
                Thread.sleep(500);
            }
        }
        if (target == null)
            Logger.error("Failed to load target class {} for mixin {}", targetClass.getName(), source.name);
        else
            Logger.info("Loaded target class {} for mixin {}, tries: {}", targetClass.getName(), source.name, try_count);
    }
}
