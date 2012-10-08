package btwmods.api.player.listeners;

import java.util.EventListener;
import net.minecraft.src.ItemStack;

public interface IDropListener extends EventListener {
	void itemDropped(ItemStack items);
	void itemsDroppedAll();
}
