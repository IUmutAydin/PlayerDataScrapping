package optaAnalystPages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import database.memberProcesses.AssociationDatabaseProcesses;
import database.memberProcesses.TeamDatabaseProcesses;
import members.team.Team;
import optaAnalystPages.OptaAnalystNavigatorPage.League;
import utilities.DriverSwitchUtilities;
import utilities.JavaScriptUtilities;
import utilities.WaitUtilities;

public class TeamPage extends BasePage {

	private League league;
	private List<String> possibleFullTeamNames;
	private Queue<String> nextPlayers;

	private By teamName = By.xpath("//*[contains(@id, 'post-')]/header/div/div[2]/h1/a");
	private By abbreviatedTeamName = By.xpath(
			"//*[contains(@id, 'post-')]/descendant::td[contains(@style, 'background-color: rgb(99, 39, 198)')]/a/span");
	private By fullTeamName = By
			.xpath("//*[contains(@id, 'Opta_0')]/div/div[1]/table/tbody/tr[1]/td[starts-with(@class, 'Opta-Team')]");
	private By teamIconURL = By.xpath("//*[contains(@id, 'post-')]/header/div/div[1]/a/img");
	private By match1 = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[3]/div/div/div[2]/div/div[1]/a[1]");
	private By match2 = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[3]/div/div/div[2]/div/div[1]/a[2]");
	private By iFrame = By.xpath("/html/body/article/div/div[2]/iframe");
	// private By statsOption = By.xpath("//*[contains(@id,
	// 'post-')]/header/nav/ul/li[2]/a");
	private By squadOption = By.xpath("//*[contains(@id, 'post-')]/header/nav/ul/li[3]/a");
	// private By playerList = By.xpath("//*[contains(@id,
	// 'post-')]/div/div/div/div/descendant::table/tbody/tr/td[1]/descendant::a");
	private By playerList1 = By.xpath("//*[contains(@id, 'post-')]/div/div/div/div/div/div[2]/a");

	public TeamPage() {
		super();
		possibleFullTeamNames = new ArrayList<>();
		nextPlayers = new LinkedList<>();
	}

	public void createTeam() {
		int associationID = AssociationDatabaseProcesses.getID(league.getAssociationName());
		TeamDatabaseProcesses.insertConditionally(
				new Team(getTeamName(), associationID, getTeamIconURL(), getAbbreviatedTeamName(), getFullTeamName()));
	}

	public String getTeamName() {
		WaitUtilities.explicitlyWaitUntilVisible(10, teamName);
		JavaScriptUtilities.scrollToElement(teamName);
		return find(teamName).getText();
	}

	public String getAbbreviatedTeamName() {
		WaitUtilities.explicitlyWaitUntilVisible(10, abbreviatedTeamName);
		JavaScriptUtilities.scrollToElement(abbreviatedTeamName);
		return find(abbreviatedTeamName).getText();
	}

	public String getFullTeamName() {
		String fullTeamName = getFullTeamName(match1, false);

		if (fullTeamName != null)
			return fullTeamName;
		else
			return getFullTeamName(match2, true);
	}

	public String getFullTeamName(By match, boolean searchForDuplicate) {
		String teamName = getTeamName();

		WaitUtilities.explicitlyWaitUntilVisible(10, match);
		JavaScriptUtilities.scrollToElement(match);
		click(find(match));

		String currWindow = DriverSwitchUtilities.switchNextWindow();
		WaitUtilities.explicitlyWaitUntilFrame(20, iFrame);
		WaitUtilities.explicitlyWaitUntilPageComplition(20);

		WaitUtilities.explicitlyWaitUntilPresent(50, fullTeamName);
		WaitUtilities.explicitlyWaitUntilVisible(50, fullTeamName);
		JavaScriptUtilities.scrollToElement(fullTeamName);

		for (WebElement option : findElements(fullTeamName)) {
			String fullTeamName = option.getText();
			possibleFullTeamNames.add(fullTeamName);

			if (fullTeamName.contains(teamName)) {
				DriverSwitchUtilities.switchDefaultContent();
				DriverSwitchUtilities.switchPreviousWindow(currWindow);
				return fullTeamName;
			}
		}

		DriverSwitchUtilities.switchDefaultContent();
		DriverSwitchUtilities.switchPreviousWindow(currWindow);

		if (searchForDuplicate) {
			HashSet<String> fullTeamNames = new HashSet<>();
			return possibleFullTeamNames.stream().filter(ftn -> !fullTeamNames.add(ftn)).toList().get(0);
		} else
			return null;
	}

	public String getTeamIconURL() {
		WaitUtilities.explicitlyWaitUntilVisible(10, teamIconURL);
		JavaScriptUtilities.scrollToElement(teamIconURL);
		return find(teamIconURL).getDomAttribute("src");
	}

	public PlayerPage getPlayerPage(String teamName) {
		String url = nextPlayers.poll();
		navigateToURL(url);

		System.out.println(url);

		PlayerPage playerPage = new PlayerPage(teamName);

		return playerPage;
	}

	public Queue<String> getNextPlayers() {
		return nextPlayers;
	}

	public void getPlayers() {
		WaitUtilities.explicitlyWaitUntilVisible(10, squadOption);
		JavaScriptUtilities.scrollToElement(squadOption);

		String url = find(squadOption).getDomAttribute("href");
		driver.get(url);
		WaitUtilities.explicitlyWaitUntilPageComplition(10);

		WaitUtilities.explicitlyWaitUntilVisible(10, playerList1);
		JavaScriptUtilities.scrollToElement(playerList1);
		nextPlayers = findElements(playerList1).stream().map(element -> this.url + element.getDomAttribute("href"))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}
}
