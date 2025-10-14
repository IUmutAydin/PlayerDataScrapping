package optaAnalystTests.matchTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import optaAnalystPages.DriverManager;
import optaAnalystTests.baseTest.BaseTest;
import tasks.MatchURLTask;

public class MatchTest extends BaseTest {

	private ExecutorService executorService;

	@BeforeClass
	@Override
	public void setUp() {
		super.setUp();

		executorService = Executors.newFixedThreadPool(8);
	}

	@Test
	private void createMatchURLs() {
		oanp.setLeagueURLs();

		List<Future<Void>> futures = new ArrayList<>();

		oanp.getLeagueURLs().stream().forEach(
				url -> futures.add(executorService.submit(new MatchURLTask(url, oanp.getNextLeagues().poll()))));

		DriverManager.quitDriver();

		futures.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});

	}

	@AfterClass
	@Override
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
