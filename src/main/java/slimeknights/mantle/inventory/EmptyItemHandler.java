package slimeknights.mantle.inventory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Item handler that contains no items. Use similarly to {@link net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyItemHandler implements IItemHandler {
  public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

  public int getSlots() {
    return 0;
  }

  public int getSlotLimit(int slot) {
    return 0;
  }

  public ItemStack getStackInSlot(int slot) {
    return ItemStack.EMPTY;
  }

  public boolean isItemValid(int slot, ItemStack stack) {
    return false;
  }

  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return stack;
  }

  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return ItemStack.EMPTY;
  }
}

