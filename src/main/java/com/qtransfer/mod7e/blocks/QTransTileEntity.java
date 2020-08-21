package com.qtransfer.mod7e.blocks;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class QTransTileEntity extends TileEntity {

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        return super.getCapability(cap, facing);
    }

    //Network
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        // 第一个参数是要同步的 TileEntity 它自己。
        // 第二个参数是幻数，Forge patch 后的实现中，如果不是原版的 TileEntity，这个参数就没有意义。
        // 第三个就是要同步的数据了，你会在 onDataPacket 中拿到它。
        return new SPacketUpdateTileEntity(this.getPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {

        handleUpdateTag(packet.getNbtCompound());
        //System.out.println("update"+tank.getFluidAmount());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        return writeToNBT(nbtTagCompound);
    }
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    public void syncToTrackingClients() {
        if (!this.world.isRemote) {
            SPacketUpdateTileEntity packet = this.getUpdatePacket();
            // 获取当前正在“追踪”目标 TileEntity 所在区块的玩家。
            // 之所以这么做，是因为在逻辑服务器上，不是所有的玩家都需要获得某个 TileEntity 更新的信息。
            // 比方说，有一个玩家和需要同步的 TileEntity 之间差了八千方块，或者压根儿就不在同一个维度里。
            // 这个时候就没有必要同步数据——强行同步数据实际上也没有什么用，因为大多数时候这样的操作都应会被
            // World.isBlockLoaded（func_175667_e）的检查拦截下来，避免意外在逻辑客户端上加载多余的区块。
            PlayerChunkMapEntry trackingEntry = ((WorldServer)this.world).getPlayerChunkMap().getEntry(this.pos.getX() >> 4, this.pos.getZ() >> 4);
            if (trackingEntry != null) {
                for (EntityPlayerMP player : trackingEntry.getWatchingPlayers()) {
                    player.connection.sendPacket(packet);
                }
            }
        }
    }
}
