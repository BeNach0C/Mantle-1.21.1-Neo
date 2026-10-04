package slimeknights.mantle.fluid.transfer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.core.registries.Registries;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import net.minecraft.network.codec.StreamCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import io.netty.buffer.ByteBuf;

/** Packet to sync fluid container transfer */
public class FluidContainerTransferPacket implements IThreadsafePacket {
  public static final Type<FluidContainerTransferPacket> ID = new Type<>(Mantle.getResource("fluid_container_transfer"));
  
  public static final StreamCodec<RegistryFriendlyByteBuf, FluidContainerTransferPacket> CODEC = StreamCodec.composite(
          ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.registry(Registries.ITEM)),
          p -> p.items,
          FluidContainerTransferPacket::new
  );

  private final Set<Item> items;

  public FluidContainerTransferPacket(Set<Item> items) {
    this.items = items;
  }

  @Override
  public Type<FluidContainerTransferPacket> type() {
    return ID;
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    CODEC.encode(buffer, this);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    FluidContainerTransferManager.INSTANCE.setContainerItems(items);
  }
}

