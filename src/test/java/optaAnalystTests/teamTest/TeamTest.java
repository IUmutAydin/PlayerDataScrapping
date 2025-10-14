package optaAnalystTests.teamTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import optaAnalystPages.DriverManager;
import optaAnalystTests.baseTest.BaseTest;
import tasks.LeagueTask;
import utilities.WaitUtilities;

public class TeamTest extends BaseTest {

	private ExecutorService executorService;

	@Override
	@BeforeMethod
	public void setUp() {
		super.setUp();
		executorService = Executors.newFixedThreadPool(8);
	}

	@Test
	public void createTeams() {
		while (!oanp.getNextLeagues().isEmpty()) {
			leaguePage = oanp.getLeaguePage();
			WaitUtilities.explicitlyWaitUntilPageComplition(10);

			leaguePage.selectTeams();
			leaguePage.getTeams();

			while (!leaguePage.getNextTeams().isEmpty()) {
				System.out.println(leaguePage.getNextTeams().size());
				teamPage = leaguePage.getTeamPage();
				WaitUtilities.explicitlyWaitUntilPageComplition(10);
				teamPage.createTeam();
			}

			leaguePage.navigateToPreviousURL();
			WaitUtilities.explicitlyWaitUntilPageComplition(10);
		}
	}

	@Test
	public void createTeams2() {
		oanp.setLeagueURLs();

		List<Future<Void>> futures = new ArrayList<>();

		oanp.getLeagueURLs().stream().map(url -> new LeagueTask(url, oanp.getNextLeagues().poll()))
				.forEach(task -> futures.add(executorService.submit(task)));

		DriverManager.quitDriver();

		futures.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	@AfterClass
	public void tearDown() {
		super.tearDown();
		executorService.shutdown();

		try {
			if (executorService.awaitTermination(60, TimeUnit.SECONDS))
				executorService.shutdownNow();
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
	}
}
