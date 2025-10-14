package optaAnalystTests.statTest;

import java.util.List;
import java.util.stream.IntStream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import database.memberProcesses.MatchDatabaseProcesses;
import optaAnalystPages.MatchPage;
import optaAnalystTests.baseTest.BaseTest;

public class StatTest extends BaseTest {

	@BeforeClass
	@Override
	public void setUp() {
		super.setUp();
	}

	@Test
	public void createTypes() {
		/*
		 * oanp.acceptCookies(); StatPage sp = new StatPage(); sp.createTypes();
		 */
	}

	@Test
	public void createMatch() {
		oanp.acceptCookies();

		IntStream.range(1, 2).forEach(i -> {
			String url = MatchDatabaseProcesses.getMatchURL(i, 0);
			MatchPage mp;

			while (url != null) {

				System.out.println("------------------------------------------------------------");
				mp = new MatchPage(url, i);
				mp.createMatch(mp1 -> {
					mp1.createRecords();
					return null;
				});
				url = MatchDatabaseProcesses.getMatchURL(i, 0);
			}
		});
	}

	@Test
	public void createRecords() {
		oanp.acceptCookies();

		IntStream.range(1, 2).forEach(i -> {
			List<String> matchURLs = MatchDatabaseProcesses.getMatchURLs(i, 2);

			matchURLs.forEach(url -> {
				System.out.println("------------------------------------------------------------");
				MatchPage mp = new MatchPage(url, i);
				mp.createMatch(mp1 -> {
					mp1.createRecords();
					return null;
				});
			});
		});

	}

	@AfterClass
	@Override
	public void tearDown() {
		super.tearDown();
	}
}
