package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.util.typed.TypedMap;

/** Loadable for ingredients, handling Forge ingredients */
public enum IngredientLoadable implements Loadable<Ingredient> {
  ALLOW_EMPTY,
  DISALLOW_EMPTY;

  @Override
  public Ingredient convert(JsonElement element, String key, TypedMap context) {
    return (this == ALLOW_EMPTY ? Ingredient.CODEC : Ingredient.CODEC_NONEMPTY).parse(com.mojang.serialization.JsonOps.INSTANCE, element).getOrThrow(com.google.gson.JsonSyntaxException::new);
  }

  @Override
  public JsonElement serialize(Ingredient object) {
    if (object.isEmpty() && this == DISALLOW_EMPTY) {
      throw new IllegalArgumentException("Ingredient cannot be empty");
    }
    return (this == ALLOW_EMPTY ? Ingredient.CODEC : Ingredient.CODEC_NONEMPTY).encodeStart(com.mojang.serialization.JsonOps.INSTANCE, object).getOrThrow(IllegalStateException::new);
  }

  @Override
  public Ingredient decode(FriendlyByteBuf buffer, TypedMap context) {
    return Ingredient.CONTENTS_STREAM_CODEC.decode((net.minecraft.network.RegistryFriendlyByteBuf) buffer);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, Ingredient object) {
    Ingredient.CONTENTS_STREAM_CODEC.encode((net.minecraft.network.RegistryFriendlyByteBuf) buffer, object);
  }
}
