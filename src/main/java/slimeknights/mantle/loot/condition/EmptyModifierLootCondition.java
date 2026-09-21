package slimeknights.mantle.loot.condition;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;

import java.util.List;

/** Loot condition to check if previously generated loot is empty */
public class EmptyModifierLootCondition implements ILootModifierCondition {
  public static final ResourceLocation ID = Mantle.getResource("empty");
  public static final EmptyModifierLootCondition INSTANCE = new EmptyModifierLootCondition();

  private EmptyModifierLootCondition() {}

  @Override
  public boolean test(List<ItemStack> generatedLoot, LootContext context) {
    return generatedLoot.isEmpty();
  }
}
