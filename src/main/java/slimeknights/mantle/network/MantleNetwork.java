package slimeknights.mantle.network;


import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferPacket;
import slimeknights.mantle.network.packet.DropLecternBookPacket;
import slimeknights.mantle.network.packet.OpenLecternBookPacket;
import slimeknights.mantle.network.packet.OpenNamedBookPacket;
import slimeknights.mantle.network.packet.SwingArmPacket;
import slimeknights.mantle.network.packet.UpdateHeldPagePacket;
import slimeknights.mantle.network.packet.UpdateInventoryPagePacket;
import slimeknights.mantle.network.packet.UpdateLecternPagePacket;

public class MantleNetwork {
  /**
   * Network instance
   * 1: 1.11.101 and before
   * 2: 1.11.102 - New predicate types, enum loadable nullable field optimization
   * 3: 1.11.108 - New export book command
   */
  public static final NetworkWrapper INSTANCE = new NetworkWrapper(Mantle.getResource("network"), "3");

  /**
   * Registers packets into this network
   */
  public static void registerPackets() {
    INSTANCE.playToClient().registerPacket(OpenLecternBookPacket.ID, OpenLecternBookPacket.CODEC);
    INSTANCE.playToServer().registerPacket(UpdateHeldPagePacket.ID, UpdateHeldPagePacket.CODEC);
    INSTANCE.playToServer().registerPacket(UpdateInventoryPagePacket.ID, UpdateInventoryPagePacket.CODEC);
    INSTANCE.playToServer().registerPacket(UpdateLecternPagePacket.ID, UpdateLecternPagePacket.CODEC);
    INSTANCE.playToServer().registerPacket(DropLecternBookPacket.ID, DropLecternBookPacket.CODEC);
    INSTANCE.playToClient().registerPacket(SwingArmPacket.ID, SwingArmPacket.CODEC);
    INSTANCE.playToClient().registerPacket(OpenNamedBookPacket.ID, OpenNamedBookPacket.CODEC);
    INSTANCE.playToClient().registerPacket(FluidContainerTransferPacket.ID, FluidContainerTransferPacket.CODEC);
  }
}
