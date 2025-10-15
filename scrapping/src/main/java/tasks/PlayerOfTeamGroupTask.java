package tasks;

import java.util.Queue;

import optaAnalystPages.OptaAnalystNavigatorPage.League;
import optaAnalystPages.PlayerPage;
import optaAnalystPages.TeamPage;
import utilities.WaitUtilities;

public class PlayerOfTeamGroupTask extends PlayerTask {

	private Queue<String> nextTeams;

	public PlayerOfTeamGroupTask(String leagueURL, League league, Queue<String> nextTeams) {
		super(leagueURL, league);
		this.nextTeams = nextTeams;
	}

	@Override
	public Void call() throws Exception {
		String teamName = null;
		try {
			setDriver();
			setPages();

			TeamPage teamPage;
			while (!nextTeams.isEmpty()) {
				teamPage = leaguePage.getTeamPage(nextTeams.poll());
				WaitUtilities.explicitlyWaitUntilPageComplition(10);
				teamName = teamPage.getTeamName();
				teamPage.getPlayers();

				PlayerPage playerPage;
				while (!teamPage.getNextPlayers().isEmpty()) {
					playerPage = teamPage.getPlayerPage(teamName);

					System.out.println(Thread.currentThread().getName() + " " + league.getLeagueName() + " "
							+ driver.getCurrentUrl() + " " + teamPage.getNextPlayers().size() + " " + nextTeams.size());

					WaitUtilities.explicitlyWaitUntilPageComplition(10);
					playerPage.createPlayer();
				}
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " " + driver.getCurrentUrl() + teamName);
			e.printStackTrace();
		}

		return null;
	}

}
