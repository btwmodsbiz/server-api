package btwmods.player;

import java.util.EventObject;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;

public class PlayerActionEvent extends EventObject {
	
	public enum TYPE { ATTACK };
	
	private TYPE type;
	private EntityLiving attackTarget = null;
	private DamageSource attackSource = null;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityLiving getAttackTarget() {
		return attackTarget;
	}
	
	public DamageSource getAttackSource() {
		return attackSource;
	}
	
	public static PlayerActionEvent Attack(EntityLiving targetEntity, DamageSource source) {
		PlayerActionEvent event = new PlayerActionEvent(source.getEntity(), TYPE.ATTACK);
		event.attackTarget = targetEntity;
		event.attackSource = source;
		return event;
	}
	
	private PlayerActionEvent(Object source, TYPE type) {
		super(source);
		this.type = type;
	}
}
