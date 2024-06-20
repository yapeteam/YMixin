package cn.yapeteam.ymixin.annotations;

import cn.yapeteam.ymixin.utils.ASMUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm_9_2.tree.AnnotationNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Modify {
    String method();

    String desc();

    String replacepath();

    String replacementfunc();

    String funcdesc();



    class Helper {
        public static Modify fromNode(AnnotationNode annotation) {
            return new Modify() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Modify.class;
                }

                @Override
                public String method() {
                    return ASMUtils.getAnnotationValue(annotation, "method");
                }

                @Override
                public String desc() {
                    return ASMUtils.getAnnotationValue(annotation, "desc");
                }

                @Override
                public String replacepath() {
                    return ASMUtils.getAnnotationValue(annotation, "replacepath");
                }

                @Override
                public String replacementfunc() {
                    return ASMUtils.getAnnotationValue(annotation, "replacementfunc");
                }

                @Override
                public String funcdesc() {
                    return ASMUtils.getAnnotationValue(annotation, "funcdesc");
                }
            };
        }


        public static boolean isAnnotation(@NotNull AnnotationNode node) {
            return node.desc.contains(ASMUtils.slash(Modify.class.getName()));
        }

        public static boolean hasAnnotation(@NotNull MethodNode node) {
            return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(Helper::isAnnotation);
        }

        public static @Nullable Modify getAnnotation(MethodNode node) {
            if (!hasAnnotation(node)) return null;
            return fromNode(node.visibleAnnotations.stream().filter(Helper::isAnnotation).findFirst().orElse(null));
        }
    }
}
