package slimeknights.mantle.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.Mantle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

import slimeknights.mantle.util.OffhandCooldownTracker;

/** Packet to tell a client to swing an entity arm, as the vanilla one resets cooldown */
public class SwingArmPacket implements IThreadsafePacket {
  public static final Type<SwingArmPacket> ID = new Type<>(Mantle.getResource("swing_arm"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SwingArmPacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), SwingArmPacket::new);

  @Override
  public Type<SwingArmPacket> type() {
    return ID;
  }

  private final int entityId;
  private final InteractionHand hand;

  public SwingArmPacket(Entity entity, InteractionHand hand) {
    this.entityId = entity.getId();
    this.hand = hand;
  }

  public SwingArmPacket(RegistryFriendlyByteBuf buffer) {
    this.entityId = buffer.readVarInt();
    this.hand = buffer.readEnum(InteractionHand.class);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(entityId);
    buffer.writeEnum(hand);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(SwingArmPacket packet) {
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        Entity entity = world.getEntity(packet.entityId);
        if (entity instanceof LivingEntity) {
          OffhandCooldownTracker.swingHand((LivingEntity) entity, packet.hand, false);
        }
      }
    }
  }
}

