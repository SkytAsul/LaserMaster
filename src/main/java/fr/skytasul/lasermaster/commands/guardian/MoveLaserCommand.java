package fr.skytasul.lasermaster.commands.guardian;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.karuslabs.commons.command.tree.nodes.Argument;
import com.karuslabs.commons.command.tree.nodes.Literal;
import com.karuslabs.commons.command.types.PointType;
import com.karuslabs.commons.command.types.WordType;
import com.karuslabs.commons.util.Point;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import fr.skytasul.lasermaster.LaserMaster;
import fr.skytasul.lasermaster.commands.AbstractLaserCommand;
import fr.skytasul.lasermaster.lasers.Move;
import fr.skytasul.lasermaster.lasers.RunningLaser;

public class MoveLaserCommand extends AbstractLaserCommand {
	
	private Argument<CommandSender, Point> endArg = Argument.of("end", PointType.CUBIC).build();
	private Argument<CommandSender, Integer> durationArg = Argument.of("duration", IntegerArgumentType.integer(1)).build();
	private Argument<CommandSender, String> otherArg = Argument.of("other", StringArgumentType.greedyString()).build();
	
	@Override
	public CommandNode<CommandSender> computeCommandNode() {
		return Literal.of("movelaser")
				.then(Argument.of("name", WordType.WORD).suggests((context, builder) -> {
					LaserMaster.getInstance().getGuardianLasers().getLasers().stream().map(RunningLaser::getName).filter(x -> x.startsWith(builder.getRemaining())).forEach(builder::suggest);
					return builder.buildFuture();
				})
						.then(Argument.of("repeat", IntegerArgumentType.integer(0))
								.then(Argument.of("endduration", IntegerArgumentType.integer(0))
										.then(endArg.createBuilder()
												.then(durationArg.createBuilder()
														.then(otherArg.createBuilder()
																.executes(context -> execute(context, true)))
														.executes(context -> execute(context, false))).build()))))
				.requires(x -> x.hasPermission("lasermover.move"))
				.description("Moves an existing laser.")
				.build();
	}
	
	private int execute(CommandContext<CommandSender> context, boolean hasMore) throws CommandSyntaxException {
		String name = context.getArgument("name", String.class);
		RunningLaser laser = LaserMaster.getInstance().getGuardianLasers().getLaser(name);
		if (laser == null) throw UNKNOWN_LASER.create();
		World world = getWorld(context.getSource());
		if (world == null) world = Bukkit.getWorld("Park");
		int repeat = IntegerArgumentType.getInteger(context, "repeat");
		int endDuration = IntegerArgumentType.getInteger(context, "endduration");
		
		try {
			LinkedList<Move> moves = new LinkedList<>();
			Location end = context.getArgument("end", Point.class);
			end.setWorld(world);
			int duration = IntegerArgumentType.getInteger(context, "duration");
			moves.add(new Move(end, duration));
			if (hasMore) parseMove(moves, world, context.getSource(), StringArgumentType.getString(context, "other"));
			try {
				laser.move(repeat, moves, endDuration);
				context.getSource().sendMessage("§7➤ §aLaser \"%s\" successfully moved.".formatted(laser.getName()));
			}catch (Exception ex) {
				ex.printStackTrace();
				context.getSource().sendMessage("§7➤ §cAn error ocurred while moving the laser \"%s\".".formatted(name));
			}
		}catch (CommandSyntaxException ex) {
			context.getSource().sendMessage("§7➤ §cAn error ocurred while parsing moves.");
			throw ex;
		}catch (Exception ex) {
			ex.printStackTrace();
			context.getSource().sendMessage("§7➤ §cAn unexpected error ocurred while parsing moves.");
		}
		return 1;
	}
	
	private void parseMove(List<Move> moves, World world, CommandSender sender, String more) throws CommandSyntaxException {
		StringReader reader = new StringReader(more);
		CommandContextBuilder<CommandSender> contextBuilder = new CommandContextBuilder<>(LaserMaster.getInstance().getDispatcher(), sender, endArg, 0);
		endArg.parse(reader, contextBuilder);
		CommandContext<CommandSender> context = contextBuilder.build(more);
		Location end = context.getArgument("end", Point.class);
		end.setWorld(world);
		reader.skip();
		if (!reader.canRead()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().create();
		contextBuilder = new CommandContextBuilder<>(LaserMaster.getInstance().getDispatcher(), context.getSource(), durationArg, 0);
		durationArg.parse(reader, contextBuilder);
		context = contextBuilder.build(reader.getRemaining());
		int duration = IntegerArgumentType.getInteger(context, "duration");
		reader.skip();
		moves.add(new Move(end, duration));
		if (reader.getRemainingLength() > 0) {
			parseMove(moves, world, sender, reader.getRemaining());
		}
	}
	
}
