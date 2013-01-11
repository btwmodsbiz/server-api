package btwmods.player;

import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerActionEvent extends APIEvent implements IEventInterrupter {
	
	public enum TYPE { ATTACKED_BY_PLAYER, PLAYER_USE_ENTITY_ATTEMPT, PLAYER_USE_ENTITY, USE_ITEM_ATTEMPT, USE_ITEM };
	
	private TYPE type;
	private Entity entity = null;
	private EntityPlayer player = null;
	private boolean isLeftClick = false;
	private ItemStack itemStack = null;
	private ItemStack originalItemStack;
	private int originalQuantity;
	private int originalDamage;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	
	public boolean isHandled() {
		return (type == TYPE.PLAYER_USE_ENTITY_ATTEMPT) && isHandled;
	}
	
	public void markHandled() {
		isHandled = true;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void markNotAllowed() {
		isAllowed = false;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public boolean isLeftClick() {
		return isLeftClick;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public ItemStack getOriginalItemStack() {
		return originalItemStack;
	}
	
	public int getOriginalItemStackQuantity() {
		return originalQuantity;
	}
	
	public int getOriginalItemStackDamage() {
		return originalDamage;
	}
	
	public static PlayerActionEvent AttackedByPlayer(Entity entity, EntityPlayer player) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.ATTACKED_BY_PLAYER);
		event.entity = entity;
		return event;
	}

	public static PlayerActionEvent UseEntityAttempt(EntityPlayer player, Entity entity, boolean isLeftClick) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.PLAYER_USE_ENTITY_ATTEMPT);
		event.entity = entity;
		event.isLeftClick = isLeftClick;
		return event;
	}

	public static PlayerActionEvent UseEntity(EntityPlayer player, Entity entity, boolean isLeftClick) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.PLAYER_USE_ENTITY);
		event.entity = entity;
		event.isLeftClick = isLeftClick;
		return event;
	}

	public static PlayerActionEvent ItemUseAttempt(EntityPlayer player, ItemStack itemStack) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.USE_ITEM_ATTEMPT);
		event.itemStack = itemStack;
		return event;
	}

	public static PlayerActionEvent ItemUse(EntityPlayer player, ItemStack itemStack, ItemStack originalItemStack, int originalQuantity, int originalDamage) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.USE_ITEM);
		event.itemStack = itemStack;
		event.originalItemStack = originalItemStack;
		event.originalQuantity = originalQuantity;
		event.originalDamage = originalDamage;
		return event;
	}
	
	private PlayerActionEvent(EntityPlayer player, TYPE type) {
		super(player);
		this.type = type;
		this.player = player;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
