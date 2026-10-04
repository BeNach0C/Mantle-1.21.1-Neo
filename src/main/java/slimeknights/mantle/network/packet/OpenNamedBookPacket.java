package slimeknights.mantle.network.packet;

import lombok.AllArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.Mantle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.command.client.BookCommand;

@AllArgsConstructor
public class OpenNamedBookPacket implements IThreadsafePacket {
  public static final Type<OpenNamedBookPacket> ID = new Type<>(Mantle.getResource("open_named_book"));
  public static final StreamCodec<RegistryFriendlyByteBuf, OpenNamedBookPacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), OpenNamedBookPacket::new);

  @Override
  public Type<OpenNamedBookPacket> type() {
    return ID;
  }

  private final ResourceLocation book;

  public OpenNamedBookPacket(RegistryFriendlyByteBuf buffer) {
    this.book = buffer.readResourceLocation();
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buf) {
    buf.writeResourceLocation(book);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    BookData bookData = BookLoader.getBook(book);
    if(bookData != null) {
      bookData.openGui(Component.literal("Book"), "", null, null);
    } else {
      ClientOnly.errorStatus(book);
    }
  }

  static class ClientOnly {
    static void errorStatus(ResourceLocation book) {
      BookCommand.bookNotFound(book);
    }
  }
}

