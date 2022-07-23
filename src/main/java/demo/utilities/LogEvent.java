package demo.utilities;

public abstract class LogEvent {
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "LogEvent [id=" + id + "]";
	}

}
