package slimeknights.mantle.network.packet;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.Mantle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import slimeknights.mantle.item.ILecternBookItem;

/**
 * Packet to open a book on a lectern
 */
@AllArgsConstructor
public class OpenLecternBookPacket implements IThreadsafePacket {
  public static final Type<OpenLecternBookPacket> ID = new Type<>(Mantle.getResource("open_lectern_book"));
  public static final StreamCodec<RegistryFriendlyByteBuf, OpenLecternBookPacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), OpenLecternBookPacket::new);

  @Override
  public Type<OpenLecternBookPacket> type() {
    return ID;
  }

  private final BlockPos pos;
  private final ItemStack book;


  public OpenLecternBookPacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.book = ItemStack.STREAM_CODEC.decode(buffer);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    ItemStack.STREAM_CODEC.encode(buffer, book);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (book.getItem() instanceof ILecternBookItem) {
      ((ILecternBookItem)book.getItem()).openLecternScreenClient(pos, book);
    }
  }
}

