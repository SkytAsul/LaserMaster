package fr.skytasul.lasermaster.commands.crystal;

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
import fr.skytasul.lasermaster.LaserMaster;
import fr.skytasul.lasermaster.commands.AbstractLaserCommand;
import fr.skytasul.lasermaster.lasers.RunningLaser;

public class SummonBeamCommand extends AbstractLaserCommand {
	
	@Override
	public CommandNode<CommandSender> computeCommandNode() {
		return Literal.of("summonbeam")
				.then(Argument.of("name", WordType.WORD)
						.then(Argument.of("start", PointType.CUBIC)
								.then(Argument.of("end", PointType.CUBIC)
										.then(Argument.of("amount", IntegerArgumentType.integer(1))
												.then(Argument.of("duration", IntegerArgumentType.integer(1))
														.then(Argument.of("start-spread", VectorType.CUBIC)
																.optionally(Argument.of("end-spread", VectorType.CUBIC)
																		.executes(context -> execute(context, true)))
																.executes(context -> execute(context, false))))))))
				.requires(x -> x.hasPermission("lasermover.summon"))
				.description("Summons a new beam.")
				.build();
	}
	
	private int execute(CommandContext<CommandSender> context, boolean hasEndSpread) throws CommandSyntaxException {
		String name = context.getArgument("name", String.class);
		if (LaserMaster.getInstance().getCrystalLasers().getLaser(name) != null) {
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
		Vector startSpread = context.getArgument("start-spread", Vector.class);
		Vector endSpread = hasEndSpread ? context.getArgument("end-spread", Vector.class) : new Vector();
		try {
			LaserMaster.getInstance().getCrystalLasers().addLaser(RunningLaser.build(LaserType.ENDER_CRYSTAL, name, start, end, amount, startSpread, endSpread, duration));
			context.getSource().sendMessage("§7➤ §a%d beams started under the name \"%s\".".formatted(amount, name));
		}catch (Exception e) {
			e.printStackTrace();
			context.getSource().sendMessage("§7➤ §cAn error ocurred while starting the beams \"%s\".".formatted(name));
		}
		return amount;
	}
	
}
