package members.stat;

import database.memberProcesses.PlayerDatabaseProcesses;
import database.memberProcesses.TeamDatabaseProcesses;

public class PlayerMatch {

	private int playerID;

	private int matchID;

	private int playedFor;

	private int playedAgainst;

	private int startSecond;

	private int startHalf;

	private int substituteOffSecond;

	private int substituteOffHalf;

	public PlayerMatch(int playerID, int matchID, int playedFor, int playedAgainst, int startSecond, int startHalf,
			int substituteOffSecond, int substituteOffHalf) {
		this.playerID = playerID;
		this.matchID = matchID;
		this.playedFor = playedFor;
		this.playedAgainst = playedAgainst;
		this.startSecond = startSecond;
		this.startHalf = startHalf;
		this.substituteOffSecond = substituteOffSecond;
		this.substituteOffHalf = substituteOffHalf;
	}

	public PlayerMatch(int playerID, int matchID, String playedFor, String playedAgainst, int startSecond,
			int startHalf, int substituteOffSecond, int substituteOffHalf) {
		this.playerID = playerID;
		this.matchID = matchID;
		setPlayedFor(playedFor);
		setPlayedAgainst(playedAgainst);
		this.startSecond = startSecond;
		this.startHalf = startHalf;
		this.substituteOffSecond = substituteOffSecond;
		this.substituteOffHalf = substituteOffHalf;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerName, int teamID) {
		playerID = PlayerDatabaseProcesses.getID(playerName, teamID);
	}

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public int getPlayedFor() {
		return playedFor;
	}

	public void setPlayedFor(String teamName) {
		playedFor = TeamDatabaseProcesses.getID(teamName, true);
	}

	public int getPlayedAgainst() {
		return playedAgainst;
	}

	public void setPlayedAgainst(String teamName) {
		playedAgainst = TeamDatabaseProcesses.getID(teamName, true);
	}

	public int getStartSecond() {
		return startSecond;
	}

	public void setStartSecond(int startSecond) {
		this.startSecond = startSecond;
	}

	public int getStartHalf() {
		return startHalf;
	}

	public void setStartHalf(int startHalf) {
		this.startHalf = startHalf;
	}

	public int getSubstituteOffSecond() {
		return substituteOffSecond;
	}

	public void setSubstituteOffSecond(int substituteOffSecond) {
		this.substituteOffSecond = substituteOffSecond;
	}

	public int getSubstituteOffHalf() {
		return substituteOffHalf;
	}

	public void setSubstituteOffHalf(int substituteOffHalf) {
		this.substituteOffHalf = substituteOffHalf;
	}
}
