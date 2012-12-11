package btwmods.player;

public class SpawnPosition {
	
	public final int dimension;
	
	public final int x;
	public final int y;
	public final int z;
	
	public final float pitch;
	public final float yaw;
	
	public SpawnPosition(int x, int y, int z) {
		this(0, x, y, z, 0.0F, 0.0F);
	}
	
	public SpawnPosition(int x, int y, int z, float yaw, float pitch) {
		this(0, x, y, z, yaw, pitch);
	}
	
	public SpawnPosition(int dimension, int x, int y, int z) {
		this(dimension, x, y, z, 0.0F, 0.0F);
	}
	
	public SpawnPosition(int dimension, int x, int y, int z, float yaw, float pitch) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
