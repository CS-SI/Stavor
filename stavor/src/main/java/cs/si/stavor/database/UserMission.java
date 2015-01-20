package cs.si.stavor.database;

public class UserMission {
	public String name;
	public String description;
	public byte[] serialclass;
	
	public UserMission(String name, String description, byte[] serialclass){
		this.name = name;
		this.description = description;
		this.serialclass = serialclass;
	}
}
