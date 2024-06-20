package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.operation.Operation;
import cn.yapeteam.ymixin.operation.impl.InjectOperation;
import cn.yapeteam.ymixin.operation.impl.ModifyOperation;
import cn.yapeteam.ymixin.operation.impl.OverwriteOperation;
import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cn.yapeteam.ymixin.YMixin.Logger;

@Getter
public class Transformer {
    private final ClassBytesProvider provider;
    private final ArrayList<cn.yapeteam.ymixin.Mixin> mixins;
    private final ArrayList<Operation> operations;
    private final Map<String, byte[]> oldBytes = new HashMap<>();

    public Transformer(ClassBytesProvider classBytesProvider) {
        this.provider = classBytesProvider;
        this.mixins = new ArrayList<>();
        this.operations = new ArrayList<>();
        operations.add(new InjectOperation());
        operations.add(new OverwriteOperation());
        operations.add(new ModifyOperation());
    }

    public void addMixin(ClassNode node) throws Throwable {
        mixins.add(new Mixin(node, provider));
    }

    public void addMixin(byte[] bytes) throws Throwable {
        addMixin(ASMUtils.node(bytes));
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
