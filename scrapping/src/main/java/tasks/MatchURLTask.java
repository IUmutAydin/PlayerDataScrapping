package tasks;

import optaAnalystPages.DriverManager;
import optaAnalystPages.OptaAnalystNavigatorPage.League;

public class MatchURLTask extends LeagueTask {

	public MatchURLTask(String leagueURL, League league) {
		super(leagueURL, league);
	}

	@Override
	public Void call() throws Exception {
		try {
			setDriver();
			setPages();
			leaguePage.createMatchURLs();
		} catch (Exception e) {
			System.out.println(
					Thread.currentThread().getName() + " " + driver.getCurrentUrl() + " " + league.getLeagueName());
			e.printStackTrace();
		} finally {
			DriverManager.quitDriver();
		}

		return null;
	}

}
