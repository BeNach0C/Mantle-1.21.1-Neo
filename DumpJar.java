import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DumpJar {
    public static void main(String[] args) throws Exception {
        String jarPath = "C:\\Users\\BeNach0\\.gradle\\caches\\modules-2\\files-2.1\\mezz.jei\\jei-1.21.1-common-api\\19.53.0.425\\b0a69cbd59278a2133a39061e2fde767b7a16443\\jei-1.21.1-common-api-19.53.0.425.jar";
        try (ZipFile zipFile = new ZipFile(new File(jarPath));
             PrintWriter out = new PrintWriter(new FileWriter("jar_classes.txt"))) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    out.println(entry.getName());
                }
            }
        }
    }
}

