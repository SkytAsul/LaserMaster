package fr.skytasul.lasermaster;

import java.util.OptionalInt;

import org.bukkit.util.Vector;

public class DefaultVector {
	
	private OptionalInt x;
	private OptionalInt y;
	private OptionalInt z;
	
	public DefaultVector() {
		removeX();
		removeY();
		removeZ();
	}
	
	public DefaultVector(int x, int y, int z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public DefaultVector(Vector vector) {
		setX(vector.getBlockX());
		setY(vector.getBlockY());
		setZ(vector.getBlockZ());
	}
	
	public OptionalInt getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = OptionalInt.of(x);
	}
	
	public void removeX() {
		this.x = OptionalInt.empty();
	}
	
	public void setX(OptionalInt x) {
		this.x = x;
	}
	
	public OptionalInt getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = OptionalInt.of(y);
	}
	
	public void removeY() {
		this.y = OptionalInt.empty();
	}
	
	public void setY(OptionalInt y) {
		this.y = y;
	}
	
	public OptionalInt getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = OptionalInt.of(z);
	}
	
	public void removeZ() {
		this.z = OptionalInt.empty();
	}
	
	public void setZ(OptionalInt z) {
		this.z = z;
	}
	
	public DefaultVector setDefault(Vector toUse) {
		if (x.isEmpty()) setX(toUse.getBlockX());
		if (y.isEmpty()) setY(toUse.getBlockY());
		if (z.isEmpty()) setZ(toUse.getBlockZ());
		return this;
	}
	
	public Vector toVector(Vector defaultVector) {
		setDefault(defaultVector);
		return new Vector(x.getAsInt(), y.getAsInt(), z.getAsInt());
	}
	
}
