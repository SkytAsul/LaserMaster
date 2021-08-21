package fr.skytasul.lasermaster.commands.guardian;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import com.karuslabs.commons.command.tree.nodes.Argument;
import com.karuslabs.commons.command.tree.nodes.Literal;
import com.karuslabs.commons.command.types.PointType;
import com.karuslabs.commons.command.types.VectorType;
import com.karuslabs.commons.command.types.WordType;
import com.karuslabs.commons.util.Point;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import fr.skytasul.guardianbeam.Laser.LaserType;
import fr.skytasul.lasermaster.commands.AbstractLaserCommand;
import fr.skytasul.lasermaster.lasers.RunningLaser;
import fr.skytasul.lasermaster.lasers.RunningLaserManager;

public class SummonLaserCommand extends AbstractLaserCommand {
	
	private RunningLaserManager laserManager;
	private Literal<CommandSender> node;
	
	public SummonLaserCommand(String name, String permission, String description, RunningLaserManager laserManager) {
		this.laserManager = laserManager;
		this.node = Literal.of(name)
				.then(Argument.of("name", WordType.WORD)
						.then(Argument.of("start", PointType.CUBIC)
								.then(Argument.of("end", PointType.CUBIC)
										.then(Argument.of("amount", IntegerArgumentType.integer(1))
												.then(Argument.of("duration", IntegerArgumentType.integer(1))
														.optionally(Argument.of("start-spread", VectorType.CUBIC)
																.then(Argument.of("end-spread", VectorType.CUBIC)
																		.executes(context -> execute(context, true))))
														.executes(context -> execute(context, false)))))))
				.requires(x -> x.hasPermission(permission))
				.description(description)
				.build();
	}
	
	@Override
	public CommandNode<CommandSender> computeCommandNode() {
		return node;
	}
	
	private int execute(CommandContext<CommandSender> context, boolean hasSpread) throws CommandSyntaxException {
		String name = context.getArgument("name", String.class);
		if (laserManager.getLaser(name) != null) {
			throw EXISTING_LASER.create();
		}
		World world = getWorld(context.getSource());
		if (world == null) world = Bukkit.getWorld("Park");
		Location start = context.getArgument("start", Point.class);
		start.setWorld(world);
		Location end = context.getArgument("end", Point.class);
		end.setWorld(world);
		int amount = IntegerArgumentType.getInteger(context, "amount");
		int duration = IntegerArgumentType.getInteger(context, "duration");
		Vector startSpread = hasSpread ? context.getArgument("start-spread", Vector.class) : new Vector();
		Vector endSpread = hasSpread ? context.getArgument("end-spread", Vector.class) : new Vector();
		try {
			laserManager.addLaser(RunningLaser.build(LaserType.GUARDIAN, name, start, end, amount, startSpread, endSpread, duration));
			context.getSource().sendMessage("§7➤ §a%d lasers started under the name \"%s\".".formatted(amount, name));
		}catch (Exception e) {
			e.printStackTrace();
			context.getSource().sendMessage("§7➤ §cAn error ocurred while starting the lasers \"%s\".".formatted(name));
		}
		return amount;
	}
	
}
