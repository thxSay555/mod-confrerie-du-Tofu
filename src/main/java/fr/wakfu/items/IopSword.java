package fr.wakfu.items;

import com.google.common.collect.Multimap;

import fr.wakfu.WakfuMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
public class IopSword extends ItemSword { // Au lieu de Item
    public IopSword() {
        super(WakfuMod.TOFU_MATERIAL); // Utilisez le ToolMaterial
        setRegistryName("iop_sword");
        setUnlocalizedName("iop_sword");
        setCreativeTab(CreativeTabs.COMBAT);
        
        // setMaxDamage(8000); // INUTILE : La durabilité vient de TOFU_MATERIAL
    }
    
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1.0, 1));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 1.4000000953674316D, 0));
        }

        return multimap;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public boolean canHarvestBlock(net.minecraft.block.state.IBlockState blockIn) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        return 4.5F;
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
