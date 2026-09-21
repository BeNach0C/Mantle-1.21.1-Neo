import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

public class ReflectClass {
    public static void main(String[] args) throws Exception {
        String jarPath = "C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar";
        URL url = new File(jarPath).toURI().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{url})) {
            printClass(loader, "mezz.jei.api.constants.VanillaTypes");
            printClass(loader, "mezz.jei.api.recipe.RecipeIngredientRole");
            printClass(loader, "mezz.jei.api.gui.builder.IRecipeSlotBuilder");
            printClass(loader, "mezz.jei.api.gui.ingredient.ICraftingGridHelper");
        }
    }
    
    private static void printClass(ClassLoader loader, String name) {
        try {
            Class<?> clazz = loader.loadClass(name);
            System.out.println("Class: " + clazz.getName());
            System.out.println("Is Enum: " + clazz.isEnum());
            System.out.println("Is Interface: " + clazz.isInterface());
            for (Field f : clazz.getDeclaredFields()) {
                System.out.println("  Field: " + f.getType().getName() + " " + f.getName());
            }
            for (Method m : clazz.getDeclaredMethods()) {
                System.out.println("  Method: " + m.getReturnType().getName() + " " + m.getName());
                for (Class<?> p : m.getParameterTypes()) {
                    System.out.println("    Param: " + p.getName());
                }
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error loading " + name + ": " + e);
        }
    }
}

