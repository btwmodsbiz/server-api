package btwmod.tickmonitor;

import java.io.IOException;
import java.lang.reflect.Type;

import net.minecraft.src.ChunkCoordIntPair;

import btwmods.Util;
import btwmods.measure.Average;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TypeAdapters {
	
	public static class ClassAdapter implements JsonSerializer<Class> {

		@Override
		public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src.getSimpleName());
		}
		
	}
	
	public static class ChunkCoordIntPairAdapter implements JsonSerializer<ChunkCoordIntPair> {

		@Override
		public JsonElement serialize(ChunkCoordIntPair src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src.chunkXPos + "," + src.chunkZPos);
		}
	}
	
	public static class AverageTypeAdapter extends TypeAdapter<Average> {
		
		private final BTWModTickMonitor monitor;
		
		public AverageTypeAdapter(BTWModTickMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public void write(JsonWriter out, Average average) throws IOException {
			out.beginObject();
			
			out.name("average");
			out.value(Util.DECIMAL_FORMAT_3.format(average.getAverage()));
			
			out.name("latest");
			out.value(average.getLatest());
			
			if (monitor.includeHistory()) {
				out.name("resolution");
				out.value(average.getResolution());
				
				out.name("history");
				out.beginArray();
				if (average.getTotal() > 0 && average.getTick() >= 0) {
					long[] history = average.getHistory();
					int backIndex = average.getTick() - average.getResolution();
					for (int i = average.getTick(); i >= 0 && i > backIndex; i--) {
						out.value(history[i % average.getResolution()]);
					}
				}
				out.endArray();
			}
			
			out.endObject();
		}

		@Override
		public Average read(JsonReader in) throws IOException {
			return null;
		}
	}
}
