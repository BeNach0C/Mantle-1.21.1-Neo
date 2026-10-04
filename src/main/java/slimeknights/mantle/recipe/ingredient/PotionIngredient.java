package slimeknights.mantle.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class PotionIngredient implements ICustomIngredient {
  public static final MapCodec<PotionIngredient> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
          Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.base),
          Potion.CODEC.fieldOf("potion").forGetter(i -> i.potion)
      ).apply(instance, PotionIngredient::new)
  );

  public static final StreamCodec<RegistryFriendlyByteBuf, PotionIngredient> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC, i -> i.base,
      Potion.STREAM_CODEC, i -> i.potion,
      PotionIngredient::new
  );

  private final Ingredient base;
  private final Holder<Potion> potion;

  public PotionIngredient(Ingredient base, Holder<Potion> potion) {
    this.base = base;
    this.potion = potion;
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    if (!this.base.test(stack)) {
      return false;
    }
    PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
    return contents != null && contents.is(this.potion);
  }

  @Override
  public Stream<ItemStack> getItems() {
    return Stream.of(this.base.getItems()).map(stack -> {
      ItemStack newStack = stack.copy();
      newStack.set(DataComponents.POTION_CONTENTS, new PotionContents(this.potion));
      return newStack;
    });
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public IngredientType<?> getType() {
    // This assumes there's a registered IngredientType somewhere, probably needs to be fetched from a registry
    // or we define it here if there is one. Let's see what the compiler says.
    return null;
  }
}
