package slimeknights.mantle.recipe.helper;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/** Ingredient serializer factory using loadables */
public class LoadableIngredientSerializer {
  public static <T extends ICustomIngredient> IngredientType<T> of(RecordLoadable<T> loadable) {
    MapCodec<T> codec = new MapCodec<T>() {
      @Override
      public <T1> java.util.stream.Stream<T1> keys(DynamicOps<T1> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
        try {
          JsonObject json = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries())).getAsJsonObject();
          return DataResult.success(loadable.deserialize(json));
        } catch (Exception e) {
          return DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
        try {
          JsonObject json = new JsonObject();
          loadable.serialize(input, json);
          T1 map = JsonOps.INSTANCE.convertTo(ops, json);
          ops.getMapValues(map).result().ifPresent(mapEntries -> {
            mapEntries.forEach(entry -> prefix.add(entry.getFirst(), entry.getSecond()));
          });
          return prefix;
        } catch (Exception e) {
          return prefix.withErrorsFrom(DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage()));
        }
      }
    };

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = new StreamCodec<RegistryFriendlyByteBuf, T>() {
      @Override
      public T decode(RegistryFriendlyByteBuf buffer) {
        return loadable.decode(buffer);
      }

      @Override
      public void encode(RegistryFriendlyByteBuf buffer, T value) {
        loadable.encode(buffer, value);
      }
    };

    return new IngredientType<>(codec, streamCodec);
  }
}
