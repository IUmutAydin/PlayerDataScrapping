package members.competition;

public class Competition {

	private int competitionID;

	private int competitionTypeID;

	private String competitionName;

	private int associationID;

	private String competitionIconURL;

	public Competition(int competitionTypeID, String competitionName, int associationID, String competitionIconURL) {
		this.competitionTypeID = competitionTypeID;
		this.competitionName = competitionName;
		this.associationID = associationID;
		this.competitionIconURL = competitionIconURL;
	}

	public Competition(int competitionID, int competitionTypeID, String competitionName, int associationID,
			String competitionIconURL) {
		this(competitionTypeID, competitionName, associationID, competitionIconURL);
		this.competitionID = competitionID;
	}

	public int getCompetitionID() {
		return competitionID;
	}

	public void setCompetitionID(int competitionID) {
		this.competitionID = competitionID;
	}

	public int getCompetitionTypeID() {
		return competitionTypeID;
	}

	public void setCompetitionTypeID(int competitionTypeID) {
		this.competitionTypeID = competitionTypeID;
	}

	public String getCompetitionName() {
		return competitionName;
	}

	public void setCompetitionName(String competitionName) {
		this.competitionName = competitionName;
	}

	public int getAssociationID() {
		return associationID;
	}

	public void setAssociationID(int associationID) {
		this.associationID = associationID;
	}

	public String getCompetitionIconURL() {
		return competitionIconURL;
	}

	public void setCompetitionIconURL(String competitionIconURL) {
		this.competitionIconURL = competitionIconURL;
	}
}
