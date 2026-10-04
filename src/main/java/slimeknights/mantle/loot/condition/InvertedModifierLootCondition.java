package slimeknights.mantle.loot.condition;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;

import java.lang.reflect.Type;
import java.util.List;

/** Loot modifier condition that inverts the base condition */
public class InvertedModifierLootCondition implements ILootModifierCondition {
  public static final ResourceLocation ID = Mantle.getResource("inverted");

  /** Condition to invert */
  private final ILootModifierCondition condition;

  public InvertedModifierLootCondition(ILootModifierCondition condition) {
    this.condition = condition;
  }

  @Override
  public boolean test(List<ItemStack> generatedLoot, LootContext context) {
    return !condition.test(generatedLoot, context);
  }

  @Override
  public ILootModifierCondition inverted() {
    return condition;
  }

  public static final com.mojang.serialization.MapCodec<InvertedModifierLootCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst ->
      inst.group(
          ((com.mojang.serialization.Codec<ILootModifierCondition>)(Object)net.minecraft.world.level.storage.loot.predicates.LootItemCondition.DIRECT_CODEC).fieldOf("condition").forGetter(c -> c.condition)
      ).apply(inst, InvertedModifierLootCondition::new)
  );
}
