package fr.skytasul.lasermaster.lasers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.skytasul.guardianbeam.Laser;
import fr.skytasul.lasermaster.LaserMover;

public class RunningLaser {
	
	private String name;
	private Vector spread;
	private List<Laser> lasers;
	
	private Set<Laser> moving;
	
	private RunningLaser(String name, Vector spread, List<Laser> lasers) {
		this.name = name;
		this.spread = spread;
		this.lasers = lasers;
		
		moving = new HashSet<>(lasers.size(), 1);
	}
	
	public String getName() {
		return name;
	}
	
	void start() {
		lasers.forEach(x -> x.start(LaserMover.getInstance()));
	}
	
	void end() {
		new ArrayList<>(lasers).forEach(Laser::stop);
	}
	
	public void move(int repeat, LinkedList<Move> moves) {
		moveInternal((repeat + 1) * moves.size() - 1, moves);
	}
	
	private void moveInternal(int repeat, LinkedList<Move> moves) {
		if (repeat == -1) return;
		if (moves.isEmpty()) return;
		Move move = moves.removeFirst();
		int amountSplit = lasers.size() / 2;
		Location end = move.end().clone().subtract(spread.clone().multiply(amountSplit));
		for (Iterator<Laser> iterator = lasers.iterator(); iterator.hasNext();) {
			Laser laser = iterator.next();
			moving.add(laser);
			laser.moveEnd(end.add(spread), move.duration(), () -> {
				moving.remove(laser);
				if (moving.isEmpty()) {
					moves.add(move);
					moveInternal(repeat - 1, moves);
				}
			});
		}
	}
	
	public static RunningLaser build(String name, Location start, Location end, int amount, Vector spread, int duration) throws ReflectiveOperationException {
		List<Laser> lasers = Collections.synchronizedList(new ArrayList<>());
		int amountSplit = amount / 2;
		end.subtract(spread.clone().multiply(amountSplit));
		for (int i = 0; i < amount; i++) {
			lasers.add(new Laser(start, end.add(spread).clone(), duration, 100).durationInTicks());
		}
		RunningLaser runningLaser = new RunningLaser(name, spread, lasers);
		lasers.forEach(laser -> laser.executeEnd(() -> {
			lasers.remove(laser);
			if (lasers.isEmpty()) LaserMover.getInstance().getLasersManager().endLaser(runningLaser);
		}));
		return runningLaser;
	}
	
}
