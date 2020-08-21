package com.qtransfer.mod7e.recipe;

import com.google.gson.JsonObject;
import com.qtransfer.mod7e.items.QuantumSuperpositionBall;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;

public class ConvertUpgradRecipe extends ShapedOreRecipe{

    public ConvertUpgradRecipe(@Nullable final ResourceLocation group, final ItemStack result, final CraftingHelper.ShapedPrimer primer) {
        super(group, result, primer);
    }

    @Override
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        final ItemStack output = super.getCraftingResult(inv); // Get the default output

        if (!output.isEmpty()) {
            NBTTagCompound nbt=new QuantumSuperpositionBall(output).serializeNBT();
            /*if(nbt==null)
                nbt=new NBTTagCompound();*/
            nbt.setBoolean("outfe",true);
            output.setTagCompound(nbt);
        }

        return output; // Return the modified output
    }

    @Override
    public String getGroup() {
        return group == null ? "" : group.toString();
    }


    public static class Factory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            final String group = JsonUtils.getString(json, "group", "");

            ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = recipe.getWidth();
            primer.height = recipe.getHeight();
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
            primer.input = recipe.getIngredients();
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

            return new ConvertUpgradRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
        }
    }
}
