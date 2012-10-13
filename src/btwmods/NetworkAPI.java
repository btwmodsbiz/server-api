package btwmods;

import java.util.HashMap;
import java.util.Map;

import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;

public class NetworkAPI {
	
	public static final String BASE_CHANNEL_NAME = "BM|";
	private static Map<String, INetworkListener> networkListeners = new HashMap<String, INetworkListener>();
	
	private NetworkAPI() { }
	
	public static boolean registerCustomChannel(String channel, INetworkListener listener) {
		if (!networkListeners.containsKey(channel)) {
			networkListeners.put(channel, listener);
			return true;
		}
		return false;
	}
	
	public static boolean unregisterCustomChannel(String channel, INetworkListener listener) {
		if (networkListeners.containsKey(channel) && networkListeners.get(channel) == listener) {
			networkListeners.remove(channel);
			return true;
		}
		return false;
	}

	public static boolean handleCustomPayload(String channel, byte[] data, int length) {
		if (networkListeners.containsKey(channel)) {
			networkListeners.get(channel).customPacketAction(new CustomPacketEvent(channel, data, length));
		}
		return false;
	}
}
