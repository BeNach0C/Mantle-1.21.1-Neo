package slimeknights.mantle.plugin.jei;

import com.google.common.collect.Streams;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * JEI crafting extension to properly show, animate, and focus {@link ShapedRetexturedRecipe} instances
 */
public class RetexturableRecipeExtension implements ICraftingCategoryExtension {
  private final RecipeHolder<ShapedRetexturedRecipe> holder;
  /** Actual recipe instance */
  private final ShapedRetexturedRecipe recipe;
  /** List of all textured variants, fallback for JEI display */
  private final List<ItemStack> displayOutputs;
  /** Ingredient indexes of all texture slots */
  private final int[] textureSlots;

  RetexturableRecipeExtension(RecipeHolder<ShapedRetexturedRecipe> holder) {
    this.holder = holder;
    this.recipe = holder.value();

    this.displayOutputs = List.of();
    this.textureSlots = new int[0];
  }

  /** Checks if two ingredients match based on their display items */
  private static boolean ingredientsMatch(Ingredient left, Ingredient right) {
    ItemStack[] leftStacks = left.getItems();
    ItemStack[] rightStacks = right.getItems();
    if (leftStacks.length != rightStacks.length) {
      return false;
    }
    for (int i = 0; i < leftStacks.length; i++) {
      if (!ItemStack.isSameItemSameComponents(leftStacks[i], rightStacks[i])) {
        return false;
      }
    }
    return true;
  }

  public ResourceLocation getRegistryName() {
    return this.holder.id();
  }

  public int getWidth() {
    return recipe.getWidth();
  }

  public int getHeight() {
    return recipe.getHeight();
  }

  public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
  }
}

