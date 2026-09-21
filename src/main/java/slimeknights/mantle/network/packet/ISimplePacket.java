package slimeknights.mantle.network.packet;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet interface to add common methods for registration
 */
public interface ISimplePacket extends CustomPacketPayload {
  /**
   * Encodes a packet for the buffer
   * @param buf  Buffer instance
   */
  void encode(net.minecraft.network.RegistryFriendlyByteBuf buf);

  /**
   * Handles receiving the packet
   * @param context  Packet context
   */
  void handle(IPayloadContext context);
}
