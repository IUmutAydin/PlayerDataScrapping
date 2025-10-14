package members.stat;

import database.memberProcesses.StatDatabaseProcesses;

public class XGRecord {

	private int playerID;

	private int matchID;

	private int outcomeID;

	private int patternTypeID;

	private int hitTypeID;

	private double xgValue;

	private double startX;

	private double startY;

	private int second;

	private int half;

	public XGRecord(int playerID, int matchID, String outcomeType, String patternType, String hitType, double xgValue,
			double startX, double startY, int second, int half) {
		super();
		this.playerID = playerID;
		this.matchID = matchID;
		setOutcomeID(outcomeType);
		setPatternTypeID(patternType);
		setHitTypeID(hitType);
		this.xgValue = xgValue;
		this.startX = startX;
		this.startY = startY;
		this.second = second;
		this.half = half;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public int getOutcomeID() {
		return outcomeID;
	}

	public void setOutcomeID(String outcomeType) {
		outcomeID = StatDatabaseProcesses.getOutcomeTypeID(outcomeType);
	}

	public int getPatternTypeID() {
		return patternTypeID;
	}

	public void setPatternTypeID(String patternType) {
		patternTypeID = StatDatabaseProcesses.getPatternTypeID(patternType);
	}

	public int getHitTypeID() {
		return hitTypeID;
	}

	public void setHitTypeID(String hitType) {
		hitTypeID = StatDatabaseProcesses.getHitTypeID(hitType);
	}

	public double getXgValue() {
		return xgValue;
	}

	public void setXgValue(double xgValue) {
		this.xgValue = xgValue;
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
