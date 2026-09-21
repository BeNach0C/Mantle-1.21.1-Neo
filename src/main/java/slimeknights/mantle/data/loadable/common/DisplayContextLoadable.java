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
    try {
      return ItemDisplayContext.valueOf(name.toUpperCase(java.util.Locale.ROOT));
    } catch (IllegalArgumentException e) {
      throw new JsonSyntaxException("Invalid ItemDisplayContext: " + name);
    }
  }

  @Override
  public String getString(ItemDisplayContext object) {
    return object.name().toLowerCase(java.util.Locale.ROOT);
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

