package fr.wakfu.items;

import com.google.common.collect.Multimap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShield;
public class IopShieldItem extends ItemShield { // Au lieu de Item
    public IopShieldItem() {
        super(); // Utilisez le ToolMaterial
        setRegistryName("iopshield");
        setUnlocalizedName("iopshield");
        setCreativeTab(CreativeTabs.COMBAT);
        // setMaxDamage(8000); // INUTILE : La durabilité vient de TOFU_MATERIAL
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        target.setFire((int) attacker.getHealth());
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

    @SuppressWarnings("deprecation")
	@Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 2.0, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 1, 0)); // 
        }
        return modifiers;
    }
}
