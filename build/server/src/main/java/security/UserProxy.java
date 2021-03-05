package security;

public class UserProxy {
	public String type;
	public long userId;
	public String sessionId;

	public UserProxy(String type, Long id, String sessionId) {
		this.type = type;
		this.userId = id;
		this.sessionId = sessionId;
	}
}
