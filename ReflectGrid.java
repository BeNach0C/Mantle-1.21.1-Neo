import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

public class ReflectGrid {
    public static void main(String[] args) throws Exception {
        String jarPath = "C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar";
        URL url = new File(jarPath).toURI().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{url})) {
            printClass(loader, "mezz.jei.api.gui.ingredient.ICraftingGridHelper");
            printClass(loader, "mezz.jei.api.gui.builder.IRecipeLayoutBuilder");
        }
    }
    
    private static void printClass(ClassLoader loader, String name) {
        try {
            Class<?> clazz = loader.loadClass(name);
            System.out.println("Class: " + clazz.getName());
            for (Method m : clazz.getDeclaredMethods()) {
                System.out.println("  Method: " + m.getReturnType().getName() + " " + m.getName());
                for (Class<?> p : m.getParameterTypes()) {
                    System.out.println("    Param: " + p.getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading " + name + ": " + e);
        }
    }
}

