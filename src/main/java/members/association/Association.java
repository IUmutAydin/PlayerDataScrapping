package members.association;

public class Association {

	private int associationID;

	private String associationName;

	private String associationIconURL;

	public Association(String associationName, String associationIconURL) {
		this.associationName = associationName;
		this.associationIconURL = associationIconURL;
	}

	public Association(int associationID, String associationName, String associationIconURL) {
		this(associationName, associationIconURL);
		this.associationID = associationID;
	}

	public int getAssociationID() {
		return associationID;
	}

	public void setAssociationID(int associationID) {
		this.associationID = associationID;
	}

	public String getAssociationName() {
		return associationName;
	}

	public void setAssociationName(String associationName) {
		this.associationName = associationName;
	}

	public String getAssociationIconURL() {
		return associationIconURL;
	}

	public void setAssociationIconURL(String associationIconURL) {
		this.associationIconURL = associationIconURL;
	}
}
