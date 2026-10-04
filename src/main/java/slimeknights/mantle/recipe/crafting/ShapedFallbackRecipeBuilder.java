package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.MantleRecipes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.recipes.RecipeOutput;

/** Builder for a shaped recipe with fallbacks */
@SuppressWarnings("unused")
@RequiredArgsConstructor(staticName = "fallback")
public class ShapedFallbackRecipeBuilder {
  private final ShapedRecipeBuilder base;
  private final List<ResourceLocation> alternatives = new ArrayList<>();

  /**
   * Adds a single alternative to this recipe. Any matching alternative causes this recipe to fail
   * @param location  Alternative
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternative(ResourceLocation location) {
    this.alternatives.add(location);
    return this;
  }

  /**
   * Adds a list of alternatives to this recipe. Any matching alternative causes this recipe to fail
   * @param locations  Alternative list
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternatives(Collection<ResourceLocation> locations) {
    this.alternatives.addAll(locations);
    return this;
  }

  /**
   * Builds the recipe using the output as the name
   * @param consumer  Recipe consumer
   */
  public void build(RecipeOutput consumer) {
    base.save(new RecipeOutput() {
      @Override
      public void accept(ResourceLocation id, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
        ShapedFallbackRecipe fallback = new ShapedFallbackRecipe((net.minecraft.world.item.crafting.ShapedRecipe) recipe, alternatives);
        consumer.accept(id, fallback, advancement, conditions);
      }

      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }
    });
  }

  /**
   * Builds the recipe using the given ID
   * @param consumer  Recipe consumer
   * @param id        Recipe ID
   */
  public void build(RecipeOutput consumer, ResourceLocation id) {
    base.save(new RecipeOutput() {
      @Override
      public void accept(ResourceLocation rid, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
        ShapedFallbackRecipe fallback = new ShapedFallbackRecipe((net.minecraft.world.item.crafting.ShapedRecipe) recipe, alternatives);
        consumer.accept(rid, fallback, advancement, conditions);
      }

      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }
    }, id);
  }
}
