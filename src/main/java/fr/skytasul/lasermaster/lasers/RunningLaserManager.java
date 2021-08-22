package fr.skytasul.lasermaster.lasers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.skytasul.guardianbeam.Laser.LaserType;

public class RunningLaserManager {
	
	private final LaserType laserType;
	
	private List<RunningLaser> lasers = Collections.synchronizedList(new ArrayList<>());
	
	public RunningLaserManager(LaserType laserType) {
		this.laserType = laserType;
	}
	
	public LaserType getLaserType() {
		return laserType;
	}
	
	public void addLaser(RunningLaser laser) {
		lasers.add(laser);
		laser.start(this);
	}
	
	public void endLaser(RunningLaser laser) {
		if (lasers.remove(laser)) laser.end();
	}
	
	public List<RunningLaser> getLasers() {
		return lasers;
	}
	
	public RunningLaser getLaser(String name) {
		return lasers.stream().filter(x -> x.getName().equals(name)).findAny().orElse(null);
	}
	
}
