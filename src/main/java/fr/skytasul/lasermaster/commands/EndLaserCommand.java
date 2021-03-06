package fr.skytasul.lasermaster.commands;

import org.bukkit.command.CommandSender;

import com.karuslabs.commons.command.tree.nodes.Argument;
import com.karuslabs.commons.command.tree.nodes.Literal.Builder;
import com.karuslabs.commons.command.types.WordType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import fr.skytasul.lasermaster.lasers.RunningLaser;
import fr.skytasul.lasermaster.lasers.RunningLaserManager;

public class EndLaserCommand extends AbstractLaserCommand {
	
	public EndLaserCommand(String name, String laserName, RunningLaserManager laserManager) {
		super(name, laserManager, laserName);
	}
	
	@Override
	public CommandNode<CommandSender> computeCommandNode(Builder<CommandSender> builder) {
		return builder 
				.then(Argument.of("name", WordType.word())
						.suggests(super::suggestLaser)
						.executes(this::execute))
				.requires(x -> x.hasPermission("lasermover.end"))
				.description("Ends an existing " + laserName)
				.build();
	}
	
	private int execute(CommandContext<CommandSender> context) throws CommandSyntaxException {
		String name = context.getArgument("name", String.class);
		RunningLaser laser = laserManager.getLaser(name);
		if (laser == null) throw UNKNOWN_LASER.create();
		try {
			laserManager.endLaser(laser);
			context.getSource().sendMessage("§7➤ §aThe %s \"%s\" has successfully stopped.".formatted(laserName, laser.getName()));
		}catch (Exception ex) {
			ex.printStackTrace();
			context.getSource().sendMessage("§7➤ §cAn error ocurred while stopping the %s \"%s\".".formatted(laserName, name));
		}
		return 1;
	}
	
}
