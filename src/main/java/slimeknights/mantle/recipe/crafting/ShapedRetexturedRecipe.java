package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.recipe.MantleRecipes;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.RetexturedHelper;

/** Recipe which sets the texture for a {@link slimeknights.mantle.block.RetexturedBlock} based on an ingredient input. */
@SuppressWarnings("WeakerAccess")
public class ShapedRetexturedRecipe extends ShapedRecipe {
  /** Ingredient used to determine the texture on the output */
  @Getter
  private final Ingredient texture;
  public Ingredient getTexture() { return texture; }
  private final boolean matchAll;

  /** Creates a new recipe using the passed parameters */
  public ShapedRetexturedRecipe(String group, CraftingBookCategory category, net.minecraft.world.item.crafting.ShapedRecipePattern pattern, ItemStack result, boolean showNotification, Ingredient texture, boolean matchAll) {
    super(group, category, pattern, result, showNotification);
    this.texture = texture;
    this.matchAll = matchAll;
  }

  /**
   * Creates a new recipe using an existing shaped recipe
   * @param orig       Shaped recipe to copy
   * @param texture    Ingredient to use for the texture
   * @param matchAll   If true, all inputs must match for the recipe to match
   */
  public ShapedRetexturedRecipe(ShapedRecipe orig, Ingredient texture, boolean matchAll) {
    this(orig.getGroup(), orig.category(), orig.pattern, orig.getResultItem(null), orig.showNotification(), texture, matchAll);
  }

  /**
   * Gets the output using the given texture
   * @param texture  Texture to use
   * @return  Output with texture. Will be blank if the input is not a block
   */
  public ItemStack getResultItem(Item texture, RegistryAccess access) {
    return RetexturedHelper.setTexture(getResultItem(access).copy(), Block.byItem(texture));
  }

  @Override
  public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput craftMatrix, net.minecraft.core.HolderLookup.Provider access) {
    ItemStack result = super.assemble(craftMatrix, access);
    Block currentTexture = null;
    for (int i = 0; i < craftMatrix.size(); i++) {
      ItemStack stack = craftMatrix.getItem(i);
      if (!stack.isEmpty() && texture.test(stack)) {
        // fetch texture from the block if it has one
        Block block = RetexturedHelper.getTexture(stack);
        // assuming it does not, use the block itself as the texture (provided it is not the result that is)
        if (block == Blocks.AIR && stack.getItem() != result.getItem()) {
          block = Block.byItem(stack.getItem());
        }
        // if no texture, skip
        if (block == Blocks.AIR) {
          continue;
        }

        // if we have not found a texture yet, store the found block
        if (currentTexture == null) {
          currentTexture = block;
          // match all means we must check the rest. If not match all, we can be done
          if (!matchAll) {
            break;
          }

          // if we found a texture before, must match or we do no texture
        } else if (currentTexture != block) {
          currentTexture = null;
          break;
        }
      }
    }

    // set the texture if found. No texture will use the fallback
    if (currentTexture != null) {
      return RetexturedHelper.setTexture(result, currentTexture);
    }
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MantleRecipes.CRAFTING_SHAPED_RETEXTURED.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedRetexturedRecipe> {
    public static final com.mojang.serialization.MapCodec<ShapedRetexturedRecipe> CODEC = new com.mojang.serialization.MapCodec<>() {
      @Override
      public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T> com.mojang.serialization.DataResult<ShapedRetexturedRecipe> decode(com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
        try {
          JsonObject json = ops.convertTo(com.mojang.serialization.JsonOps.INSTANCE, ops.createMap(input.entries())).getAsJsonObject();
          JsonElement nbtElement = null;
          if (json.has("result") && json.get("result").isJsonObject()) {
            JsonObject resultObj = json.getAsJsonObject("result");
            if (resultObj.has("item") && !resultObj.has("id")) {
              resultObj.add("id", resultObj.get("item"));
              resultObj.remove("item");
            }
            if (resultObj.has("nbt")) {
              nbtElement = resultObj.remove("nbt");
            }
          }
          ShapedRecipe orig = net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().codec()
            .parse(com.mojang.serialization.JsonOps.INSTANCE, json)
            .getOrThrow(com.google.gson.JsonSyntaxException::new);
          ItemStack result = orig.getResultItem(null);
          if (nbtElement != null) {
            net.minecraft.nbt.CompoundTag tag = slimeknights.mantle.data.loadable.common.NBTLoadable.ALLOW_STRING.convert(nbtElement, "nbt");
            result = result.copy();
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
          }
          JsonElement texElement = json.get("texture");
          if (texElement != null && texElement.isJsonPrimitive() && json.has("key") && json.get("key").isJsonObject()) {
            JsonObject keyObj = json.getAsJsonObject("key");
            String k = texElement.getAsString();
            if (keyObj.has(k)) {
              texElement = keyObj.get(k);
            }
          }
          Ingredient texture = slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.convert(texElement, "texture");
          boolean matchAll = GsonHelper.getAsBoolean(json, "match_all", false);
          return com.mojang.serialization.DataResult.success(
            new ShapedRetexturedRecipe(orig.getGroup(), orig.category(), orig.pattern, result, orig.showNotification(), texture, matchAll));
        } catch (Exception e) {
          return com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T> com.mojang.serialization.RecordBuilder<T> encode(ShapedRetexturedRecipe input, com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().encode(input, ops, prefix);
      }
    };

    @Override
    public com.mojang.serialization.MapCodec<ShapedRetexturedRecipe> codec() {
      return CODEC;
    }

    @Override
    public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapedRetexturedRecipe> streamCodecSafe() {
      return new net.minecraft.network.codec.StreamCodec<>() {
        @Override
        public ShapedRetexturedRecipe decode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
          ShapedRecipe recipe = net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
          Ingredient texture = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
          boolean matchAll = buffer.readBoolean();
          return new ShapedRetexturedRecipe(recipe, texture, matchAll);
        }

        @Override
        public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, ShapedRetexturedRecipe recipe) {
          net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
          Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getTexture());
          buffer.writeBoolean(recipe.matchAll);
        }
      };
    }
  }
}
