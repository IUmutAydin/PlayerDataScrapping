package pages;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test {
	private static final String siteHeader = "https://www.transfermarkt.com.tr";

	private static Queue<String> leagueURLs = new ArrayDeque<>();
	private static Queue<String> teamURLs = new ArrayDeque<>();
	private static Queue<String> playerURLs = new ArrayDeque<>();

	private static void collectLeagueURLs() {
		try {

			String url = "https://www.transfermarkt.com.tr/wettbewerbe/europa";
			Document doc = Jsoup.connect(url).get();

			Deque<String> pageURLs = new LinkedList<>();
			pageURLs.add(url);

			Elements pages = doc.select(
					"#yw1 > div.pager > ul > li.tm-pagination__list-item.tm-pagination__list-item--icon-next-page");

			while (!pages.isEmpty()) {
				url = pageURLs.getLast();
				doc = Jsoup.connect(url).get();
				pages = doc.select(
						"#yw1 > div.pager > ul > li.tm-pagination__list-item.tm-pagination__list-item--icon-next-page");

				if (pages.select("a").hasAttr("href")) {
					String pageURL = siteHeader + pages.select("a").attr("href");
					pageURLs.add(pageURL);
					System.out.println(pageURL);
				}
			}

			while (!pageURLs.isEmpty()) {
				String pageURL = pageURLs.poll();
				doc = Jsoup.connect(pageURL).get();

				Elements elements = doc.select(
						"#yw1 > table > tbody > tr > td:nth-child(1) > table > tbody > tr > td:nth-child(2) > a");

				for (Element element : elements) {
					String leagueURL = siteHeader + element.attr("href");
					leagueURLs.add(leagueURL);
				}
			}

			for (String leagueURL : leagueURLs) {
				System.out.println(leagueURL);
			}
			System.out.println(leagueURLs.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void collectTeamURLs() {
		Queue<String> leagueURLs = new LinkedList<>();
		leagueURLs.addAll(Test.leagueURLs);

		try {
			for (int i = 0; i < 2; i++) {
				String leagueURL = leagueURLs.poll();

				Document doc = Jsoup.connect(leagueURL).get();
				Elements elements = doc.select("#yw1 > table > tbody > tr > td.hauptlink.no-border-links");

				for (Element element : elements) {
					String teamURL = siteHeader + element.firstElementChild().attr("href");
					teamURLs.add(teamURL);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private static void collectPlayerURLs() {
		Queue<String> teamURLs = new LinkedList<>();
		teamURLs.addAll(Test.teamURLs);

		try {
			for (int i = 0; i < 2; i++) {
				String teamURL = teamURLs.poll();
				Document doc = Jsoup.connect(teamURL).get();
				Elements elements = doc.select(
						"#yw1 > table > tbody > tr > td.posrela > table > tbody > tr:nth-child(1) > td.hauptlink");

				for (Element element : elements) {
					String playerURL = siteHeader + element.firstElementChild().attr("href");
					playerURLs.add(playerURL);
				}
			}

			for (String playerURL : playerURLs)
				System.out.println(playerURL);
			System.out.println(playerURLs.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.sofascore.com");
		PlayerSearchPage page = new PlayerSearchPage();
		page.setDriver(driver);
		page.moveToPlayerPage("Rafa Silva", "Football", "Beşiktaş");

	}
}
