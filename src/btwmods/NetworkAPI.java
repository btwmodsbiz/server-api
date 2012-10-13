package btwmods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;

public class NetworkAPI {
	
	private static final String BASE_CHANNEL_NAME = "BM|";
	
	// Normal channel lookup (by channel extension).
	private static Map<String, INetworkListener> networkListeners = new HashMap<String, INetworkListener>();
	
	// Reverse channel lookup (by INetworkListener).
	private static Map<INetworkListener, Set<String>> channelListeners = new HashMap<INetworkListener, Set<String>>();
	
	private NetworkAPI() { }
	
	/**
	 * Registered a custom channel to a {@link INetworkListener}.
	 * 
	 * @param channelExtension the extension that will be added to the base channel name used by the API.
	 * @param listener the listener that implements {@link INetworkListener}.
	 * @return true if the channel was registered; false if the channel is already registered.
	 * @throws IllegalArgumentException if channelExtension is null or empty, or if listener is null.
	 */
	public static boolean registerCustomChannel(String channelExtension, INetworkListener listener) throws IllegalArgumentException {
		if (channelExtension == null || channelExtension.length() == 0)
			throw new IllegalArgumentException("channelExtension cannot be null or a zero length string");
		
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null or a zero length string");
		
		if (!networkListeners.containsKey(channelExtension)) {
			networkListeners.put(BASE_CHANNEL_NAME + channelExtension, listener);
			
			// Add the channel to a reversed map.
			Set channels;
			if ((channels = channelListeners.get(listener)) == null) {
				channelListeners.put(listener, channels = new HashSet<String>());
			}
			channels.add(channelExtension);
			
			return true;
		}
		return false;
	}
	
	/**
	 * Unregister all custom channels for a listener.
	 * 
	 * @param listener the listener that implements {@link INetworkListener}.
	 * @throws IllegalArgumentException if listener is null.
	 */
	public static void unregisterCustomChannels(IAPIListener listener) throws IllegalArgumentException {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null or a zero length string");
		
		// Remove the listener from the normal lookup.
		Set<String> channels = channelListeners.get(listener);
		if (channels != null) {
			for (String channelExtension : channelListeners.get(listener)) {
				networkListeners.remove(channelExtension);
			}
		}
		
		// Remove the listener from the reverse lookup.
		channelListeners.remove(listener);
	}
	
	/**
	 * Unregister a channel attached to a listener.
	 * 
	 * @param channelExtension the extension that will be added to the base channel name used by the API.
	 * @param listener the listener that implements {@link INetworkListener}.
	 * @return true if the channel was unregistered; false if a channel was not found by that name, or it is registered to another listener.
	 * @throws IllegalArgumentException if channelExtension is null or empty, or if listener is null.
	 */
	public static boolean unregisterCustomChannel(String channelExtension, INetworkListener listener) throws IllegalArgumentException {
		if (channelExtension == null || channelExtension.length() == 0)
			throw new IllegalArgumentException("channelExtension cannot be null or a zero length string");
		
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null or a zero length string");
		
		if (networkListeners.containsKey(channelExtension) && networkListeners.get(channelExtension) == listener) {
			networkListeners.remove(channelExtension);
			return true;
		}
		return false;
	}

	public static boolean handleCustomPayload(String channel, byte[] data, int length) {
		if (networkListeners.containsKey(channel)) {
			CustomPacketEvent event = new CustomPacketEvent(channel, data, length);
			networkListeners.get(channel).customPacketAction(event);
			return event.isHandled();
		}
		return false;
	}
}
