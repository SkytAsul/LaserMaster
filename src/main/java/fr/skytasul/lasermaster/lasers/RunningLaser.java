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
import fr.skytasul.guardianbeam.Laser.LaserType;
import fr.skytasul.lasermaster.DefaultVector;
import fr.skytasul.lasermaster.LaserMaster;

public class RunningLaser {
	
	private final String name;
	private final Vector originEndSpread;
	private final Location originEnd;
	
	private Vector endSpread;
	private List<Laser> lasers;
	
	private Set<Laser> moving;
	private RunningLaserManager manager;
	
	private RunningLaser(String name, Vector spread, Location originEnd, List<Laser> lasers) {
		this.name = name;
		this.originEndSpread = spread;
		this.endSpread = spread;
		this.originEnd = originEnd;
		this.lasers = lasers;
		
		moving = new HashSet<>(lasers.size() + 1, 1);
	}
	
	public String getName() {
		return name;
	}
	
	void start(RunningLaserManager manager) {
		this.manager = manager;
		lasers.forEach(x -> x.start(LaserMaster.getInstance()));
	}
	
	void end() {
		new ArrayList<>(lasers).forEach(Laser::stop);
	}
	
	public void move(int repeat, LinkedList<Move> moves, int endDuration) {
		moves.add(new Move(originEnd, endDuration, new DefaultVector(originEndSpread)));
		moveInternal((repeat + 1) * moves.size() - 1, moves, endDuration);
	}
	
	private void moveInternal(int repeat, LinkedList<Move> moves, int endDuration) {
		/*if (repeat == -1) {
			moves.clear();
			moves.add(new Move(originEnd, endDuration, originEndSpread));
		}else if (repeat == -2) return;*/
		if (repeat == -1) return;
		if (moves.isEmpty()) return;
		Move move = moves.removeFirst();
		int amountSplit = lasers.size() / 2;
		if (move.spread() != null) endSpread = move.spread().toVector(endSpread);
		Location end = move.end().clone().subtract(endSpread.clone().multiply(amountSplit));
		for (Iterator<Laser> iterator = lasers.iterator(); iterator.hasNext();) {
			Laser laser = iterator.next();
			moving.add(laser);
			laser.moveEnd(end.add(endSpread), move.duration(), () -> {
				moving.remove(laser);
				if (moving.isEmpty()) {
					moves.add(move);
					moveInternal(repeat - 1, moves, endDuration);
				}
			});
		}
	}
	
	public static RunningLaser build(LaserType type, String name, Location start, Location end, int amount, Vector startSpread, Vector endSpread, int duration) throws ReflectiveOperationException {
		List<Laser> lasers = Collections.synchronizedList(new ArrayList<>());
		int amountSplit = amount / 2;
		Location newEnd = end.clone().subtract(endSpread.clone().multiply(amountSplit));
		Location newStart = start.clone().subtract(startSpread.clone().multiply(amountSplit));
		for (int i = 0; i < amount; i++) {
			lasers.add(type.create(newStart.add(startSpread), newEnd.add(endSpread).clone(), duration, 10).durationInTicks());
		}
		RunningLaser runningLaser = new RunningLaser(name, endSpread, end, lasers);
		lasers.forEach(laser -> laser.executeEnd(() -> {
			lasers.remove(laser);
			if (lasers.isEmpty()) runningLaser.manager.endLaser(runningLaser);
		}));
		return runningLaser;
	}
	
}
