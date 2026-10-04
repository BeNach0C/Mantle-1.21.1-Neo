package slimeknights.mantle.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.common.loot.LootModifierManager;
import slimeknights.mantle.data.JsonCodec.GsonCodec;

/** This class contains codecs for various vanilla things that we need to use in codecs. Typically the reason is forge pre-emptively moved a thing to codecs before vanilla did. */
public class MantleCodecs {
  /** Codec for loot pool entries */
  public static final Codec<LootPoolEntryContainer> LOOT_ENTRY = net.minecraft.world.level.storage.loot.entries.LootPoolEntries.CODEC;
  /** Codec for loot functions */
  public static final Codec<LootItemFunction[]> LOOT_FUNCTIONS = net.minecraft.world.level.storage.loot.functions.LootItemFunctions.CODEC.listOf().xmap(
      l -> l.stream().map(net.minecraft.core.Holder::value).toArray(LootItemFunction[]::new),
      l -> java.util.Arrays.stream(l).map(net.minecraft.core.Holder::direct).toList()
  );
  /** Codec for ingredients, handling forge ingredient types */
  public static final Codec<Ingredient> INGREDIENT = Ingredient.CODEC_NONEMPTY;
}

