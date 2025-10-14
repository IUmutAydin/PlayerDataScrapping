package tests.playerSearch;

import org.testng.annotations.Test;

import base.BaseTest;

public class PlayerSearchTest extends BaseTest {

	@Test
	public void searchPlayer() {
		playerSearchPage.setDriver(driver);
		playerSearchPage.moveToPlayerPage("Rafa Silva", "Football", "Beşiktaş");
	}
}
