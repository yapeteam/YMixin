package cn.yapeteam.ymixin.operation.impl;

import cn.yapeteam.ymixin.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.operation.Operation;
import cn.yapeteam.ymixin.utils.DescParser;
import cn.yapeteam.ymixin.utils.Mapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.stream.Collectors;

import static cn.yapeteam.ymixin.YMixin.Logger;

public class OverwriteOperation implements Operation {
    @Override
    public void dispose(Mixin mixin) {
        ClassNode source = mixin.getSource();
        ClassNode target = mixin.getTarget();
        List<MethodNode> replacements = source.methods.stream()
                .filter(Overwrite.Helper::hasAnnotation)
                .collect(Collectors.toList());
        for (MethodNode replacement : replacements) {
            Overwrite info = Overwrite.Helper.getAnnotation(replacement);
            if (info == null) continue;
            MethodNode targetMethod = Operation.findTargetMethod(target.methods, mixin.getTargetName(), info.method(), info.desc());
            if (targetMethod == null) {
                Logger.error("No method found: {} in {}", Mapper.mapWithSuper(mixin.getTargetName(), info.method(), info.desc(), Mapper.Type.Method) + DescParser.mapDesc(info.desc()), target.name);
                return;
            }
            targetMethod.instructions = replacement.instructions;
        }
    }
}
