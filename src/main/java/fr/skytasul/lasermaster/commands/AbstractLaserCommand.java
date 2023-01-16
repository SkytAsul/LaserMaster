package fr.skytasul.lasermaster.commands;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.karuslabs.commons.command.tree.nodes.Literal;
import com.karuslabs.commons.command.tree.nodes.Literal.Builder;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;

import fr.skytasul.lasermaster.lasers.RunningLaser;
import fr.skytasul.lasermaster.lasers.RunningLaserManager;

public abstract class AbstractLaserCommand {
	
	public static final SimpleCommandExceptionType UNKNOWN_LASER = new SimpleCommandExceptionType(new LiteralMessage("Unknown laser."));
	public static final SimpleCommandExceptionType EXISTING_LASER = new SimpleCommandExceptionType(new LiteralMessage("This laser already exists."));
	
	private final String name;
	protected final RunningLaserManager laserManager;
	protected final String laserName;
	
	private CommandNode<CommandSender> commandNode;
	
	public AbstractLaserCommand(String name, RunningLaserManager laserManager, String laserName) {
		this.name = name;
		this.laserManager = laserManager;
		this.laserName = laserName;
	}
	
	public final CommandNode<CommandSender> getCommandNode() {
		if (commandNode == null) commandNode = computeCommandNode(Literal.of(name));
		return commandNode;
	}
	
	protected CompletableFuture<Suggestions> suggestLaser(CommandContext<CommandSender> context, SuggestionsBuilder builder) {
		laserManager.getLasers().stream().map(RunningLaser::getName).filter(x -> x.startsWith(builder.getRemaining())).forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	public abstract CommandNode<CommandSender> computeCommandNode(Builder<CommandSender> builder);
	
	public static World getWorld(CommandSender sender, String defaultWorld) {
		if (sender instanceof BlockCommandSender block) return block.getBlock().getWorld();
		if (sender instanceof Entity entity) return entity.getWorld();
		return Bukkit.getWorld(defaultWorld);
	}
	
}
