package fr.skytasul.lasermaster.lasers;

import org.bukkit.Location;

import fr.skytasul.lasermaster.DefaultVector;

public record Move(Location end, int duration, DefaultVector spread) {}