package btwmods.util;

public class CaselessKey {
	public final String key;
	
	public CaselessKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		return key.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (obj.getClass() == String.class)
			return key.equalsIgnoreCase((String)obj);
		
		if (getClass() != obj.getClass())
			return false;
		
		CaselessKey other = (CaselessKey)obj;
		if (!key.equalsIgnoreCase(other.key))
			return false;
		
		return true;
	}
}
