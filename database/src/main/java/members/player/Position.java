package members.player;

public class Position {

	private int positionID;

	private String positionName;

	public Position(String positionName) {
		this.positionName = positionName;
	}

	public Position(int positionID, String positionName) {
		this(positionName);
		this.positionID = positionID;
	}

	public int getPositionID() {
		return positionID;
	}

	public void setPositionID(int positionID) {
		this.positionID = positionID;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
}
