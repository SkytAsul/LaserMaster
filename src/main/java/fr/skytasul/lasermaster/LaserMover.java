package fr.skytasul.lasermaster;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.karuslabs.commons.command.dispatcher.Dispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.skytasul.lasermaster.commands.AbstractLaserCommand;
import fr.skytasul.lasermaster.commands.EndLaserCommand;
import fr.skytasul.lasermaster.commands.MoveLaserCommand;
import fr.skytasul.lasermaster.commands.SummonLaserCommand;
import fr.skytasul.lasermaster.lasers.RunningLaserManager;

public class LaserMover extends JavaPlugin implements Listener {
	
	private static LaserMover instance;
	
	private List<AbstractLaserCommand> commands = Arrays.asList(new SummonLaserCommand(), new MoveLaserCommand(), new EndLaserCommand());
	private Dispatcher dispatcher;
	
	private RunningLaserManager lasersManager = new RunningLaserManager();
	
	@Override
	public void onEnable() {
		instance = this;
		
		getServer().getPluginManager().registerEvents(this, this);
		
		dispatcher = Dispatcher.of(this);
		commands.forEach(x -> dispatcher.getRoot().addChild(x.getCommandNode()));
		dispatcher.update();
	}
	
	@Override
	public void onDisable() {
		commands.forEach(x -> dispatcher.getRoot().removeChild(x.getCommandNode().getName()));
	}
	
	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	
	public RunningLaserManager getLasersManager() {
		return lasersManager;
	}
	
	@EventHandler
	public void onCommandBlock(ServerCommandEvent e) {
		if (e.getSender() instanceof BlockCommandSender block) {
			String cmdLabel = e.getCommand().split(" ")[0];
			if (commands.stream().noneMatch(x -> x.getCommandNode().getName().equals(cmdLabel))) return;
			e.setCancelled(true);
			try {
				getDispatcher().execute(e.getCommand(), block);
			}catch (CommandSyntaxException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static LaserMover getInstance() {
		return instance;
	}
	
}
