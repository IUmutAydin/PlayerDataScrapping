package tasks;

import java.util.concurrent.Callable;

import org.openqa.selenium.WebDriver;

import optaAnalystPages.DriverManager;
import optaAnalystPages.LeaguePage;
import optaAnalystPages.OptaAnalystNavigatorPage;
import optaAnalystPages.OptaAnalystNavigatorPage.League;
import optaAnalystPages.TeamPage;
import utilities.WaitUtilities;

public class LeagueTask implements Callable<Void> {

	protected WebDriver driver;
	protected String leagueURL;
	protected League league;
	protected OptaAnalystNavigatorPage oanp;
	protected LeaguePage leaguePage;

	public LeagueTask() {

	}

	public LeagueTask(String leagueURL, League league) {
		this.leagueURL = leagueURL;
		this.league = league;
	}

	protected void setDriver() {
		driver = DriverManager.getDriver();
		driver.get(leagueURL);
		driver.manage().window().maximize();
	}

	protected void setPages() {
		oanp = new OptaAnalystNavigatorPage();
		oanp.acceptCookies();
		leaguePage = new LeaguePage();
		leaguePage.setLeague(league);
	}

	@Override
	public Void call() throws Exception {
		try {
			setDriver();
			setPages();

			leaguePage.selectTeams();
			leaguePage.getTeams();

			TeamPage teamPage;

			while (!leaguePage.getNextTeams().isEmpty()) {
				System.out.println(
						Thread.currentThread().getName() + " " + leagueURL + " " + leaguePage.getNextTeams().size()
								+ " " + driver.getCurrentUrl() + leaguePage.getNextTeams().size());

				teamPage = leaguePage.getTeamPage();
				teamPage.setLeague(league);
				WaitUtilities.explicitlyWaitUntilPageComplition(10);
				teamPage.createTeam();
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + leagueURL + driver.getCurrentUrl());
			DriverManager.quitDriver();
			e.printStackTrace();
		}

		DriverManager.quitDriver();

		return null;
	}
}
