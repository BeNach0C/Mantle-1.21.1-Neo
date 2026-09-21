package slimeknights.mantle.recipe.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Ingredient for a non-NBT sensitive item from another mod, should never be used outside datagen
 */
public class ItemNameIngredient implements ICustomIngredient {
  private final List<ResourceLocation> names;
  protected ItemNameIngredient(List<ResourceLocation> names) {
    this.names = names;
  }

  /** Creates a new ingredient from a list of names */
  public static ItemNameIngredient from(List<ResourceLocation> names) {
    return new ItemNameIngredient(names);
  }

  /** Creates a new ingredient from a list of names */
  public static ItemNameIngredient from(ResourceLocation... names) {
    return from(Arrays.asList(names));
  }

  public boolean test(ItemStack stack) {
    throw new UnsupportedOperationException();
  }

  public java.util.stream.Stream<ItemStack> getItems() {
    throw new UnsupportedOperationException();
  }

  /** Creates a JSON object for a name */
  private static JsonObject forName(ResourceLocation name) {
    JsonObject json = new JsonObject();
    json.addProperty("item", name.toString());
    return json;
  }

  public JsonElement toJson() {
    if (names.size() == 1) {
      return forName(names.get(0));
    }
    JsonArray array = new JsonArray();
    for (ResourceLocation name : names) {
      array.add(forName(name));
    }
    return array;
  }

  public boolean isSimple() {
    return false;
  }

  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    throw new UnsupportedOperationException();
  }

  @RequiredArgsConstructor
  public static class NamedValue {
    private final ResourceLocation name;

    public Collection<ItemStack> getItems() {
      throw new UnsupportedOperationException();
    }

    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("item", name.toString());
      return json;
    }
  }
}

