package members.player;

import java.sql.Date;

public class Player {

	private int playerID;

	private String playerName;

	private Date dob;

	private int teamID;

	private int associationID;

	private int positionID;

	public Player(String playerName, Date dob, int teamID, int associationID, int positionID) {
		this.playerName = playerName;
		this.dob = dob;
		this.teamID = teamID;
		this.associationID = associationID;
		this.positionID = positionID;
	}

	public Player(int playerID, String playerName, Date dob, int teamID, int associationID, int positionID) {
		this(playerName, dob, teamID, associationID, positionID);
		this.playerID = playerID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public int getAssociationID() {
		return associationID;
	}

	public void setAssociationID(int associationID) {
		this.associationID = associationID;
	}

	public int getPositionID() {
		return positionID;
	}

	public void setPositionID(int positionID) {
		this.positionID = positionID;
	}
}
