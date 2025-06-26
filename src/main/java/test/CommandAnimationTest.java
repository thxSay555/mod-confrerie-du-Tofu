package test;

import fr.wakfu.WakfuMod;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.CommandBase;
import net.minecraft.util.text.TextComponentString;

/**
 * Command to test animations
 */
public class CommandAnimationTest extends CommandBase {
    @Override
    public String getName() {
        return "animation_test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/animation_test <animationName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }
        String name = args[0];
        WakfuMod.animationManager.startAnimation(sender.getName(), name);
        sender.sendMessage(new TextComponentString("Playing animation: " + name));
    }
}
