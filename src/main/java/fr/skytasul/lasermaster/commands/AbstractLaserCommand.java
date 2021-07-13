package fr.skytasul.lasermaster.commands;

import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;

public abstract class AbstractLaserCommand {
	
	public static final SimpleCommandExceptionType UNKNOWN_LASER = new SimpleCommandExceptionType(new LiteralMessage("Unknown laser."));
	public static final SimpleCommandExceptionType EXISTING_LASER = new SimpleCommandExceptionType(new LiteralMessage("This laser already exists."));
	
	private CommandNode<CommandSender> commandNode;
	
	public final CommandNode<CommandSender> getCommandNode() {
		if (commandNode == null) commandNode = computeCommandNode();
		return commandNode;
	}
	
	public abstract CommandNode<CommandSender> computeCommandNode();
	
	public static World getWorld(CommandSender sender) {
		if (sender instanceof BlockCommandSender block) return block.getBlock().getWorld();
		if (sender instanceof Entity entity) return entity.getWorld();
		return null;
	}
	
}
