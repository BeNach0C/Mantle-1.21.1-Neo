package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.MantleRecipes;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nullable;
import java.util.Map;

/** Recipe which sets the texture for a {@link slimeknights.mantle.block.RetexturedBlock} based on an ingredient input. */
// TODO 1.21: rework to be more like the ShapedMaterialsRecipe from Tinkers for more efficient network syncing
@SuppressWarnings("WeakerAccess")
public class ShapedRetexturedRecipe extends ShapedRecipe {
  /** Ingredient used to determine the texture on the output */
  
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
    public static final com.mojang.serialization.MapCodec<ShapedRetexturedRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
      net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().forGetter(r -> (ShapedRecipe) r),
      Ingredient.CODEC_NONEMPTY.fieldOf("texture").forGetter(ShapedRetexturedRecipe::getTexture),
      com.mojang.serialization.Codec.BOOL.optionalFieldOf("match_all", false).forGetter(r -> r.matchAll)
    ).apply(inst, ShapedRetexturedRecipe::new));

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

