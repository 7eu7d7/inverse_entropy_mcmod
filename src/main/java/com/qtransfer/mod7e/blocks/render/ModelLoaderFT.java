package com.qtransfer.mod7e.blocks.render;

import com.qtransfer.mod7e.Constant;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelLoaderFT implements ICustomModelLoader{

    public final String MODEL_RESOURCE_LOCATION = "fluid_tank_model";

    private IResourceManager resourceManager;

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation resourceLocation) {
        return resourceLocation.getResourceDomain().equals(Constant.MODID)
                && resourceLocation.getResourcePath().contains(MODEL_RESOURCE_LOCATION);
    }

    @Override
    public IModel loadModel(ResourceLocation resourceLocation) throws Exception {
        String resourcePath = resourceLocation.getResourcePath();
        if (!resourcePath.startsWith(MODEL_RESOURCE_LOCATION)) {
            assert false : "loadModel expected " + MODEL_RESOURCE_LOCATION + " but found " + resourcePath;
        }
        System.out.println("load_model "+resourcePath);

        if (resourcePath.equals(MODEL_RESOURCE_LOCATION)) {
            return new FluidTankModel();
        } else {
            return ModelLoaderRegistry.getMissingModel();
        }
    }
}
