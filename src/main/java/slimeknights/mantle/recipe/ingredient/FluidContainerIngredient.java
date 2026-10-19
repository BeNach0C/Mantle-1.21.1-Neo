package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/** Ingredient that matches a container of fluid */
@SuppressWarnings("unused")  // API
public class FluidContainerIngredient implements ICustomIngredient {
  public static final ResourceLocation ID = Mantle.getResource("fluid_container");
  public static final IngredientType<FluidContainerIngredient> TYPE = LoadableIngredientSerializer.of(
    json -> {
      FluidIngredient fluidIngredient;
      if (json.has("fluid") && !json.get("fluid").isJsonPrimitive()) {
        fluidIngredient = FluidIngredient.LOADABLE.getIfPresent(json, "fluid");
      } else {
        fluidIngredient = FluidIngredient.LOADABLE.convert(json, "fluid");
      }
      Ingredient display = null;
      if (json.has("display")) {
        display = IngredientLoadable.ALLOW_EMPTY.convert(JsonHelper.getElement(json, "display"), "display");
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    },
    (ing, json) -> {
      JsonElement element = ing.fluidIngredient.serialize();
      if (element.isJsonObject()) {
        element.getAsJsonObject().entrySet().forEach(e -> json.add(e.getKey(), e.getValue()));
      } else {
        json.add("fluid", element);
      }
      if (ing.display != null) {
        json.add("display", IngredientLoadable.ALLOW_EMPTY.serialize(ing.display));
      }
    },
    buffer -> {
      FluidIngredient fluidIngredient = FluidIngredient.LOADABLE.decode(buffer);
      Ingredient display = null;
      if (buffer.readBoolean()) {
        display = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    },
    (buffer, ing) -> {
      FluidIngredient.LOADABLE.encode(buffer, ing.fluidIngredient);
      if (ing.display != null) {
        buffer.writeBoolean(true);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing.display);
      } else {
        buffer.writeBoolean(false);
      }
    }
  );

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private ItemStack[] displayStacks;

  protected FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
    this.fluidIngredient = fluidIngredient;
    this.display = display;
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
    return new FluidContainerIngredient(ingredient, display);
  }

  /** Creates an instance from a fluid ingredient with no display, not recommended */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient) {
    return new FluidContainerIngredient(ingredient, null);
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromFluid(FluidObject<?> fluid) {
    return fromIngredient(fluid.ingredient(FluidType.BUCKET_VOLUME), Ingredient.of(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
    if (cap == null || cap.getTanks() != 1) {
      return false;
    }
    FluidStack contained = cap.getFluidInTank(0);
    if (contained.isEmpty() || fluidIngredient.getAmount(contained.getFluid()) != contained.getAmount() || !fluidIngredient.test(contained.getFluid())) {
      return false;
    }
    ItemStack copy = stack.copyWithCount(1);
    IFluidHandlerItem copyCap = copy.getCapability(Capabilities.FluidHandler.ITEM);
    if (copyCap == null) {
      return false;
    }
    Fluid fluid = copyCap.getFluidInTank(0).getFluid();
    int amount = fluidIngredient.getAmount(fluid);
    FluidStack drained = copyCap.drain(amount, FluidAction.EXECUTE);
    return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.isSameItemSameComponents(stack.getCraftingRemainingItem(), copyCap.getContainer());
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (displayStacks == null) {
      if (display == null) {
        displayStacks = new ItemStack[0];
      } else {
        displayStacks = display.getItems();
      }
    }
    return Arrays.stream(displayStacks);
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public IngredientType<?> getType() {
    return TYPE;
  }
}
