package cn.yapeteam.ymixin.operation.impl;

import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.ymixin.Mixin;
import cn.yapeteam.ymixin.annotations.Modify;
import cn.yapeteam.ymixin.operation.Operation;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.yapeteam.ymixin.YMixin.Logger;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.TypeReference.NEW;


public class ModifyOperation implements Operation {
    @Override
    public void dispose(Mixin mixin) {
        ClassNode source = mixin.getSource();
        ClassNode target = mixin.getTarget();
        List<MethodNode> modifications = source.methods.stream()
                .filter(Modify.Helper::hasAnnotation)
                .collect(Collectors.toList());
        for (MethodNode modification : modifications) {
            Modify info = Modify.Helper.getAnnotation(modification);
            if (info == null) continue;
            MethodNode targetMethod = Operation.findTargetMethod(target.methods, mixin.getTargetName(), info.method(), info.desc());
            if (targetMethod == null) {
                Logger.error("No method found: {} in {}", Mapper.mapWithSuper(mixin.getTargetName(), info.method(), info.desc(), Mapper.Type.Method) + info.desc(), target.name);
                return;
            }

            // Find the LdcInsnNode that loads the constant 3.0D onto the operand stack
            LdcInsnNode ldc = null;
            for (int i = 0; i < targetMethod.instructions.size(); ++i) {
                AbstractInsnNode x = targetMethod.instructions.get(i);

                if (x instanceof LdcInsnNode) {
                    LdcInsnNode t = (LdcInsnNode) x;

                    if (t.cst instanceof Double && ((Double) t.cst) == 3.0) {
                        ldc = t;
                    }
                }
            }
            if (ldc == null) {
                Logger.error("LdcInsnNode not found in method " + info.method());
                return;
            }

            try {
                Class<?> clazz = Class.forName(info.replacepath().replace("/", "."));
                // Check if the method exists
                if (Arrays.stream(clazz.getMethods()).anyMatch(method -> method.getName().equals(info.replacementfunc()))) {
                    TypeInsnNode newTypeInsn = new TypeInsnNode(NEW, info.replacepath());
                    InsnNode dupInsn = new InsnNode(DUP);
                    LdcInsnNode reachLdc = new LdcInsnNode(3.0);
                    MethodInsnNode constructorInsn =
                            new MethodInsnNode(INVOKESPECIAL, info.replacepath(), "<init>", "(F)V", false);
                    VarInsnNode storeInsn = new VarInsnNode(ASTORE, 999);
                    VarInsnNode loadInsn = new VarInsnNode(ALOAD, 999);
                    MethodInsnNode getRangeInsn =
                            new MethodInsnNode(INVOKEVIRTUAL, info.replacepath(), info.method(), info.funcdesc(), false);
                    // Replace the ldc instruction
                    targetMethod.instructions.insertBefore(ldc, newTypeInsn);
                    targetMethod.instructions.insert(newTypeInsn, dupInsn);
                    targetMethod.instructions.insert(dupInsn, reachLdc);
                    targetMethod.instructions.insert(reachLdc, constructorInsn);
                    targetMethod.instructions.insert(constructorInsn, storeInsn);
                    targetMethod.instructions.insert(storeInsn, loadInsn);
                    targetMethod.instructions.insert(loadInsn, getRangeInsn);
                    targetMethod.instructions.remove(ldc);
                } else {
                    Logger.info("Method " + info.replacementfunc() + " does not exist in class " + info.replacepath());
                }
            } catch (ClassNotFoundException e) {
                Logger.info("Class " + info.replacepath() + " does not exist");
            }
        }
    }
}
