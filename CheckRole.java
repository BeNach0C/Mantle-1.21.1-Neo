import java.nio.file.*;
import java.util.zip.*;

public class CheckRole {
    public static void main(String[] args) throws Exception {
        try (ZipFile zip = new ZipFile("C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar")) {
            System.out.println("Role: " + (zip.getEntry("mezz/jei/api/recipe/Role.class") != null));
            System.out.println("RecipeIngredientRole: " + (zip.getEntry("mezz/jei/api/recipe/RecipeIngredientRole.class") != null));
        }
    }
}

