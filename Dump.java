import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.lang.reflect.Field;
public class Dump {
    public static void main(String[] args) {
        for (Field f : VertexFormatElement.class.getFields()) {
            System.out.println(f.getName());
        }
    }
}

