package tasks;

import optaAnalystPages.DriverManager;
import optaAnalystPages.MatchPage;

public class XGRecordTask extends LeagueTask {

	private String matchURL;

	private int competitionID;

	private MatchPage mp;

	public XGRecordTask(String matchURL, int competitionID) {
		super();
		this.matchURL = matchURL;
		this.competitionID = competitionID;
	}

	@Override
	protected void setDriver() {
		driver = DriverManager.getDriver();
		driver.manage().window().maximize();
	}

	@Override
	protected void setPages() {
		super.setPages();
		mp = new MatchPage(matchURL, competitionID);
	}

	@Override
	public Void call() throws Exception {
		setDriver();
		setPages();

		mp.createMatch(mp1 -> {
			mp1.createXGRecords();
			return null;
		});

		return null;
	}

}
