package btwmods.io;

public class IllegalSettingException extends IllegalArgumentException {
	public final String section;
	public final String key;
	public final String value;
	
	public IllegalSettingException(String section, String key) {
		super();
		this.section = section;
		this.key = key;
		this.value = null;
	}
	
	public IllegalSettingException(String section, String key, String value) {
		super();
		this.section = section;
		this.key = key;
		this.value = value;
	}
}
