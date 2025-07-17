package fr.wakfu.items;

import fr.wakfu.WakfuMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
public class GoultardItem extends ItemSword { // Au lieu de Item
	
    public GoultardItem() {
    	
        super(WakfuMod.TOFU_MATERIAL); // Utilisez le ToolMaterial
        setRegistryName("goultard");
        setUnlocalizedName("goultard");
        setCreativeTab(CreativeTabs.COMBAT);
        
        // setMaxDamage(8000); // INUTILE : La durabilité vient de TOFU_MATERIAL
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(0, attacker);
        target.knockBack(attacker, 2.0F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        return true;
    }

    @Override
    public boolean canHarvestBlock(net.minecraft.block.state.IBlockState blockIn) {
        return false;
    }

  

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return false;
    }

    @Override
    public boolean isFull3D() {
        return true; // Pour bien l'afficher comme une vraie épée à la 3e personne
    }

  
}
