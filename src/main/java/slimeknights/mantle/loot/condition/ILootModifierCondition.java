package slimeknights.mantle.loot.condition;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

/** Condition for the global loot modifier add entry */
public interface ILootModifierCondition {
  // Removed MODIFIER_CONDITIONS and CODEC as part of abandoning IJsonSerializable in favor of Codecs

  /** Checks if this condition passes */
  boolean test(List<ItemStack> generatedLoot, LootContext context);

  /** Creates an inverted instance of this condition */
  default ILootModifierCondition inverted() {
    return new InvertedModifierLootCondition(this);
  }
}
