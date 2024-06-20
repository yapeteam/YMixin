package cn.yapeteam.ymixin.annotations;

import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.utils.ASMUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.AnnotationNode;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.lang.annotation.Target;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mixin {
    Class<?> value();

    class Helper {
        public static Mixin fromNode(AnnotationNode annotation) {
            return new Mixin() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Mixin.class;
                }

                @Override
                public Class<?> value() {
                    String className = ((Type) ASMUtils.getAnnotationValue(annotation, "value")).getClassName();
                    return YMixin.classProvider.get(className);
                }
            };
        }
        public static boolean isAnnotation(@NotNull AnnotationNode node) {
            return node.desc.contains(ASMUtils.slash(Mixin.class.getName()));
        }

        public static boolean hasAnnotation(@NotNull ClassNode node) {
            return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(Mixin.Helper::isAnnotation);
        }

        public static @Nullable Mixin getAnnotation(ClassNode node) {
            if (!hasAnnotation(node)) return null;
            return fromNode(node.visibleAnnotations.stream().filter(Mixin.Helper::isAnnotation).findFirst().orElse(null));
        }
    }
}
