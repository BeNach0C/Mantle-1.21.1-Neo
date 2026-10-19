package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.mapping.EnumMapLoadable;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Map;

/** Special loadable for display contexts due to the Forge weirdness in {@link ItemDisplayContext} */
public enum DisplayContextLoadable implements slimeknights.mantle.data.loadable.primitive.StringLoadable<ItemDisplayContext> {
  INSTANCE;

  @Override
  public ItemDisplayContext parseString(String name, String key, TypedMap context) {
    for (ItemDisplayContext c : ItemDisplayContext.values()) {
      if (c.getSerializedName().equalsIgnoreCase(name) || c.name().equalsIgnoreCase(name)) {
        return c;
      }
    }
    switch (name) {
      case "tconstruct:casting_table":
      case "tconstruct:fluid_cannon":
      case "tconstruct:thrown":
        return ItemDisplayContext.FIXED;
      case "tconstruct:table":
      case "tconstruct:casting_basin":
      case "tconstruct:melter":
        return ItemDisplayContext.NONE;
    }
    if (name.contains(":")) {
      return ItemDisplayContext.NONE;
    }
    throw new JsonSyntaxException("Invalid ItemDisplayContext: " + name);
  }

  @Override
  public String getString(ItemDisplayContext object) {
    return object.getSerializedName();
  }

  @Override
  public ItemDisplayContext decode(FriendlyByteBuf buffer, TypedMap context) {
    return buffer.readEnum(ItemDisplayContext.class);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, ItemDisplayContext value) {
    buffer.writeEnum(value);
  }

  public <V> Loadable<Map<ItemDisplayContext,V>> mapWithValues(Loadable<V> valueLoadable, int minSize) {
    return new EnumMapLoadable<>(ItemDisplayContext.class, this, valueLoadable, minSize);
  }
}

