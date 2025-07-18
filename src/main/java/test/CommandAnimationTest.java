package test;

import fr.wakfu.WakfuMod;
import fr.wakfu.network.WakfuNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import test.PacketAnimationControl.Type;

/**
 * /animation_test <animName>
 */
public class CommandAnimationTest extends CommandBase {
    @Override public String getName()      { return "animation_test"; }
    @Override public String getUsage(ICommandSender sender) {
        return "/animation_test <animationName>";
    }
    

    @Override
    public void execute(MinecraftServer server,
                        ICommandSender sender,
                        String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(
                "Usage: " + getUsage(sender)
            ));
            sender.sendMessage(new TextComponentString(
                "Liste: " + AnimationLoader.listAnimations()
            ));
            return;
        }
        String name = args[0];
        if (AnimationLoader.getAnimation(name) == null) {
            sender.sendMessage(new TextComponentString(
                "Animation inconnue: " + name
            ));
            sender.sendMessage(new TextComponentString(
                "Liste: " + AnimationLoader.listAnimations()
            ));
            return;
        }

        String player = sender.getName();
        AnimationInstance inst =
            WakfuMod.proxy.getAnimationManager()
                         .startAnimation(player, name);
        if (inst == null) {
            sender.sendMessage(new TextComponentString(
                "Impossible de lancer: " + name
            ));
        } else {
            // 1️⃣ On diffuse le START à tous les clients
            WakfuNetwork.sendToAll(
                new PacketAnimationControl(player, name, Type.START)
            );

            sender.sendMessage(new TextComponentString(
                "Playing animation: " + name
            ));
        }
    }
}
