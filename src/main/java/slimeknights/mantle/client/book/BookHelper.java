package slimeknights.mantle.client.book;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Objects;

public class BookHelper {

  public static final String BOOK_COMPOUND = "mantle";
  public static final String BOOK_DATA_COMPOUND = "book";

  public static final String NBT_CURRENT_PAGE = "current_page";

  /**
   * Returns the current saved page on the book
   * Returns an empty string is one is not found
   *
   * @param item The book to check for a saved page on
   * @return The current saved page
   */
  public static String getCurrentSavedPage(@Nullable ItemStack item) {
    if (item != null && !item.isEmpty()) {
      net.minecraft.world.item.component.CustomData data = item.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
      CompoundTag bookNBT = data.copyTag().getCompound(BOOK_COMPOUND).getCompound(BOOK_DATA_COMPOUND);
      if (bookNBT.contains(NBT_CURRENT_PAGE, 8)) {
        return bookNBT.getString(NBT_CURRENT_PAGE);
      }
    }
    return "";
  }

  public static void writeSavedPageToBook(ItemStack stack, String currentPage) {
    net.minecraft.world.item.component.CustomData data = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
    CompoundTag compoundNBT = data.copyTag();

    CompoundTag mantleCompound = compoundNBT.getCompound(BOOK_COMPOUND);
    CompoundTag bookCompound = compoundNBT.getCompound(BOOK_DATA_COMPOUND);

    bookCompound.putString(NBT_CURRENT_PAGE, currentPage);

    mantleCompound.put(BOOK_DATA_COMPOUND, bookCompound);
    compoundNBT.put(BOOK_COMPOUND, mantleCompound);
    stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(compoundNBT));
  }
}
