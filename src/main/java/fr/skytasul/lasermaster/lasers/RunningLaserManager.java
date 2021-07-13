package fr.skytasul.lasermaster.lasers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunningLaserManager {
	
	private List<RunningLaser> lasers = Collections.synchronizedList(new ArrayList<>());
	
	public void addLaser(RunningLaser laser) {
		lasers.add(laser);
		laser.start();
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
