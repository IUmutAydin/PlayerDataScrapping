package optaAnalystTests.playerTest;

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
import tasks.PlayerTask;

public class PlayerTest extends BaseTest {

	private ExecutorService executorService;

	@Override
	@BeforeMethod
	public void setUp() {
		super.setUp();
		executorService = Executors.newFixedThreadPool(8);
	}

	@Test
	public void createPlayers() {
		oanp.setLeagueURLs();

		List<Future<Void>> futures = new ArrayList<>();

		List<String> leagueURLs = oanp.getLeagueURLs().stream().toList();

		/*
		 * leagueURLs.stream().map(url -> new PlayerTask(url,
		 * oanp.getNextLeagues().poll())) .forEach(task ->
		 * futures.add(executorService.submit(task)));
		 */

		futures.add(executorService
				.submit(new PlayerTask(leagueURLs.get(1), oanp.getNextLeagues().stream().toList().get(1))));

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
