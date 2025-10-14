package optaAnalystPages;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import utilities.JavaScriptUtilities;
import utilities.WaitUtilities;

public class OptaAnalystNavigatorPage extends BasePage {

	public enum League {
		PL("Premier League", "England",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=1fk5l4hkqk12i7zske6mcqju6"),
		LaLiga("La Liga", "Spain",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=49ih1pwv3ahshdf8uzrimi54c"),
		Bundesliga("Bundesliga", "Germany",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=36min0qztu8eydwvpv8t1is0m"),
		SerieA("Serie A", "Italy",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=25f2cmb2r8mk5rj92tzer6kvv"),
		Ligue1("Ligue 1", "France",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=7gww28djs405rfga69smki84o"),
		EC("English Championship", "England",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=1fk5l4hkqk12i7zske6mcqju6"),
		L1("League One", "England",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=1fk5l4hkqk12i7zske6mcqju6"),
		L2("League Two", "England",
				"https://omo.akamai.opta.net/image.php?secure=true&h=omo.akamai.opta.net&sport=football&entity=flags&description=countries&dimensions=21x21&id=1fk5l4hkqk12i7zske6mcqju6");

		private String leagueName;
		private String associationName;
		private String associationIconURL;

		private League(String leagueName, String associationName, String associationIconURL) {
			this.leagueName = leagueName;
			this.associationName = associationName;
			this.associationIconURL = associationIconURL;
		}

		public String getLeagueName() {
			return leagueName;
		}

		public String getAssociationName() {
			return associationName;
		}

		public String getAssociationIconURL() {
			return associationIconURL;
		}

		public void setAssociationIconURL(String associationIconURL) {
			this.associationIconURL = associationIconURL;
		}
	}

	private Queue<League> nextLeagues;
	private List<String> leagueURLs;

	private By popupAcceptButton = By.id("onetrust-accept-btn-handler");
	private By competitionsNavigator = By.xpath("/html/body/header/nav/div/ul/li[3]");
	private By competitionList = By.xpath("/html/body/header/nav/div/ul/li[3]/ul/li/a");

	public OptaAnalystNavigatorPage() {
		super();
		nextLeagues = new LinkedList<>();
		nextLeagues.addAll(Arrays.asList(League.values()));
	}

	public LeaguePage getLeaguePage() {
		WaitUtilities.explicitlyWaitUntilPageComplition(10);

		if (nextLeagues.size() == League.values().length)
			acceptCookies();

		WaitUtilities.explicitlyWaitUntilVisible(3, competitionsNavigator);

		JavaScriptUtilities.mouseOverElement(competitionsNavigator);

		try {
			WaitUtilities.explicitlyWaitUntilVisible(10, competitionList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		League league = nextLeagues.poll();

		for (WebElement option : findElements(competitionList)) {
			if (option.getText().equals(league.getLeagueName())) {
				LeaguePage leaguePage = new LeaguePage();
				leaguePage.setLeague(league);
				leaguePage.setPreviousURL(driver.getCurrentUrl());
				navigateToURL(url + option.getDomAttribute("href"));
				return leaguePage;
			}
		}

		return null;
	}

	public List<String> getLeagueURLs() {
		return leagueURLs;
	}

	public void setLeagueURLs() {
		WaitUtilities.explicitlyWaitUntilPageComplition(10);

		WaitUtilities.explicitlyWaitUntilVisible(3, competitionsNavigator);

		JavaScriptUtilities.mouseOverElement(competitionsNavigator);

		try {
			WaitUtilities.explicitlyWaitUntilVisible(10, competitionList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		leagueURLs = findElements(competitionList)
				.stream().filter(option -> Arrays.asList(League.values()).stream().map(league -> league.getLeagueName())
						.toList().contains(option.getText()))
				.map(option -> url + option.getDomAttribute("href")).toList();
	}

	public void acceptCookies() {
		WaitUtilities.explicitlyWaitUntilPageComplition(10);
		WaitUtilities.explicitlyWaitUntilPresent(10, popupAcceptButton);
		WaitUtilities.explicitlyWaitUntilVisible(10, popupAcceptButton);
		waitUntilClickableThenScroll(10, popupAcceptButton, 2);
		find(popupAcceptButton).click();
	}

	public Queue<League> getNextLeagues() {
		return nextLeagues;
	}
}
