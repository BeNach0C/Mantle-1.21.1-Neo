package slimeknights.mantle.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import slimeknights.mantle.network.packet.ISimplePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkWrapper {
  private final ResourceLocation channelName;
  private final String version;
  private final List<Consumer<PayloadRegistrar>> packetRegistrations = new ArrayList<>();
  private boolean isClient = false;
  private boolean isServer = false;

  public NetworkWrapper(ResourceLocation channelName, String version) {
    this.channelName = channelName;
    this.version = version;
  }

  public NetworkWrapper playToClient() { 
    this.isClient = true;
    this.isServer = false;
    return this; 
  }
  
  public NetworkWrapper playToServer() { 
    this.isClient = false;
    this.isServer = true;
    return this; 
  }

  public <MSG extends ISimplePacket> void registerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> codec) {
    boolean client = this.isClient;
    boolean server = this.isServer;
    packetRegistrations.add(registrar -> {
      if (client && server) {
        registrar.playBidirectional(type, codec, (msg, context) -> msg.handle(context));
      } else if (client) {
        registrar.playToClient(type, codec, (msg, context) -> msg.handle(context));
      } else if (server) {
        registrar.playToServer(type, codec, (msg, context) -> msg.handle(context));
      }
    });
  }

  public void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(channelName.getNamespace()).versioned(version);
    for (Consumer<PayloadRegistrar> reg : packetRegistrations) {
      reg.accept(registrar);
    }
  }

  public void sendToServer(CustomPacketPayload msg) {
    PacketDistributor.sendToServer(msg);
  }

  public void sendVanillaPacket(Packet<?> packet, Entity player) {
    if (player instanceof ServerPlayer sPlayer) {
      sPlayer.connection.send(packet);
    }
  }

  public void sendTo(CustomPacketPayload msg, Player player) {
    if (player instanceof ServerPlayer && !(player instanceof FakePlayer)) {
      PacketDistributor.sendToPlayer((ServerPlayer) player, msg);
    }
  }

  public void sendToClientsAround(CustomPacketPayload msg, ServerLevel serverWorld, BlockPos position) {
    PacketDistributor.sendToPlayersTrackingChunk(serverWorld, serverWorld.getChunkAt(position).getPos(), msg);
  }

  public void sendToTrackingAndSelf(CustomPacketPayload msg, Entity entity) {
    PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, msg);
  }

  public void sendToTracking(CustomPacketPayload msg, Entity entity) {
    PacketDistributor.sendToPlayersTrackingEntity(entity, msg);
  }
}
