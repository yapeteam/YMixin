package cn.yapeteam.ymixin.operation.test;

public class test {
    static class CustomLoader extends ClassLoader {
        public Class<?> load(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {
        // Mapper.setMode(Mapper.Mode.None);
        // Transformer transformer = new Transformer((name) -> ResourceManager.readStream(InjectOperation.class.getResourceAsStream("/" + name.getName().replace('.', '/') + ".class")));
        // transformer.addMixin(source.class);
        // byte[] bytes = transformer.transform().get("cn.yapeteam.loader.mixin.operation.test.target");
        // Files.write(new File("target.class").toPath(), bytes);
        // new CustomLoader().load(bytes).getMethod("func").invoke(null);
        //  Mixin.Helper.getAnnotation(ASMUtils.node(Files.readAllBytes(Paths.get("Loader/target/classes/cn/yapeteam/loader/mixin/operation/test/source.class")))).value();
    }
}
