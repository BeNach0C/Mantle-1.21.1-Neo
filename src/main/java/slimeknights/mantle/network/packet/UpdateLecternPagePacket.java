package slimeknights.mantle.network.packet;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.Mantle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.util.BlockEntityHelper;

/**
 * Packet to update the book page in a lectern
 */
@AllArgsConstructor
public class UpdateLecternPagePacket implements IThreadsafePacket {
  public static final Type<UpdateLecternPagePacket> ID = new Type<>(Mantle.getResource("update_lectern_page"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateLecternPagePacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), UpdateLecternPagePacket::new);

  @Override
  public Type<UpdateLecternPagePacket> type() {
    return ID;
  }

  private final BlockPos pos;
  private final String page;
  public UpdateLecternPagePacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.page = buffer.readUtf(100);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeUtf(page);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Player player = (context.player() instanceof ServerPlayer sp ? sp : null);
    if (player != null && this.page != null) {
      Level world = player.getCommandSenderWorld();
      if (BlockEntityHelper.isBlockLoaded(world, pos)) {
        if (world.getBlockEntity(pos) instanceof LecternBlockEntity te) {
          ItemStack stack = te.getBook();
          if (!stack.isEmpty()) {
            BookHelper.writeSavedPageToBook(stack, this.page);
          }
        } else {
          Mantle.logger.error("Failed to find lectern at {} to update page for {}.", pos, player.getScoreboardName());
        }
      } else {
        Mantle.logger.error("Attempted to update lectern page at {} for {}, but world is not loaded", pos, player.getScoreboardName());
      }
    }
  }
}

