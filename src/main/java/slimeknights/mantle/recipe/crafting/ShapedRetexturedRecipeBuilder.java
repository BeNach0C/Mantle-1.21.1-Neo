package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.MantleRecipes;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import net.minecraft.data.recipes.RecipeOutput;

@SuppressWarnings("unused")
@RequiredArgsConstructor(staticName = "fromShaped")
public class ShapedRetexturedRecipeBuilder {
  private final ShapedRecipeBuilder parent;
  private Ingredient texture = null;
  private char textureKey = '\0';
  private boolean matchAll = false;

  public ShapedRetexturedRecipeBuilder setSource(Ingredient texture) {
    this.texture = texture;
    this.textureKey = '\0';
    return this;
  }

  public ShapedRetexturedRecipeBuilder setSource(TagKey<Item> tag) {
    return setSource(Ingredient.of(tag));
  }

  public ShapedRetexturedRecipeBuilder setSource(char textureKey) {
    this.textureKey = textureKey;
    this.texture = null;
    return this;
  }

  public ShapedRetexturedRecipeBuilder setMatchAll() {
    this.matchAll = true;
    return this;
  }

  private void validate() {
    if (texture == null && textureKey == '\0') {
      throw new IllegalStateException("No texture defined for texture recipe");
    }
  }

  public void build(RecipeOutput consumer) {
    this.validate();
    parent.save(new RecipeOutput() {
      @Override
      public void accept(ResourceLocation id, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
        Ingredient finalTexture = texture;
        if (finalTexture == null && textureKey != '\0') {
           finalTexture = net.minecraft.world.item.crafting.Ingredient.of();
        }
        ShapedRetexturedRecipe fallback = new ShapedRetexturedRecipe((net.minecraft.world.item.crafting.ShapedRecipe) recipe, finalTexture, matchAll);
        consumer.accept(id, fallback, advancement, conditions);
      }

      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }
    });
  }

  public void build(RecipeOutput consumer, ResourceLocation location) {
    this.validate();
    parent.save(new RecipeOutput() {
      @Override
      public void accept(ResourceLocation id, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
        Ingredient finalTexture = texture;
        if (finalTexture == null && textureKey != '\0') {
           finalTexture = net.minecraft.world.item.crafting.Ingredient.of();
        }
        ShapedRetexturedRecipe fallback = new ShapedRetexturedRecipe((net.minecraft.world.item.crafting.ShapedRecipe) recipe, finalTexture, matchAll);
        consumer.accept(id, fallback, advancement, conditions);
      }

      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }
    }, location);
  }
}
