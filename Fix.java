import java.nio.file.*;

public class Fix {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("D:/Tinkers 1.21.1/Mantle/src/main/java/slimeknights/mantle/client/model/util/MantleItemLayerModel.java");
        String text = new String(Files.readAllBytes(p));
        
        text = text.replace("builder.add(buildQuad(quadBuilder, quadConsumer, Direction.SOUTH, color, emissivity,\n                0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),\n                1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),\n                1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),\n                0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0()));\n      return quadBuilder.bakeQuad();",
                            "return buildQuad(quadBuilder, quadConsumer, Direction.SOUTH, color, emissivity,\n                0, 0, 8.5f / 16f, sprite.getU0(), sprite.getV1(),\n                1, 0, 8.5f / 16f, sprite.getU1(), sprite.getV1(),\n                1, 1, 8.5f / 16f, sprite.getU1(), sprite.getV0(),\n                0, 1, 8.5f / 16f, sprite.getU0(), sprite.getV0());");
                            
        text = text.replace("List<BakedQuad> builder = new ArrayList<>();", "ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();");
        
        Files.write(p, text.getBytes());
    }
}
