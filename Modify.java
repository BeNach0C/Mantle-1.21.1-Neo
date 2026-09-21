import java.util.regex.*;
import java.nio.file.*;

public class Modify {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("D:/Tinkers 1.21.1/Mantle/src/main/java/slimeknights/mantle/client/model/util/MantleItemLayerModel.java");
        String text = new String(Files.readAllBytes(p));
        
        // QuadBakingVertexConsumer instantiation
        text = text.replace("QuadBakingVertexConsumer quadBuilder = new QuadBakingVertexConsumer(builder::add);", "QuadBakingVertexConsumer quadBuilder = new QuadBakingVertexConsumer();");
        text = text.replace("QuadBakingVertexConsumer.Buffered quadBuilder = new QuadBakingVertexConsumer.Buffered();", "QuadBakingVertexConsumer quadBuilder = new QuadBakingVertexConsumer();");
        
        // return quadBuilder.getQuad()
        text = text.replace("return quadBuilder.getQuad();", "return quadBuilder.bakeQuad();"); // wait, getQuad was replaced or not?
        
        // buildSideQuad return type
        text = text.replace("private static void buildSideQuad", "private static net.minecraft.client.renderer.block.model.BakedQuad buildSideQuad");
        text = text.replace("public static void buildQuad", "public static net.minecraft.client.renderer.block.model.BakedQuad buildQuad");
        
        // in buildQuad add return builder.bakeQuad();
        text = text.replace("putVertex(consumer, side, x3, y3, z3, u3, v3, color, luminosity);\n  }", "putVertex(consumer, side, x3, y3, z3, u3, v3, color, luminosity);\n    return builder.bakeQuad();\n  }");
        
        // in buildSideQuad add return before buildQuad
        text = text.replace("buildQuad(builder, consumer, (side.getAxis() == Axis.Y ? side.getOpposite() : side),", "return buildQuad(builder, consumer, (side.getAxis() == Axis.Y ? side.getOpposite() : side),");
        
        // add to builder in getQuadsForSprite
        text = text.replace("buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v + off, uEnd - uStart, emissivity);", "builder.add(buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v + off, uEnd - uStart, emissivity));");
        text = text.replace("buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v+off, uEnd-uStart, emissivity);", "builder.add(buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, uStart, v+off, uEnd-uStart, emissivity));");
        text = text.replace("buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u + off, vStart, vEnd - vStart, emissivity);", "builder.add(buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u + off, vStart, vEnd - vStart, emissivity));");
        text = text.replace("buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u+off, vStart, vEnd-vStart, emissivity);", "builder.add(buildSideQuad(quadBuilder, quadConsumer, facing, color, sprite, u+off, vStart, vEnd-vStart, emissivity));");
        
        text = text.replace("buildQuad(quadBuilder, quadConsumer, Direction.NORTH, color, emissivity,", "builder.add(buildQuad(quadBuilder, quadConsumer, Direction.NORTH, color, emissivity,");
        text = text.replace("1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1());", "1, 0, 7.5f / 16f, sprite.getU1(), sprite.getV1()));");
        
        text = text.replace("buildQuad(quadBuilder, quadConsumer, Direction.SOUTH, color, emissivity,", "builder.add(buildQuad(quadBuilder, quadConsumer, Direction.SOUTH, color, emissivity,");
        text = text.replace("0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0());", "0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0()));");
        
        Files.write(p, text.getBytes());
    }
}
