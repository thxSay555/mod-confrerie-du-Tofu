package fr.wakfu.common.network;

import fr.wakfu.common.capabilities.RaceCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncRaceCapability implements IMessage {
    private String race;

    public SyncRaceCapability() {}
    public SyncRaceCapability(String race) { this.race = race; }

    @Override
    public void fromBytes(ByteBuf buf) {
        race = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, race);
    }

    // Correction : Utiliser SyncRaceCapability comme type de message
    public static class Handler implements IMessageHandler<SyncRaceCapability, IMessage> {
        @Override
        public IMessage onMessage(SyncRaceCapability message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                RaceCapability.IRace raceCap = player.getCapability(RaceCapability.RACE_CAPABILITY, null);
                if (raceCap != null) {
                    raceCap.setRace(message.race);
                    System.out.println("[Client] Race synchronisée : " + message.race);
                }
            });
            return null;
        }
    }
}