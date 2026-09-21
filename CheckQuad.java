import java.lang.reflect.*;
public class CheckQuad {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer");
        for (Constructor<?> c : clazz.getConstructors()) {
            System.out.println(c);
        }
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m);
        }
    }
}
