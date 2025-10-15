package database.memberProcesses;

import java.sql.Date;

import members.player.Player;

public class PlayerDatabaseProcesses extends MemberDatabaseProcesses {

	private static Integer setCount() {
		String statement = "SELECT count(playerID) AS COUNT FROM player";

		return fetchSingleData(statement, null, wrapper(t -> t.getInt(1)));
	}

	public static boolean insertConditionally(Player player) {
		if (!exists(player.getPlayerName(), player.getDob()))
			return insert(player);
		else
			return false;
	}

	public static boolean insert(Player player) {
		String statement = "INSERT INTO player VALUES(DEFAULT, ?, ?, ?, ?, ?)";

		int rowNumber = executeUpdate(statement, ps -> {
			ps.setString(1, player.getPlayerName());
			ps.setDate(2, player.getDob());
			isNumericDataNull(ps, 3, player.getTeamID());
			isNumericDataNull(ps, 4, player.getAssociationID());
			isNumericDataNull(ps, 5, player.getPositionID());
		});

		setCount();

		return rowNumber > 0;
	}

	public static boolean exists(String playerName, Date dob) {
		String statement = "SELECT * FROM player WHERE player_name = ? AND dob = ?";

		return exists(statement, ps -> {
			ps.setString(1, playerName);
			ps.setDate(2, dob);
		}, rs -> rs.next());
	}

	public static Integer getID(String playerName, int teamID) {
		String likePlayerName = "%" + playerName.replaceAll("\\.", "%") + "%";
		System.out.println(likePlayerName);
		String statement = "SELECT playerID FROM player WHERE player_name LIKE ?";

		if (!exists(statement, ps -> ps.setString(1, likePlayerName), rs -> rs.next() && rs.next()))
			return fetchSingleData(statement, ps -> ps.setString(1, likePlayerName), wrapper(rs -> rs.getInt(1)));
		else {
			statement = "SELECT playerID FROM player WHERE player_name LIKE ? AND teamID = ?";

			PreparedStatementSetter psSetter = ps -> {
				ps.setString(1, likePlayerName);
				ps.setInt(2, teamID);
			};

			if (exists(statement, psSetter, rs -> rs.next()))
				return fetchSingleData(statement, psSetter, wrapper(rs -> rs.getInt(1)));
			else {
				System.out.println(likePlayerName);
				return null;
			}
		}
	}

	public static Integer getPositionID(String positionName) {
		String statement = "SELECT positionID FROM position WHERE position_name = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, positionName), wrapper(rs -> rs.getInt(1)));
	}
}
