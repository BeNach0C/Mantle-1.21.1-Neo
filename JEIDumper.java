import java.lang.reflect.*;
import java.net.*;
import java.io.File;

public class JEIDumper {
    public static void main(String[] args) throws Exception {
        File f = new File("C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar");
        URLClassLoader loader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        String[] classes = {
            "mezz.jei.api.ingredients.IIngredientHelper",
            "mezz.jei.api.ingredients.IIngredientRenderer",
            "mezz.jei.api.gui.builder.IRecipeSlotBuilder",
            "mezz.jei.api.recipe.RecipeIngredientRole"
        };
        for(String c : classes) {
            System.out.println("--- " + c + " ---");
            try {
                Class<?> cls = loader.loadClass(c);
                if (cls.isEnum()) {
                    System.out.println("Enum constants:");
                    for(Object obj : cls.getEnumConstants()) {
                        System.out.println(obj.toString());
                    }
                }
                for (Method m : cls.getDeclaredMethods()) {
                    System.out.println(m.toString());
                }
            } catch (Exception e) {
                System.out.println("Failed: " + e.getMessage());
            }
        }
    }
}

