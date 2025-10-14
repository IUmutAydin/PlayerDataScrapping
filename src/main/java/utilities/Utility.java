package utilities;

import org.openqa.selenium.WebDriver;

import optaAnalystPages.DriverManager;

public class Utility {
	public static WebDriver driver;

	public static void setDriver() {
		driver = DriverManager.getDriver();
	}
}
