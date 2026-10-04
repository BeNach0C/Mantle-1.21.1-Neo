package slimeknights.mantle.recipe.data;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.data.recipes.RecipeOutput;

@SuppressWarnings("unused")  // API
public class ConsumerWrapperBuilder {
  private final List<ICondition> conditions = new ArrayList<>();
  @Nullable
  private final RecipeSerializer<?> override;
  @Nullable
  private final ResourceLocation overrideName;

  private ConsumerWrapperBuilder(@Nullable RecipeSerializer<?> override, @Nullable ResourceLocation overrideName) {
    this.override = override;
    this.overrideName = overrideName;
  }

  public static ConsumerWrapperBuilder wrap() {
    return new ConsumerWrapperBuilder(null, null);
  }

  public static ConsumerWrapperBuilder wrap(RecipeSerializer<?> override) {
    return new ConsumerWrapperBuilder(override, null);
  }

  public static ConsumerWrapperBuilder wrap(ResourceLocation override) {
    return new ConsumerWrapperBuilder(null, override);
  }

  @CanIgnoreReturnValue
  public ConsumerWrapperBuilder addCondition(ICondition condition) {
    conditions.add(condition);
    return this;
  }

  public RecipeOutput build(RecipeOutput consumer) {
    return new RecipeOutput() {
      @Override
      public void accept(ResourceLocation id, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... baseConditions) {
        List<ICondition> allConditions = new ArrayList<>();
        if (baseConditions != null) {
          for (ICondition c : baseConditions) {
            allConditions.add(c);
          }
        }
        allConditions.addAll(conditions);
        consumer.accept(id, recipe, advancement, allConditions.toArray(new ICondition[0]));
      }

      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }
    };
  }
}
