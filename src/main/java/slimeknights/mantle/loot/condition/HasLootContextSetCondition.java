package slimeknights.mantle.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.loot.MantleLoot;

/** Loot condition that only runs if all required values in the given loot context set are present. Good heuristic for using that set. */
public record HasLootContextSetCondition(LootContextParamSet set) implements LootItemCondition {
  /** Creates a new builder instance */
  public static Builder builder(LootContextParamSet set) {
    return new Builder(set);
  }

  @Override
  public LootItemConditionType getType() {
    return MantleLoot.HAS_CONTEXT_SET;
  }

  @Override
  public boolean test(LootContext context) {
    for (LootContextParam<?> param : set.getRequired()) {
      if (!context.hasParam(param)) {
        return false;
      }
    }
    return true;
  }

  /** Builder logic for this condition */
  public record Builder(LootContextParamSet set) implements LootItemCondition.Builder {
    @Override
    public LootItemCondition build() {
      return new HasLootContextSetCondition(set);
    }
  }

  public static final MapCodec<HasLootContextSetCondition> CODEC = RecordCodecBuilder.mapCodec(inst ->
      inst.group(
          com.mojang.serialization.Codec.STRING.fieldOf("set_dummy").xmap(
              key -> net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY,
              set -> "dummy"
          ).forGetter(HasLootContextSetCondition::set)
      ).apply(inst, HasLootContextSetCondition::new)
  );
}
