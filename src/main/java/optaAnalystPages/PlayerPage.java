package optaAnalystPages;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import database.memberProcesses.AssociationDatabaseProcesses;
import database.memberProcesses.PlayerDatabaseProcesses;
import database.memberProcesses.TeamDatabaseProcesses;
import members.association.Association;
import members.player.Player;
import utilities.WaitUtilities;

public class PlayerPage extends BasePage {

	private String teamName;

	private By playerName = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[1]/div[2]");
	private By playerName1 = By.xpath("//*[contains(@id, 'post-')]/header/div/div[2]/h1/a");
	private By dob = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[2]/div[2]");
	private By teamName1 = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[4]/div[2]/a/span");
	private By teamURL = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[4]/div[2]/a");
	private By teamNameWithoutURL = By
			.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[4]/div[2]/span");
	private By associationName = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[5]/div[2]/span");
	private By associationIconURL = By
			.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[5]/div[2]/span/img");
	private By position = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[1]/div/div/div/div[6]/div[2]");
	private By noDataText = By.xpath("//*[contains(@id, 'post-')]/div[1]/div[2]/div/p");
	private By noDataText1 = By.xpath("//*[@id=\"content\"]/h1");

	public PlayerPage(String teamName) {
		super();
		this.teamName = teamName;
	}

	public void createPlayer() {
		if (isPageAvailable()) {
			String playerName = getPlayerName();

			try {
				Date date = getDate();

				if (!exists(playerName, date)) {
					String associationName = getAssociationName();
					AssociationDatabaseProcesses
							.insertConditionally(new Association(associationName, getAssociationIconURL()));
					Integer associationID = AssociationDatabaseProcesses.getID(associationName);
					Integer teamID = TeamDatabaseProcesses.getID(getTeamName(), false);
					Integer positionID = PlayerDatabaseProcesses.getPositionID(getPosition());

					PlayerDatabaseProcesses
							.insertConditionally(new Player(playerName, date, teamID != null ? teamID.intValue() : 0,
									associationID != null ? associationID.intValue() : 0,
									positionID != null ? positionID.intValue() : 0));
				}
			} catch (NoSuchElementException e) {
				Integer teamID = TeamDatabaseProcesses.getID(teamName, false);
				PlayerDatabaseProcesses
						.insert(new Player(playerName, null, teamID != null ? teamID.intValue() : 0, 0, 0));
			}
		}
	}

	public boolean exists(String playerName, Date dob) {
		return PlayerDatabaseProcesses.exists(playerName, dob);
	}

	public String getPlayerName() {
		try {
			waitUntilVisibleThenScroll(10, playerName, 2);
			return find(playerName).getText();
		} catch (NoSuchElementException e) {
			e.printStackTrace();

		}
		return find(playerName1).getText();
	}

	public Date getDate() {
		waitUntilVisibleThenScroll(10, dob, 2);

		String date = find(dob).getText();

		return dateConverter(date);
	}

	private Date dateConverter(String date) {
		if (!date.equals("Invalid Date")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("tr-TR"));
			LocalDate localDate = LocalDate.parse(date, formatter);

			return Date.valueOf(localDate);
		} else
			return null;
	}

	public String getTeamName() {
		String currentURL = driver.getCurrentUrl();
		try {
			waitUntilVisibleThenScroll(10, teamName1);

			if (!find(teamName1).getText().equals(teamName)) {
				navigateToURL(url + find(teamURL).getDomAttribute("href"));
				WaitUtilities.explicitlyWaitUntilPageComplition(10);

				TeamPage teamPage = new TeamPage();
				teamName = teamPage.getTeamName();
				navigateToURL(currentURL);
				WaitUtilities.explicitlyWaitUntilPageComplition(10);
			}

			return teamName;
		} catch (NoSuchElementException e) {
			navigateToURL(currentURL);
			WaitUtilities.explicitlyWaitUntilPageComplition(10);
			e.printStackTrace();
		}

		waitUntilVisibleThenScroll(10, teamNameWithoutURL);

		return find(teamNameWithoutURL).getText();
	}

	public String getAssociationName() {
		waitUntilVisibleThenScroll(10, associationName, 2);

		return find(associationName).getText();
	}

	public String getAssociationIconURL() {
		waitUntilVisibleThenScroll(10, associationIconURL);

		return find(associationIconURL).getDomAttribute("src");
	}

	public String getPosition() {
		waitUntilVisibleThenScroll(10, position, 2);

		return find(position).getText();
	}

	public boolean isPageAvailable() {
		return findElements(noDataText).isEmpty() && findElements(noDataText1).isEmpty();
	}
}
