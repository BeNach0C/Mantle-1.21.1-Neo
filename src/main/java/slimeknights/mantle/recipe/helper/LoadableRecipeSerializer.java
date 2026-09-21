package slimeknights.mantle.recipe.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.Mantle;

import java.util.function.Supplier;

/**
 * Recipe serializer instance using Codecs.
 * @param <T>  Recipe type
 */
public class LoadableRecipeSerializer<T extends Recipe<?>> implements LoggingRecipeSerializer<T> {
  public static final slimeknights.mantle.data.loadable.field.LoadableField<String, Recipe<?>> RECIPE_GROUP = slimeknights.mantle.data.loadable.primitive.StringLoadable.DEFAULT.defaultField("group", "", Recipe::getGroup);

  protected final MapCodec<T> codec;
  protected final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

  protected LoadableRecipeSerializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    this.codec = codec;
    this.streamCodec = streamCodec;
  }

  /** Creates a standard serializer from codecs */
  public static <T extends Recipe<?>> RecipeSerializer<T> of(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    return new LoadableRecipeSerializer<>(codec, streamCodec);
  }

  /** Creates a serializer from a RecordLoadable */
  public static <T extends Recipe<?>> RecipeSerializer<T> of(slimeknights.mantle.data.loadable.record.RecordLoadable<T> loadable) {
    MapCodec<T> codec = new MapCodec<T>() {
      @Override
      public <T1> java.util.stream.Stream<T1> keys(com.mojang.serialization.DynamicOps<T1> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T1> com.mojang.serialization.DataResult<T> decode(com.mojang.serialization.DynamicOps<T1> ops, com.mojang.serialization.MapLike<T1> input) {
        try {
          com.google.gson.JsonObject json = ops.convertTo(com.mojang.serialization.JsonOps.INSTANCE, ops.createMap(input.entries())).getAsJsonObject();
          return com.mojang.serialization.DataResult.success(loadable.deserialize(json));
        } catch (Exception e) {
          return com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T1> com.mojang.serialization.RecordBuilder<T1> encode(T input, com.mojang.serialization.DynamicOps<T1> ops, com.mojang.serialization.RecordBuilder<T1> prefix) {
        try {
          com.google.gson.JsonObject json = new com.google.gson.JsonObject();
          loadable.serialize(input, json);
          T1 map = com.mojang.serialization.JsonOps.INSTANCE.convertTo(ops, json);
          ops.getMapValues(map).result().ifPresent(mapEntries -> {
            mapEntries.forEach(entry -> prefix.add(entry.getFirst(), entry.getSecond()));
          });
          return prefix;
        } catch (Exception e) {
          return prefix.withErrorsFrom(com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage()));
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

    return new LoadableRecipeSerializer<>(codec, streamCodec);
  }

  /** Creates a type aware serializer from codecs */
  public static <T extends R, R extends Recipe<?>> TypeAwareRecipeSerializer<T> of(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<? extends RecipeType<R>> type) {
    return new TypeAware<>(codec, streamCodec, type);
  }

  @Override
  public MapCodec<T> codec() {
    return codec;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, T> streamCodecSafe() {
    return streamCodec;
  }

  public static class TypeAware<T extends Recipe<?>> extends LoadableRecipeSerializer<T> implements TypeAwareRecipeSerializer<T> {
    private final Supplier<? extends RecipeType<?>> type;
    protected TypeAware(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<? extends RecipeType<?>> type) {
      super(codec, streamCodec);
      this.type = type;
    }

    @Override
    public RecipeType<?> getType() {
      return type.get();
    }
  }
}
