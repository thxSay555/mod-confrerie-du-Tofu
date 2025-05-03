package fr.wakfu.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UpdateStatsMessage implements IMessage {
    private NBTTagCompound data;

    public UpdateStatsMessage() {}

    public UpdateStatsMessage(NBTTagCompound data) {
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    public NBTTagCompound getData() {
        return data;
    }
}
