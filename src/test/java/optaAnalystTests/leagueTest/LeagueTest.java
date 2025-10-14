package optaAnalystTests.leagueTest;

import org.testng.annotations.Test;
import optaAnalystPages.LeaguePage;
import optaAnalystTests.baseTest.BaseTest;
import utilities.WaitUtilities;

public class LeagueTest extends BaseTest {

	private LeaguePage leaguePage;

	@Test
	public void createLeagues() {
		while (!oanp.getNextLeagues().isEmpty()) {
			leaguePage = oanp.getLeaguePage();
			WaitUtilities.explicitlyWaitUntilPageComplition(10);

			leaguePage.createLeague();

			leaguePage.backPage();
			WaitUtilities.explicitlyWaitUntilPageComplition(10);
		}
	}

}
