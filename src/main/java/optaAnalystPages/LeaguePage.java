package optaAnalystPages;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.By;

import database.memberProcesses.AssociationDatabaseProcesses;
import database.memberProcesses.CompetitionDatabaseProcesses;
import database.memberProcesses.MatchDatabaseProcesses;
import members.association.Association;
import members.competition.Competition;
import optaAnalystPages.OptaAnalystNavigatorPage.League;
import utilities.WaitUtilities;

public class LeaguePage extends BasePage {
	private League league;

	private Queue<String> nextTeams;

	private By leagueName = By.xpath("//*[contains(@id, 'post-')]/header/div/div[2]/h1/a");
	private By leagueIconURL = By.xpath("//*[contains(@id, 'post-')]/header/div/div[1]/a/img");
	private By teamsButton = By.id("teams");
	private By orderByNameButton = By
			.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[4]/div/div[4]/div[1]/table/thead/tr/th[1]");
	private By teamList = By.xpath("//*[contains(@id, 'post-')]/descendant::table/tbody/tr/descendant::a");
	private By fixture = By.xpath("//*[contains(@id, 'post-')]/header/nav/ul/li[6]/a");
	private By calendarButton = By.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[1]/div/div/div[1]/button[2]");
	private By backMonthButton = By
			.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[1]/div/div/div[2]/div/div/button[1]");
	private By firstMatchDay = By.xpath(
			"//*[contains(@id, 'post-')]/div/div/div/div/div[1]/div/div/div[2]/div/table/tbody/tr/td[contains(@class, 'matchday')]");
	private By nextMatchDay = By.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[1]/div/div/div[1]/button[3]");
	private By matches = By.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[2]/div/div/a");
	private By matchDate = By.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div[1]/div/div/div[1]/button[2]/div");

	public LeaguePage() {
		super();
		nextTeams = new LinkedList<>();
	}

	public void createMatchURLs() {
		String leagueName = getLeagueName();
		getFixture();
		getFirstMatchDay();

		String date = " ";

		do {
			WaitUtilities.explicitlyWaitUntilPresent(20, matches);
			waitUntilVisibleThenScroll(10, matches);

			findElements(matches).forEach(element -> {
				MatchDatabaseProcesses.insertConditionally(element.getDomAttribute("href"),
						CompetitionDatabaseProcesses.getID(leagueName));
			});

			date = find(matchDate).getText();

			waitUntilClickableThenScroll(10, nextMatchDay);
			click(nextMatchDay);

			waitUntilVisibleThenScroll(10, matchDate);
		} while (!date.equals(find(matchDate).getText()));
	}

	private void getFirstMatchDay() {
		waitUntilClickableThenScroll(10, calendarButton, 2);

		click(calendarButton);

		IntStream.range(0, 8).forEach(i -> {
			waitUntilClickableThenScroll(10, backMonthButton);
			click(backMonthButton);
		});

		waitUntilVisibleThenScroll(10, firstMatchDay);

		click(findElements(firstMatchDay).get(0));
	}

	private void getFixture() {
		waitUntilVisibleThenScroll(10, fixture, 2);

		navigateToURL(find(fixture).getDomAttribute("href"));

		WaitUtilities.explicitlyWaitUntilPageComplition(10);
	}

	public String getLeagueName() {
		waitUntilVisibleThenScroll(10, leagueName);
		return find(leagueName).getText();
	}

	private String getLeagueIconURL() {
		waitUntilVisibleThenScroll(10, leagueIconURL);
		return find(leagueIconURL).getDomAttribute("src");
	}

	public void createLeague() {
		AssociationDatabaseProcesses
				.insertConditionally(new Association(league.getAssociationName(), league.getAssociationIconURL()));

		int associationID = AssociationDatabaseProcesses.getID(league.getAssociationName());

		CompetitionDatabaseProcesses
				.insertConditionally(new Competition(1, getLeagueName(), associationID, getLeagueIconURL()));
	}

	public void sortTeams() {
		selectTeams();

		waitUntilClickableThenScroll(10, orderByNameButton);
		click(orderByNameButton);
	}

	public void selectTeams() {
		waitUntilClickableThenScroll(10, teamsButton);
		click(teamsButton);
	}

	public TeamPage getTeamPage() {
		navigateToURL(url + nextTeams.poll());

		TeamPage teamPage = new TeamPage();
		teamPage.setLeague(league);
		teamPage.setPreviousURL(driver.getCurrentUrl());

		return teamPage;
	}

	public TeamPage getTeamPage(String teamURL) {
		navigateToURL(url + teamURL);

		TeamPage teamPage = new TeamPage();

		return teamPage;
	}

	public void getTeams() {
		WaitUtilities.explicitlyWaitUntilVisible(30, teamList, 500);

		nextTeams = findElements(teamList).stream().map(element -> element.getDomAttribute("href"))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public Queue<String> getNextTeams() {
		return nextTeams;
	}
}
