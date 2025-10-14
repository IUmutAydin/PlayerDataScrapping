package database.memberProcesses;

import members.team.Team;

public class TeamDatabaseProcesses extends MemberDatabaseProcesses {

	private static Integer setCount() {
		String statement = "SELECT count(teamID) FROM team";

		return fetchSingleData(statement, null, wrapper(rs -> rs.getInt(1)));
	}

	public static boolean insertConditionally(Team team) {
		if (!exists(team.getFullTeamName()))
			return insert(team);
		else {
			setFullNameConditionally(getID(team.getTeamName(), false), team.getFullTeamName());
			return false;
		}
	}

	public static boolean insert(Team team) {
		String statement = "INSERT INTO team VALUES(DEFAULT, ?, ?, ?, ?, ?)";

		int rowNumber = executeUpdate(statement, ps -> {
			ps.setString(1, team.getTeamName());
			ps.setInt(2, team.getAssociationID());
			ps.setString(3, team.getTeamIconURL());
			ps.setString(4, team.getAbbreviatedTeamName());
			ps.setString(5, team.getFullTeamName());
		});

		setCount();

		return rowNumber > 0;
	}

	public static boolean exists(String fullTeamName) {
		String statement = "SELECT * FROM team WHERE full_team_name = ?";

		return exists(statement, ps -> ps.setString(1, fullTeamName), rs -> rs.next());
	}

	public static Integer getID(String teamName, boolean isFullName) {
		String statement;

		if (isFullName)
			statement = "SELECT teamID FROM team WHERE full_team_name = ?";
		else
			statement = "SELECT teamID FROM team WHERE team_name = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, teamName), wrapper(rs -> rs.getInt(1)));
	}

	public static boolean setFullNameConditionally(int teamID, String fullTeamName) {
		if (isFullNameEmpty(teamID))
			return updateFullName(teamID, fullTeamName);
		else
			return false;
	}

	public static boolean updateFullName(int teamID, String fullTeamName) {
		String statement = "UPDATE team SET full_team_name = ? WHERE teamID = ?";

		int rowNumber = executeUpdate(statement, ps -> {
			ps.setString(1, fullTeamName);
			ps.setInt(2, teamID);
		});

		return rowNumber > 0;
	}

	public static boolean isFullNameEmpty(int teamID) {
		String statement = "SELECT team_name FROM team WHERE teamID = ?";

		return fetchSingleData(statement, ps -> ps.setInt(1, teamID), wrapper(rs -> rs.getString(1))) == null;
	}
}
