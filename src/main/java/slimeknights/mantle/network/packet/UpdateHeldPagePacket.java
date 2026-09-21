package slimeknights.mantle.network.packet;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.Mantle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import slimeknights.mantle.client.book.BookHelper;

/**
 * Packet to update the page in a book in the players hand
 */
@RequiredArgsConstructor
public class UpdateHeldPagePacket implements IThreadsafePacket {
  public static final Type<UpdateHeldPagePacket> ID = new Type<>(Mantle.getResource("update_held_page"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateHeldPagePacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), UpdateHeldPagePacket::new);

  @Override
  public Type<UpdateHeldPagePacket> type() {
    return ID;
  }

  private final InteractionHand hand;
  private final String page;
  public UpdateHeldPagePacket(RegistryFriendlyByteBuf buffer) {
    this.hand = buffer.readEnum(InteractionHand.class);
    this.page = buffer.readUtf(100);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buf) {
    buf.writeEnum(hand);
    buf.writeUtf(this.page);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Player player = (context.player() instanceof ServerPlayer sp ? sp : null);
    if (player != null && this.page != null) {
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.isEmpty()) {
        BookHelper.writeSavedPageToBook(stack, this.page);
      }
    }
  }
}

