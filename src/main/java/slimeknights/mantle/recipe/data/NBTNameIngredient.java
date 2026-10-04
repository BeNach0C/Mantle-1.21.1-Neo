package slimeknights.mantle.recipe.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Ingredient for a NBT sensitive item from another mod, should never be used outside datagen
 */
public class NBTNameIngredient implements net.neoforged.neoforge.common.crafting.ICustomIngredient {
  private final ResourceLocation name;
  @Nullable
  private final CompoundTag nbt;

  protected NBTNameIngredient(ResourceLocation name, @Nullable CompoundTag nbt) {
    this.name = name;
    this.nbt = nbt;
  }

  /**
   * Creates an ingredient for the given name and NBT
   * @param name  Item name
   * @param nbt   NBT
   * @return  Ingredient
   */
  public static NBTNameIngredient from(ResourceLocation name, CompoundTag nbt) {
    return new NBTNameIngredient(name, nbt);
  }

  /**
   * Creates an ingredient for an item that must have no NBT
   * @param name  Item name
   * @return  Ingredient
   */
  public static NBTNameIngredient from(ResourceLocation name) {
    return new NBTNameIngredient(name, null);
  }

  public boolean test(ItemStack stack) {
    throw new UnsupportedOperationException();
  }

  public java.util.stream.Stream<ItemStack> getItems() {
    throw new UnsupportedOperationException();
  }

  public boolean isSimple() {
    return false;
  }

  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    throw new UnsupportedOperationException();
  }

  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "neoforge:components");
    json.addProperty("item", name.toString());
    if (nbt != null) {
      json.addProperty("nbt", nbt.toString()); // Note: should probably be components, but datagen doesn't care right now
    }
    return json;
  }
}

