package members.team;

public class Team {

	private int teamID;

	private String teamName;

	private int associationID;

	private String teamIconURL;

	private String abbreviatedTeamName;

	private String fullTeamName;

	public Team(String teamName, int associationID, String teamIconURL, String abbreviatedTeamName,
			String fullTeamName) {
		this.teamName = teamName;
		this.associationID = associationID;
		this.teamIconURL = teamIconURL;
		this.abbreviatedTeamName = abbreviatedTeamName;
		this.fullTeamName = fullTeamName;
	}

	public Team(int teamID, String teamName, int associationID, String teamIconURL, String abbreviatedTeamName,
			String fullTeamName) {
		this(teamName, associationID, teamIconURL, abbreviatedTeamName, fullTeamName);
		this.teamID = teamID;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getAssociationID() {
		return associationID;
	}

	public void setAssociationID(int associationID) {
		this.associationID = associationID;
	}

	public String getTeamIconURL() {
		return teamIconURL;
	}

	public void setTeamIconURL(String teamIconURL) {
		this.teamIconURL = teamIconURL;
	}

	public String getAbbreviatedTeamName() {
		return abbreviatedTeamName;
	}

	public void setAbbreviatedTeamName(String abbreviatedTeamName) {
		this.abbreviatedTeamName = abbreviatedTeamName;
	}

	public String getFullTeamName() {
		return fullTeamName;
	}

	public void setFullTeamName(String fullTeamName) {
		this.fullTeamName = fullTeamName;
	}
}
