import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

public class InspectJar {
    public static void main(String[] args) throws Exception {
        String jarPath = "C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar";
        try (ZipFile zipFile = new ZipFile(new File(jarPath))) {
            extract(zipFile, "mezz/jei/api/constants/VanillaTypes.class");
            extract(zipFile, "mezz/jei/api/recipe/RecipeIngredientRole.class");
            extract(zipFile, "mezz/jei/api/gui/builder/IRecipeSlotBuilder.class");
            extract(zipFile, "mezz/jei/api/gui/ingredient/ICraftingGridHelper.class");
        }
    }
    
    private static void extract(ZipFile zip, String name) throws Exception {
        ZipEntry entry = zip.getEntry(name);
        if (entry != null) {
            File out = new File(new File(name).getName());
            try (InputStream in = zip.getInputStream(entry); FileOutputStream fos = new FileOutputStream(out)) {
                in.transferTo(fos);
            }
        }
    }
}

