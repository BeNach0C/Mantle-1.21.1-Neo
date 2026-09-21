package slimeknights.mantle.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookHelper;

/** Packet to update the page in a book in the players inventory */
public record UpdateInventoryPagePacket(int slot, String page) implements IThreadsafePacket {
  public static final Type<UpdateInventoryPagePacket> ID = new Type<>(Mantle.getResource("update_inventory_page"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInventoryPagePacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), UpdateInventoryPagePacket::new);

  public UpdateInventoryPagePacket(RegistryFriendlyByteBuf buffer) {
    this(buffer.readVarInt(), buffer.readUtf(100));
  }

  @Override
  public Type<UpdateInventoryPagePacket> type() {
    return ID;
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buf) {
    buf.writeVarInt(slot);
    buf.writeUtf(page);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Player player = context.player() instanceof ServerPlayer sp ? sp : null;
    if (player != null && this.page != null && slot >= 0) {
      ItemStack stack = player.getInventory().getItem(slot);
      if (!stack.isEmpty()) {
        BookHelper.writeSavedPageToBook(stack, this.page);
      }
    }
  }
}

