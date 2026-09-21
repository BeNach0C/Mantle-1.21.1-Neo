import java.nio.file.*;
import java.util.zip.*;
import java.io.*;

public class ClassReader {
    public static void main(String[] args) throws Exception {
        try (ZipFile zip = new ZipFile("C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar")) {
            for(String name : new String[]{"mezz/jei/api/gui/builder/IRecipeSlotBuilder.class", "mezz/jei/api/recipe/RecipeIngredientRole.class", "mezz/jei/api/ingredients/IIngredientHelper.class", "mezz/jei/api/ingredients/IIngredientRenderer.class"}) {
                ZipEntry entry = zip.getEntry(name);
                if (entry != null) {
                    System.out.println("FOUND: " + name);
                    InputStream is = zip.getInputStream(entry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    is.transferTo(baos);
                    byte[] bytes = baos.toByteArray();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<bytes.length; i++) {
                        if(bytes[i] >= 32 && bytes[i] <= 126) sb.append((char)bytes[i]);
                        else sb.append('.');
                    }
                    System.out.println(sb.toString());
                } else {
                    System.out.println("NOT FOUND: " + name);
                }
            }
        }
    }
}

