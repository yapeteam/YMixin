package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.operation.Operation;
import cn.yapeteam.ymixin.operation.impl.InjectOperation;
import cn.yapeteam.ymixin.operation.impl.OverwriteOperation;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.ClassMapper;
import lombok.Getter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.yapeteam.ymixin.YMixin.*;

@Getter
public class Transformer {
    private final ArrayList<cn.yapeteam.ymixin.Mixin> mixins;
    private final ArrayList<Operation> operations;
    private final Map<String, byte[]> oldBytes = new HashMap<>();

    public Transformer() {
        this.mixins = new ArrayList<>();
        this.operations = new ArrayList<>();
        operations.add(new InjectOperation());
        operations.add(new OverwriteOperation());
    }

    public void addMixin(byte[] bytes) throws Throwable {
        addMixin(ASMUtils.node(bytes));
    }

    public void addMixin(Class<?> clazz) throws Throwable {
        addMixin(classBytesProvider.getClassBytes(clazz));
    }

    public void addMixin(ClassNode node) throws Throwable {
        Shadow.Helper.processShadow(node);
        ClassMapper.map(node, ClassMapper.MapMode.Mixed);
        mixins.add(new Mixin(node, classBytesProvider));
    }

    public Map<String, byte[]> transform() {
        Map<String, byte[]> classMap = new HashMap<>();
        oldBytes.clear();
        for (Mixin mixin : mixins) {
            if (mixin.getTarget() == null) {
                Logger.warn("Mixin {} has no target class, skipping.", mixin.getSource().name);
                continue;
            }
            String name = mixin.getTarget().name.replace('/', '.');
            oldBytes.put(name, mixin.getTargetOldBytes());
            for (Operation operation : operations)
                operation.dispose(mixin);
            try {
                byte[] class_bytes = ASMUtils.rewriteClass(mixin.getTarget());
                classMap.put(name, class_bytes);
            } catch (Throwable e) {
                Logger.error("Failed to transform class " + name, e);
                Logger.exception(e);
            }
        }
        return classMap;
    }
}
