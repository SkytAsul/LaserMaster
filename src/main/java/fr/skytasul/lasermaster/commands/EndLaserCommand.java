package fr.skytasul.lasermaster.commands;

import org.bukkit.command.CommandSender;

import com.karuslabs.commons.command.tree.nodes.Argument;
import com.karuslabs.commons.command.tree.nodes.Literal;
import com.karuslabs.commons.command.types.WordType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import fr.skytasul.lasermaster.LaserMover;
import fr.skytasul.lasermaster.lasers.RunningLaser;

public class EndLaserCommand extends AbstractLaserCommand {
	
	@Override
	public CommandNode<CommandSender> computeCommandNode() {
		return Literal.of("endlaser")
				.then(Argument.of("name", WordType.WORD).suggests((context, builder) -> {
					LaserMover.getInstance().getLasersManager().getLasers().stream().map(RunningLaser::getName).filter(x -> x.startsWith(builder.getRemaining())).forEach(builder::suggest);
					return builder.buildFuture();
				})
						.executes(this::execute))
				.requires(x -> x.hasPermission("lasermover.end"))
				.description("Ends an existing laser.")
				.build();
	}
	
	private int execute(CommandContext<CommandSender> context) throws CommandSyntaxException {
		String name = context.getArgument("name", String.class);
		RunningLaser laser = LaserMover.getInstance().getLasersManager().getLaser(name);
		if (laser == null) throw UNKNOWN_LASER.create();
		try {
			LaserMover.getInstance().getLasersManager().endLaser(laser);
			context.getSource().sendMessage("§7➤ §aLaser \"%s\" successfully stopped.".formatted(laser.getName()));
		}catch (Exception ex) {
			ex.printStackTrace();
			context.getSource().sendMessage("§7➤ §cAn error ocurred while stopping the laser \"%s\".".formatted(name));
		}
		return 1;
	}
	
}
