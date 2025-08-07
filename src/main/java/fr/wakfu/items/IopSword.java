package fr.wakfu.items;

import java.util.UUID;

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
        super(WakfuMod.iop_sword_MATERIAL); // Utilisez le ToolMaterial
        setRegistryName(WakfuMod.MODID,"iop_sword");
        setUnlocalizedName("iop_sword");
        setCreativeTab(CreativeTabs.COMBAT);
        
        // setMaxDamage(8000); // INUTILE : La durabilité vient de TOFU_MATERIAL
    }
    
    private static final UUID ATTACK_DAMAGE_CUSTOM_UUID = UUID.fromString("a0a1a2a3-a4a5-a6a7-a8a9-aaabacadaeaf");
    private static final UUID ATTACK_SPEED_CUSTOM_UUID = UUID.fromString("b0b1b2b3-b4b5-b6b7-b8b9-babbbcbdbebf");

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            // Dégâts totaux = 12 ❤️
            modifiers.put(
                SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(
                    ATTACK_DAMAGE_CUSTOM_UUID, // UUID unique
                    "Custom Damage", 
                    5.0, 
                    0
                )
            );

            // Vitesse d'attaque = 4.5 (calcul correct)
            modifiers.put(
                SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(
                    ATTACK_SPEED_CUSTOM_UUID, // UUID unique
                    "Custom Speed", 
                    0.0, // 4.0 (base) + 0.5 = 4.5
                    0
                )
            );
        }
        return modifiers;
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
