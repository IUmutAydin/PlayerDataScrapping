package optaAnalystTests.baseTest;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import optaAnalystPages.BasePage;
import optaAnalystPages.DriverManager;
import optaAnalystPages.LeaguePage;
import optaAnalystPages.OptaAnalystNavigatorPage;
import optaAnalystPages.TeamPage;
import utilities.Utility;

public class BaseTest {

	protected WebDriver driver;
	protected BasePage basePage;
	protected OptaAnalystNavigatorPage oanp;
	protected LeaguePage leaguePage;
	protected TeamPage teamPage;
	private String url = "https://theanalyst.com/eu";

	@BeforeClass
	public void setUp() {
		driver = DriverManager.getDriver();
		driver.get(url);
		driver.manage().window().maximize();
		basePage = new BasePage();
		Utility.setDriver();
		oanp = new OptaAnalystNavigatorPage();
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
