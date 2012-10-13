package btwmods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;

public class NetworkAPI {
	
	private static final String BASE_CHANNEL_NAME = "BM|";
	private static Map<String, INetworkListener> networkListeners = new HashMap<String, INetworkListener>();
	private static Map<INetworkListener, Set<String>> channelListeners = new HashMap<INetworkListener, Set<String>>();
	
	private NetworkAPI() { }
	
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
	
	public static void unregisterCustomChannels(INetworkListener listener) throws IllegalArgumentException {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null or a zero length string");
		
		// Remove the listener from the normal lookup.
		for (String channelExtension : channelListeners.get(listener)) {
			networkListeners.remove(channelExtension);
		}
		
		// Remove the listener from the reverse lookup.
		channelListeners.remove(listener);
	}
	
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
