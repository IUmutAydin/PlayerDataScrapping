package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PlayerSearchPage extends BasePage {

	private By searchBox = By.id("search-input");

	private By playerButton = By
			.xpath("//*[@id=\"__next\"]/header/div/div[2]/div/div[2]/div/div/div[1]/div/div/button[3]");

	private By playerList = By.xpath(
			"//*[@id=\"__next\"]/header/div/div[2]/div/div[2]/div/div/div[2]/div/div/div/div/a/div/div[1]/div[2]");

	private By sportBranch = By.xpath("span[2]");

	private By clubName = By.xpath("span[1]");

	public void searchPlayer(String playerName) {
		click(searchBox);
		click(playerButton);
		set(searchBox, playerName);

		try {
			Thread.sleep(Duration.ofMillis(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PlayerPage moveToPlayerPage(String playerName, String sportBranch, String clubName) {
		searchPlayer(playerName);

		for (WebElement playerRow : findElements(playerList)) {
			String branch = playerRow.findElement(this.sportBranch).getText();
			String club = playerRow.findElement(this.clubName).getText();

			if (sportBranch.equals(branch) && clubName.equals(club)) {
				click(playerRow.findElement(By.xpath("ancestor::a")));
				break;
			}
		}

		return new PlayerPage();
	}

}
