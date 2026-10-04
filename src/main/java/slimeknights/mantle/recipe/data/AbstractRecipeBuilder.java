package slimeknights.mantle.recipe.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.data.recipes.RecipeOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {
  protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
  @Nonnull
  protected String group = "";

  @SuppressWarnings("unchecked")
  public T unlockedBy(String name, Criterion<?> criteria) {
    this.advancementBuilder.addCriterion(name, criteria);
    return (T)this;
  }

  @SuppressWarnings("unchecked")
  public T group(String group) {
    this.group = group;
    return (T)this;
  }

  public T group(ResourceLocation group) {
    if ("minecraft".equals(group.getNamespace())) {
      return group(group.getPath());
    }
    return group(group.toString());
  }

  public abstract void save(RecipeOutput consumerIn);

  public abstract void save(RecipeOutput consumerIn, ResourceLocation id);

  private AdvancementHolder buildAdvancementInternal(ResourceLocation id, String folder) {
    this.advancementBuilder
        .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "recipes/root"))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);
    this.advancementBuilder.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id));
    ResourceLocation advId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + folder + "/" + id.getPath());
    return this.advancementBuilder.build(advId);
  }

  @Nullable
  protected AdvancementHolder buildAdvancement(ResourceLocation id, String folder) {
    // We can't easily check if criteria is empty in 1.21 without reflection or tracking it ourselves, 
    // but typically unlockedBy is called at least once. 
    return buildAdvancementInternal(id, folder);
  }

  @Nullable
  protected AdvancementHolder buildOptionalAdvancement(ResourceLocation id, String folder) {
    return buildAdvancementInternal(id, folder);
  }
}
