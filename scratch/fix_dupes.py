import os
import re

packets = [
    "OpenLecternBookPacket",
    "OpenNamedBookPacket",
    "SwingArmPacket",
    "UpdateHeldPagePacket",
    "UpdateLecternPagePacket"
]

base_dir = r"D:\Tinkers 1.21.1\Mantle\src\main\java\slimeknights\mantle\network\packet"

for p in packets:
    path = os.path.join(base_dir, p + ".java")
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    content = re.sub(r"  public static final Type<.*?> ID = new Type.*?;[\r\n]+", "", content)
    content = re.sub(r"  public static final StreamCodec<.*?> CODEC = StreamCodec.*?;[\r\n]+", "", content)
    content = re.sub(r"  @Override[\r\n]+  public Type<.*?> type\(\) \{[\r\n]+    return ID;[\r\n]+  \}[\r\n]+", "", content)
    
    match = re.search(r"(public class " + p + r".*?\{)", content, re.DOTALL)
    class_def = match.group(1)
    
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

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

