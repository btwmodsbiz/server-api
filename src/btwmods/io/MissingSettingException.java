package btwmods.io;

public class MissingSettingException extends NullPointerException {
	public final String section;
	public final String key;
	
	public MissingSettingException(String section, String key) {
		super();
		this.section = section;
		this.key = key;
	}
	
	public MissingSettingException(String section, String key, String message) {
		super(message);
		this.section = section;
		this.key = key;
	}
}
