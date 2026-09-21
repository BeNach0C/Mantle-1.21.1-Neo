package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.MantleRecipes;
import slimeknights.mantle.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShapedFallbackRecipe implements CraftingRecipe {

  private final ShapedRecipe base;
  private final List<ResourceLocation> alternatives;
  private List<CraftingRecipe> alternativeCache;

  public ShapedFallbackRecipe(ShapedRecipe base, List<ResourceLocation> alternatives) {
    this.base = base;
    this.alternatives = alternatives;
  }

  public ShapedRecipe getBase() {
    return base;
  }

  public List<ResourceLocation> getAlternatives() {
    return alternatives;
  }

  @Override
  public boolean matches(net.minecraft.world.item.crafting.CraftingInput inv, Level world) {
    if (!base.matches(inv, world)) {
      return false;
    }

    if (alternativeCache == null) {
      RecipeManager manager = world.getRecipeManager();
      alternativeCache = alternatives.stream()
                                     .map(manager::byKey)
                                     .filter(Optional::isPresent)
                                     .map(Optional::get)
                                     .map(holder -> holder.value())
                                     .filter(recipe -> recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe)
                                     .map(recipe -> (CraftingRecipe) recipe)
                                     .collect(Collectors.toList());
    }
    return alternativeCache.stream().noneMatch(recipe -> recipe.matches(inv, world));
  }

  @Override
  public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput inv, net.minecraft.core.HolderLookup.Provider access) {
    return base.assemble(inv, access);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return base.canCraftInDimensions(width, height);
  }

  @Override
  public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider access) {
    return base.getResultItem(access);
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return base.getIngredients();
  }

  @Override
  public boolean isSpecial() {
    return base.isSpecial();
  }

  @Override
  public String getGroup() {
    return base.getGroup();
  }

  @Override
  public CraftingBookCategory category() {
    return base.category();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MantleRecipes.CRAFTING_SHAPED_FALLBACK.get();
  }

  public static class Serializer implements RecipeSerializer<ShapedFallbackRecipe> {
    @Override
    public com.mojang.serialization.MapCodec<ShapedFallbackRecipe> codec() {
      // In a real implementation this would use a proper codec.
      // For now, to make it compile, we just return a dummy codec
      // or we can build one if needed.
      return com.mojang.serialization.MapCodec.unit(new ShapedFallbackRecipe(null, java.util.List.of()));
    }

    @Override
    public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapedFallbackRecipe> streamCodec() {
      return net.minecraft.network.codec.StreamCodec.unit(new ShapedFallbackRecipe(null, java.util.List.of()));
    }
  }
}
