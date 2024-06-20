package cn.yapeteam.ymixin.operation;


import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.ymixin.Mixin;
import cn.yapeteam.ymixin.utils.DescParser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Field;
import java.util.List;

public interface Operation {
    int ASM_API = Opcodes.ASM9;

    void dispose(Mixin mixin);

    static boolean isLoadOpe(int opcode) {
        for (Field field : Opcodes.class.getFields())
            if (field.getName().endsWith("LOAD"))
                try {
                    if ((int) field.get(null) == opcode)
                        return true;
                } catch (Throwable ignored) {
                }
        return false;
    }

    static boolean isStoreOpe(int opcode) {
        for (Field field : Opcodes.class.getFields())
            if (field.getName().endsWith("STORE"))
                try {
                    if ((int) field.get(null) == opcode)
                        return true;
                } catch (Throwable ignored) {
                }
        return false;
    }

    static MethodNode findTargetMethod(List<MethodNode> list, String owner, String name, String desc) {
        name = Mapper.mapWithSuper(owner, name, desc, Mapper.Type.Method);
        desc = DescParser.mapDesc(desc);
        String finalName = name;
        String finalDesc = desc;
        return list.stream().filter(m -> m != null && m.name.equals(finalName) && m.desc.equals(finalDesc)).findFirst().orElse(null);
    }
}
