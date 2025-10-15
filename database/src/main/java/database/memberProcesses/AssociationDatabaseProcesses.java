package database.memberProcesses;

import members.association.Association;

public class AssociationDatabaseProcesses extends MemberDatabaseProcesses {

	public static Integer getCount() {
		String statement = "SELECT count(associationID) AS COUNT FROM association";

		return fetchSingleData(statement, null, wrapper(t -> t.getInt(1)));
	}

	public static boolean insertConditionally(Association association) {
		if (!exists(association.getAssociationName()))
			return insert(association);
		else
			return false;
	}

	public static boolean insert(Association association) {
		String statement = "INSERT INTO association VALUES(DEFAULT, ?, ?)";

		int rowNumber = executeUpdate(statement, ps -> {
			ps.setString(1, association.getAssociationName());
			ps.setString(2, association.getAssociationIconURL());
		});

		return rowNumber > 0;
	}

	public static boolean exists(String associationName) {
		String statement = "SELECT * FROM association WHERE association_name = ?";

		return exists(statement, ps -> ps.setString(1, associationName), rs -> rs.next());
	}

	public static Integer getID(String associationName) {
		String statement = "SELECT associationID FROM association WHERE association_name = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, associationName), wrapper(rs -> rs.getInt(1)));
	}

	public static void main(String[] args) {
		AssociationDatabaseProcesses.insertConditionally(new Association("Test", " "));
	}
}
