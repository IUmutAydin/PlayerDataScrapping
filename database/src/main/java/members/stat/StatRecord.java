package members.stat;

import database.memberProcesses.PlayerDatabaseProcesses;
import database.memberProcesses.StatDatabaseProcesses;

public class StatRecord {

	private String statRecordID;

	private int statRecordType;

	private int playerID;

	private int matchID;

	private double startX;

	private double startY;

	private double endX;

	private double endY;

	private int second;

	private int half;

	public StatRecord(String statRecordID, String statRecordType, int playerID, int matchID, double startX,
			double startY, double endX, double endY, int second, int half) {
		this.statRecordID = statRecordID;
		setStatRecordType(statRecordType);
		this.playerID = playerID;
		this.matchID = matchID;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.second = second;
		this.half = half;
	}

	public StatRecord(String statRecordType, int playerID, int matchID, double startX, double startY, double endX,
			double endY, int second, int half) {
		setStatRecordType(statRecordType);
		this.playerID = playerID;
		this.matchID = matchID;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.second = second;
		this.half = half;
	}

	public int getStatRecordType() {
		return statRecordType;
	}

	public void setStatRecordType(String statRecordType) {
		if (statRecordType != null) {
			Integer statRecordTypeID = StatDatabaseProcesses.getStatRecordTypeID(statRecordType);

			if (statRecordTypeID == null)
				System.out.println(statRecordType);

			this.statRecordType = statRecordTypeID;
		}
	}

	public String getStatRecordID() {
		return statRecordID;
	}

	public void setStatRecordID(String statRecordID) {
		this.statRecordID = statRecordID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerName, int teamID) {
		if (playerName != null)
			playerID = PlayerDatabaseProcesses.getID(playerName, teamID);
	}

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	public double getEndX() {
		return endX;
	}

	public void setEndX(double endX) {
		this.endX = endX;
	}

	public double getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getHalf() {
		return half;
	}

	public void setHalf(int half) {
		this.half = half;
	}
}
