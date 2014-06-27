package tsuteto.tofu.tileentity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tsuteto.tofu.api.tileentity.ITfConsumer;
import tsuteto.tofu.api.tileentity.TileEntityTfMachineBase;
import tsuteto.tofu.block.*;
import tsuteto.tofu.util.TileScanner;

import java.util.Random;

public class TileEntityTfSaturator extends TileEntityTfMachineBase implements ITfConsumer
{
    private static final double TF_CAPACITY = 1D;
    private static final double tfNeededPerTick = 0.05D;
    private static final int radius = 16;

    private final Random machineRand = new Random();
    private double tfAmount = 0D;
    private int interval = getNextInterval();
    private int tickCounter = 0;
    private int step = 0;
    private boolean isProcessingLastTick = false;

    @Override
    protected String getInventoryNameTranslate()
    {
        return "container.tofucraft.TfSaturator";
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setFloat("amount", (float) tfAmount);
        nbtTagCompound.setShort("tick", (short) tickCounter);
        nbtTagCompound.setShort("intv", (short) interval);
        nbtTagCompound.setShort("step", (byte) step);
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        tfAmount = nbtTagCompound.getFloat("amount");
        tickCounter = nbtTagCompound.getShort("tick");
        interval = nbtTagCompound.getShort("intv");
        step = nbtTagCompound.getByte("step");
    }

    public boolean isProcessing()
    {
        return tfAmount >= tfNeededPerTick && this.isRedstonePowered();
    }

    /**
     * Update processing by a tick
     */
    @Override
    public void updateEntity()
    {
        if (worldObj.isRemote) return;

        boolean isProcessing = isProcessing();
        if (isProcessing)
        {
            if (tickCounter >= interval)
            {
                saturateAround();
                tickCounter = 0;
                interval = getNextInterval();
            }

            tickCounter++;
            tfAmount -= tfNeededPerTick;
            //ModLog.debug("t=%d, i=%d, s=%d", tickCounter, interval, step);
        }

        if (isProcessingLastTick != isProcessing())
        {
            BlockTfSaturator.updateMachineState(isProcessing(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        isProcessingLastTick = isProcessing();
    }

    public void saturateAround()
    {
        TileScanner scanner = new TileScanner(worldObj, xCoord, yCoord, zCoord);

        int len = Math.min(step * 2, radius);
        scanner.scan(len, TileScanner.Method.whole, new TileScanner.Impl()
        {
            public void apply(World world, int x, int y, int z)
            {
                Block b = world.getBlock(x, y, z);
                if (b instanceof BlockTofu)
                {
                    BlockTofu tofu = (BlockTofu)b;
                    if (tofu.canDrain() && tofu.isUnderWeight(worldObj, x, y, z))
                    {
                        tofu.drainOneStep(worldObj, x, y, z, machineRand);
                    }
                }
            }
        });

        if (++step * 2 > radius) step = 1;
    }

    private int getNextInterval()
    {
        return 200 + machineRand.nextInt(400);
    }

    @Override
    public double getMaxTfCapacity()
    {
        return Math.min(0.1, TF_CAPACITY - tfAmount);
    }

    @Override
    public double getCurrentTfConsumed()
    {
        return this.isRedstonePowered() ? tfNeededPerTick : 0;
    }

    @Override
    public void chargeTf(double amount)
    {
        tfAmount += amount;
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2)
    {
        return false;
    }
}
