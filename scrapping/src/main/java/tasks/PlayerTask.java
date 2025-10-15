package tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import optaAnalystPages.DriverManager;
import optaAnalystPages.OptaAnalystNavigatorPage.League;
import optaAnalystPages.PlayerPage;
import optaAnalystPages.TeamPage;
import utilities.WaitUtilities;

public class PlayerTask extends LeagueTask {

	private ExecutorService executorService;

	public PlayerTask(String leagueURL, League league) {
		super(leagueURL, league);
	}

	public void createPlayersofLeagues() {
		try {
			setDriver();
			setPages();

			leaguePage.selectTeams();
			leaguePage.getTeams();

			TeamPage teamPage;

			while (!leaguePage.getNextTeams().isEmpty()) {
				teamPage = leaguePage.getTeamPage();
				WaitUtilities.explicitlyWaitUntilPageComplition(10);
				String teamName = teamPage.getTeamName();
				teamPage.getPlayers();

				PlayerPage playerPage;
				while (!teamPage.getNextPlayers().isEmpty()) {
					playerPage = teamPage.getPlayerPage(teamName);
					System.out.println(Thread.currentThread().getName() + " " + league.getLeagueName() + " "
							+ driver.getCurrentUrl() + teamPage.getNextPlayers().size());
					WaitUtilities.explicitlyWaitUntilPageComplition(10);
					playerPage.createPlayer();
				}
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + league.getLeagueName() + driver.getCurrentUrl());
			e.printStackTrace();
		} finally {
			DriverManager.quitDriver();
		}
	}

	@Override
	public Void call() throws Exception {
		try {
			setDriver();
			setPages();

			leaguePage.selectTeams();
			leaguePage.getTeams();

			List<String> urls = leaguePage.getNextTeams().stream().toList();

			List<LinkedList<String>> teamLists = urls.stream()
					.collect(Collectors.groupingBy(url -> urls.indexOf(url) / 4)).values().stream()
					.map(list -> list.stream().collect(Collectors.toCollection(LinkedList::new))).toList();

			List<Future<Void>> futures = new ArrayList<>();

			executorService = Executors.newFixedThreadPool(teamLists.size());

			teamLists.forEach(
					list -> futures.add(executorService.submit(new PlayerOfTeamGroupTask(leagueURL, league, list))));

			DriverManager.quitDriver();

			futures.forEach(future -> {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});

			executorService.shutdown();

			try {
				if (executorService.awaitTermination(60, TimeUnit.SECONDS))
					executorService.shutdownNow();
			} catch (InterruptedException e) {
				executorService.shutdownNow();
			}

		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + league.getLeagueName() + driver.getCurrentUrl());
			e.printStackTrace();
		} finally {
			DriverManager.quitDriver();
		}

		return null;
	}
}
