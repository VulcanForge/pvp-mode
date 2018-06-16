package pvpmode.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import pvpmode.*;

public class PvPCommand extends CommandBase
{
    @Override
    public String getCommandName ()
    {
        return "pvp";
    }

    @Override
    public String getCommandUsage (ICommandSender sender)
    {
        return "/pvp [cancel]";
    }

    @Override
    public boolean canCommandSenderUseCommand (ICommandSender sender)
    {
        return true;
    }

    @Override
    public void processCommand (ICommandSender sender, String[] args)
    {
        EntityPlayerMP player = getCommandSenderAsPlayer (sender);
        EnumPvPMode currentMode = PvPUtils.getPvPMode (player);
        PvPData data = PvPUtils.getPvPData (player);

        if (args.length > 0)
        {
            if (args[0].equals ("cancel"))
            {
                cancelPvPTimer (player, currentMode, data);
            }
            else
            {
                PvPUtils.red (sender, getCommandUsage (sender));
            }
        }
        else
        {
            togglePvPMode (sender, data);
        }
    }

    private void cancelPvPTimer (EntityPlayer player, EnumPvPMode mode, PvPData data)
    {
        if (mode == EnumPvPMode.WARMUP)
        {
            data.setPvPWarmup (0);
            PvPUtils.yellow (player, "PvP warmup canceled.");
        }
        else
        {
            PvPUtils.yellow (player, "No PvP warmup to cancel.");
        }
    }

    private void togglePvPMode (ICommandSender sender, PvPData data)
    {
        long time = PvPUtils.getTime ();
        long toggleTime = time + PvPMode.warmup;
        long cooldownTime = data.getPvPCooldown ();

        if (cooldownTime > time)
        {
            long wait = cooldownTime - time;
            PvPUtils.yellow (sender, String.format ("Please wait %d seconds before issuing this command.", wait));
            return;
        }

        data.setPvPWarmup (toggleTime);
        data.setPvPCooldown (0);

        String status = data.isPvPEnabled () ? "disabled" : "enabled";
        PvPUtils.yellow (sender, String.format ("PvP will be %s in %d seconds...", status, PvPMode.warmup));
    }
}