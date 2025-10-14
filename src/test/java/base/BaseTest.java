package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import pages.BasePage;
import pages.PlayerSearchPage;

public class BaseTest {

	protected WebDriver driver;
	protected BasePage basePage;
	protected PlayerSearchPage playerSearchPage;
	private String url = "https://www.sofascore.com";

	@BeforeClass
	public void setUp() {
		driver = new ChromeDriver();
		driver.get(url);
		basePage = new BasePage();
		basePage.setDriver(driver);
		playerSearchPage = new PlayerSearchPage();
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
