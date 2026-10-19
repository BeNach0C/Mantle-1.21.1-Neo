package slimeknights.mantle.loot.condition;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

/** Condition for the global loot modifier add entry */
public interface ILootModifierCondition {
  com.mojang.serialization.Codec<ILootModifierCondition> CODEC = com.mojang.serialization.Codec.PASSTHROUGH.comapFlatMap(
    dynamic -> {
      try {
        com.google.gson.JsonElement element = dynamic.convert(com.mojang.serialization.JsonOps.INSTANCE).getValue();
        return com.mojang.serialization.DataResult.success(fromJson(net.minecraft.util.GsonHelper.convertToJsonObject(element, "post_condition")));
      } catch (Exception e) {
        return com.mojang.serialization.DataResult.error(e::getMessage);
      }
    },
    cond -> new com.mojang.serialization.Dynamic<>(com.mojang.serialization.JsonOps.INSTANCE, toJson(cond))
  );

  static ILootModifierCondition fromJson(com.google.gson.JsonObject json) {
    String type = net.minecraft.util.GsonHelper.getAsString(json, "type");
    if (InvertedModifierLootCondition.ID.toString().equals(type)) {
      return new InvertedModifierLootCondition(fromJson(net.minecraft.util.GsonHelper.getAsJsonObject(json, "condition")));
    } else if (ContainsItemModifierLootCondition.ID.toString().equals(type)) {
      return ContainsItemModifierLootCondition.deserialize(json, ILootModifierCondition.class, null);
    } else if (EmptyModifierLootCondition.ID.toString().equals(type)) {
      return EmptyModifierLootCondition.INSTANCE;
    }
    throw new com.google.gson.JsonParseException("Unknown ILootModifierCondition type: " + type);
  }

  static com.google.gson.JsonObject toJson(ILootModifierCondition cond) {
    if (cond instanceof InvertedModifierLootCondition inv) {
      com.google.gson.JsonObject json = new com.google.gson.JsonObject();
      json.addProperty("type", InvertedModifierLootCondition.ID.toString());
      json.add("condition", toJson(inv.inverted()));
      return json;
    } else if (cond instanceof ContainsItemModifierLootCondition contains) {
      return contains.serialize(null);
    } else if (cond instanceof EmptyModifierLootCondition) {
      com.google.gson.JsonObject json = new com.google.gson.JsonObject();
      json.addProperty("type", EmptyModifierLootCondition.ID.toString());
      return json;
    }
    throw new IllegalArgumentException("Unknown ILootModifierCondition: " + cond);
  }

  /** Checks if this condition passes */
  boolean test(List<ItemStack> generatedLoot, LootContext context);

  /** Creates an inverted instance of this condition */
  default ILootModifierCondition inverted() {
    return new InvertedModifierLootCondition(this);
  }
}

