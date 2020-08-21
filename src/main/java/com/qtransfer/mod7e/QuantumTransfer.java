package com.qtransfer.mod7e;

import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Constant.MODID, name = Constant.NAME, version = Constant.VERSION)
public class QuantumTransfer
{

    @Mod.Instance(Constant.MODID)
    public static QuantumTransfer instance;

    private static Logger logger;

    @SidedProxy(clientSide = "com.qtransfer.mod7e.proxy.ClientProxy", serverSide = "com.qtransfer.mod7e.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityQEnergy.register();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

}
