package members.match;

import java.sql.Date;

public class Match {

	private int matchID;

	private int homeTeamID;

	private int awayTeamID;

	private Date matchDate;

	private int competitionID;

	private String venue;

	private int attendance;

	private String referee;

	private int homeScore;

	private int awayScore;

	public Match(int homeTeamID, int awayTeamID, Date matchDate, int competitionID, String venue, int attendance,
			String referee, int homeScore, int awayScore) {
		this.homeTeamID = homeTeamID;
		this.awayTeamID = awayTeamID;
		this.matchDate = matchDate;
		this.competitionID = competitionID;
		this.venue = venue;
		this.attendance = attendance;
		this.referee = referee;
		this.homeScore = homeScore;
		this.awayScore = awayScore;
	}

	public int getMatchID() {
		return matchID;
	}

	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}

	public int getHomeTeamID() {
		return homeTeamID;
	}

	public void setHomeTeamID(int homeTeamID) {
		this.homeTeamID = homeTeamID;
	}

	public int getAwayTeamID() {
		return awayTeamID;
	}

	public void setAwayTeamID(int awayTeamID) {
		this.awayTeamID = awayTeamID;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public int getCompetitionID() {
		return competitionID;
	}

	public void setCompetitionID(int competitionID) {
		this.competitionID = competitionID;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public int getAttendance() {
		return attendance;
	}

	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}
}
