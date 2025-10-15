package pages;

import org.openqa.selenium.By;

public class PlayerPage extends BasePage {

	private By playerName = By
			.xpath("//*[@id=\"__next\"]/main/div[2]/div/div[2]/div[1]/div[1]/div[2]/div/div[1]/div/div[1]/h2");

	public boolean isPlayerNameDisplayed() {
		return find(playerName).isDisplayed();
	}
}
