package database.memberProcesses;

import members.competition.Competition;

public class CompetitionDatabaseProcesses extends MemberDatabaseProcesses {

	public static Integer getCount() {
		String statement = "SELECT count(competitionID) FROM competition";

		return fetchSingleData(statement, null, wrapper(rs -> rs.getInt(1)));
	}

	public static boolean insertConditionally(Competition competition) {
		if (!exist(competition.getCompetitionName()))
			return insert(competition);
		else
			return false;
	}

	public static boolean insert(Competition competition) {
		String statement = "INSERT INTO competition VALUES(DEFAULT, ?, ?, ?, ?)";

		int rowNumber = executeUpdate(statement, ps -> {
			ps.setInt(1, competition.getCompetitionTypeID());
			ps.setString(2, competition.getCompetitionName());
			ps.setInt(3, competition.getAssociationID());
			ps.setString(4, competition.getCompetitionIconURL());
		});

		return rowNumber > 0;
	}

	public static boolean exist(String competitionName) {
		String statement = "SELECT * FROM competition WHERE competition_name = ?";

		return exists(statement, ps -> ps.setString(1, competitionName), rs -> rs.next());
	}

	public static int getID(String competitionName) {
		String statement = "SELECT competitionID FROM competition WHERE competition_name = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, competitionName), wrapper(rs -> rs.getInt(1)));
	}
}
