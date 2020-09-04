package com.qtransfer.mod7e.entity.render;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.entity.QRobotEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class QRobotRenderer extends EntityOBJRender<QRobotEntity> {
    public final ResourceLocation RES_MODEL=new ResourceLocation(Constant.MODID, "entity/qrobot_opt2.obj");

    public QRobotRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getModel() {
        return RES_MODEL;
    }

    @Override
    public void doRender(QRobotEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
