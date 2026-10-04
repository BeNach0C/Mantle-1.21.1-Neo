package slimeknights.mantle.recipe.helper;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;

/**
 * Recipe serializer that logs network exceptions before throwing them as otherwise the exceptions may be invisible
 * @param <T>  Recipe class
 */
public interface LoggingRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
  /**
   * Safe stream codec that will have exceptions logged
   * @return StreamCodec for safe network syncing
   */
  StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, T> streamCodecSafe();

  @Override
  default StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, T> streamCodec() {
    return new StreamCodec<>() {
      @Override
      public T decode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        try {
          return streamCodecSafe().decode(buffer);
        } catch (RuntimeException e) {
          String error = LoggingRecipeSerializer.this.getClass().getSimpleName() + ": Error reading recipe from packet";
          Mantle.logger.error("{}", error, e);
          throw new DecoderException(error + " - " + e.getMessage(), e);
        }
      }

      @Override
      public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, T recipe) {
        try {
          streamCodecSafe().encode(buffer, recipe);
        } catch (RuntimeException e) {
          String error = LoggingRecipeSerializer.this.getClass().getSimpleName() + ": Error writing recipe of class " + recipe.getClass().getSimpleName() + " to packet";
          Mantle.logger.error("{}", error, e);
          throw new EncoderException(error + " - " + e.getMessage(), e);
        }
      }
    };
  }
}
