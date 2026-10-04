import os
import re

packets = [
    "OpenLecternBookPacket",
    "OpenNamedBookPacket",
    "SwingArmPacket",
    "UpdateHeldPagePacket",
    "UpdateInventoryPagePacket",
    "UpdateLecternPagePacket"
]

base_dir = r"D:\Tinkers 1.21.1\Mantle\src\main\java\slimeknights\mantle\network\packet"

for p in packets:
    path = os.path.join(base_dir, p + ".java")
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # Imports
    content = re.sub(r"import net\.neoforged\.neoforge\.network\.NetworkEvent\.Context;", "import net.neoforged.neoforge.network.handling.IPayloadContext;\nimport net.minecraft.network.codec.StreamCodec;\nimport slimeknights.mantle.Mantle;\nimport net.minecraft.world.entity.player.Player;\nimport net.minecraft.server.level.ServerPlayer;\n", content)
    content = re.sub(r"import net\.neoforged\.neoforge\.network\.NetworkEvent;", "import net.neoforged.neoforge.network.handling.IPayloadContext;\nimport net.minecraft.network.codec.StreamCodec;\nimport slimeknights.mantle.Mantle;\nimport net.minecraft.world.entity.player.Player;\nimport net.minecraft.server.level.ServerPlayer;\n", content)
    
    # Class declaration replacement
    print(f"Processing {p}")
    match = re.search(r"(public class " + p + r".*?\{)", content, re.DOTALL)
    if not match:
        print(f"Failed to find class definition in {p}")
        continue
    class_def = match.group(1)
    
    # Convert PascalCase to snake_case for the ID
    snake_case = re.sub(r"(?<!^)(?=[A-Z])", "_", p).lower()
    snake_case = snake_case.replace("_packet", "")
    
    new_class_def = class_def + f"""
  public static final Type<{p}> ID = new Type<>(Mantle.getResource("{snake_case}"));
  public static final StreamCodec<RegistryFriendlyByteBuf, {p}> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), {p}::new);

  @Override
  public Type<{p}> type() {{
    return ID;
  }}
"""
    content = content.replace(class_def, new_class_def)

    # handleThreadsafe replacement
    content = content.replace("handleThreadsafe(Context context)", "handleThreadsafe(IPayloadContext context)")
    content = content.replace("handleThreadsafe(NetworkEvent.Context context)", "handleThreadsafe(IPayloadContext context)")
    content = content.replace("context.getSender()", "(context.player() instanceof ServerPlayer sp ? sp : null)")
    
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
