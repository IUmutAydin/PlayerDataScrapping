package optaAnalystPages;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import database.memberProcesses.MatchDatabaseProcesses;
import database.memberProcesses.PlayerDatabaseProcesses;
import database.memberProcesses.StatDatabaseProcesses;
import database.memberProcesses.TeamDatabaseProcesses;
import members.match.Match;
import members.stat.PlayerMatch;
import members.stat.StatRecord;
import utilities.JavaScriptUtilities;
import utilities.WaitUtilities;

public class MatchPage extends BasePage {

	private Map<Entry<Integer, String>, Integer> homeTeamPlayerDataMap;
	private Map<Entry<Integer, String>, Integer> awayTeamPlayerDataMap;

	protected String matchURL;
	private int competitionID;
	private Integer matchID;
	private int matchDuration = 90;

	private Integer homeTeamID;
	private Integer awayTeamID;

	private By iframe = By.xpath("/html/body/article/div/div[2]/iframe");
	private By lineups = By.xpath("/html/body/div[1]/div[3]/ul/li[1]");
	private By statRecords = By.xpath("/html/body/div[1]/div[3]/ul/li[3]");
	private By xgMap = By.xpath("/html/body/div[1]/div[3]/ul/li[5]");
	private By statTypes = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[1]/div/ul/li/span[1]");
	private By teamDatas = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/table/tbody/tr[1]");
	private By matchDate = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/table/tbody/tr[3]/td/div/span[2]");
	private By matchDate1 = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/table/tbody/tr[2]/td/div/span[2]");
	private By additionalDatas = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/div[1]");

	private By homeTeamPlayerList = By
			.xpath("//*[contains(@id, 'Opta_')]/div/div/div/div/table/tbody/tr/td[1]/table/tbody");
	private By awayTeamPlayerList = By
			.xpath("//*[contains(@id, 'Opta_')]/div/div/div/div/table/tbody/tr/td[2]/table/tbody");

	private By firstHalf = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[4]/button[2]");
	private By additionalTime = By.xpath(
			"//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[2]//*[local-name() = 'svg']//*[local-name() = 'g'][2]//*[local-name() = 'text']");

	public MatchPage() {
		super();
	}

	public MatchPage(String matchURL, int competitionID) {
		super();
		this.matchURL = matchURL;
		this.competitionID = competitionID;
		navigateToURL(convertURL(matchURL));
	}

	public void createMatch(Function<MatchPage, Void> statCreator) {
		// switchFrame();
		homeTeamID = TeamDatabaseProcesses.getID(getHomeTeam(), true);
		awayTeamID = TeamDatabaseProcesses.getID(getAwayTeam(), true);
		Date matchDate = getMatchDate();

		if (matchDate.toLocalDate().isBefore(LocalDate.now())) {
			int attendance = getAttendance();
			String referee = getReferee();
			int homeScore = getHomeScore();
			int awayScore = getAwayScore();

			if (!MatchDatabaseProcesses.exists(homeTeamID, awayTeamID, matchDate)) {
				MatchDatabaseProcesses.insert(new Match(homeTeamID, awayTeamID, matchDate, competitionID, getVenue(),
						attendance, referee, homeScore, awayScore));
			} else {
				matchID = MatchDatabaseProcesses.getID(homeTeamID, awayTeamID, competitionID, 24);

				MatchDatabaseProcesses.updateAttendance(matchID, attendance);
				MatchDatabaseProcesses.updateReferee(matchID, referee);
				MatchDatabaseProcesses.updateHomeScore(matchID, homeScore);
				MatchDatabaseProcesses.updateAwayScore(matchID, awayScore);
			}
		} else {
			MatchDatabaseProcesses.insertConditionally(
					new Match(homeTeamID, awayTeamID, matchDate, competitionID, getVenue(), 0, null, -1, -1));
			MatchDatabaseProcesses.updateMatchURLs(matchURL, 2);
		}

		matchID = MatchDatabaseProcesses.getID(homeTeamID, awayTeamID, competitionID, 24);

		if (matchDate.toLocalDate().isBefore(LocalDate.now()))
			statCreator.apply(this);
	}

	public void createRecords() {
		addPlayerMatches();
		StatPage sp = new StatPage(this);
		sp.createRecords();
	}

	public void createXGRecords() {
		StatPage sp = new StatPage(this);
		sp.createXGRecords();
	}

	public void addPlayerMatches() {
		if (matchID != null) {
			setMatchDuration();

			waitUntilVisibleThenScroll(20, lineups);
			click(lineups);
			setHomeTeamPlayerDataMap(homeTeamID);
			setAwayTeamPlayerDataMap(awayTeamID);
			find(homeTeamPlayerList).findElements(By.xpath(".//td[2]"))
					.forEach(player -> collectDataOfPlayer(homeTeamPlayerDataMap, homeTeamID, awayTeamID, player));
			find(awayTeamPlayerList).findElements(By.xpath(".//td[2]"))
					.forEach(player -> collectDataOfPlayer(awayTeamPlayerDataMap, awayTeamID, homeTeamID, player));
		}
	}

	private void collectDataOfPlayer(Map<Entry<Integer, String>, Integer> playerDataMap, int playedFor,
			int playedAgainst, WebElement player) {
		String playerName = getName(player.getText());
		Integer playerID = getPlayerID(playerName, playerDataMap);

		if (playerID != null) {
			PlayerMatch pm = new PlayerMatch(playerID, matchID, playedFor, playedAgainst, 0, 1, matchDuration * 60, 2);

			List<WebElement> playerDatas = player.findElements(By.xpath(".//span"));
			playerDatas.forEach(data -> {
				String time;
				String dataType = data.getDomAttribute("title");

				if (dataType != null) {
					if (dataType.equals("Substitution on")) {
						time = data.findElement(By.xpath("following-sibling::*[1]")).findElement(By.xpath("span"))
								.getText();
						pm.setStartSecond(parseMinute(time) * 60);
						pm.setStartHalf(getHalf(time));
					} else if (dataType.equals("Substitution off")) {
						time = data.findElement(By.xpath("following-sibling::*[1]")).findElement(By.xpath("span"))
								.getText();
						pm.setSubstituteOffSecond(parseMinute(time) * 60);
						pm.setSubstituteOffHalf(getHalf(time));
					} else if (dataType.equals("Yellow card")) {
						StatRecord sr = new StatRecord(dataType, playerID, matchID, -1, -1, -1, -1, 0, 1);
						time = data.findElement(By.xpath("following-sibling::*[1]")).findElement(By.xpath("span"))
								.getText();
						sr.setSecond(parseMinute(time) * 60);
						sr.setHalf(getHalf(time));
						StatDatabaseProcesses.insertStatRecord(sr);
					} else if (dataType.equals("Red card")) {
						StatRecord sr = new StatRecord(dataType, playerID, matchID, -1, -1, -1, -1, 0, 1);
						time = data.findElement(By.xpath("following-sibling::*[1]")).findElement(By.xpath("span"))
								.getText();
						sr.setSecond(parseMinute(time) * 60);
						sr.setHalf(getHalf(time));
						StatDatabaseProcesses.insertStatRecord(sr);
					} else if (dataType.equals("Second yellow card")) {
						StatRecord sr = new StatRecord(dataType, playerID, matchID, -1, -1, -1, -1, 0, 1);
						time = data.findElement(By.xpath("following-sibling::*[1]")).findElement(By.xpath("span"))
								.getText();
						sr.setSecond(parseMinute(time) * 60);
						sr.setHalf(getHalf(time));
						StatDatabaseProcesses.insertStatRecord(sr);

						StatRecord sr1 = new StatRecord("Yellow card", playerID, matchID, -1, -1, -1, -1, 0, 1);
						sr1.setSecond(parseMinute(time) * 60);
						sr1.setHalf(getHalf(time));
						StatDatabaseProcesses.insertStatRecord(sr1);
					}
				}
			});

			StatDatabaseProcesses.insertPlayerMatch(pm);
		}
	}

	private void setHomeTeamPlayerDataMap(int teamID) {
		waitUntilVisibleThenScroll(10, homeTeamPlayerList);

		List<Integer> playerShirtList = find(homeTeamPlayerList)
				.findElements(By.xpath(".//tr[contains(@class, 'Opta-Player')]/td[contains(@class, 'Opta-Shirt')]"))
				.stream().map(playerShirt -> getShirtNumber(playerShirt)).toList();

		List<String> playerNameList = find(homeTeamPlayerList)
				.findElements(By.xpath(".//tr[contains(@class, 'Opta-Player')]/td[2]")).stream()
				.map(player -> getName(player.getText())).toList();

		Map<Integer, String> playerDataSet = IntStream.range(0, playerNameList.size()).boxed()
				.collect(Collectors.toMap(playerShirtList::get, playerNameList::get));

		homeTeamPlayerDataMap = playerDataSet.entrySet().stream().flatMap(playerNameEntry -> {
			Integer playerID = PlayerDatabaseProcesses.getID(playerNameEntry.getValue(), teamID);

			if (playerID != null)
				return Stream.of(Map.entry(playerNameEntry, playerID));
			else
				return Stream.empty();

		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	private void setAwayTeamPlayerDataMap(int teamID) {
		waitUntilVisibleThenScroll(10, awayTeamPlayerList);

		List<Integer> playerShirtList = find(awayTeamPlayerList)
				.findElements(By.xpath(".//tr[contains(@class, 'Opta-Player')]/td[contains(@class, 'Opta-Shirt')]"))
				.stream().map(playerShirt -> getShirtNumber(playerShirt)).toList();

		List<String> playerNameList = find(awayTeamPlayerList)
				.findElements(By.xpath(".//tr[contains(@class, 'Opta-Player')]/td[2]")).stream()
				.map(player -> getName(player.getText())).toList();

		Map<Integer, String> playerDataSet = IntStream.range(0, playerNameList.size()).boxed()
				.collect(Collectors.toMap(playerShirtList::get, playerNameList::get));

		awayTeamPlayerDataMap = playerDataSet.entrySet().stream().flatMap(playerNameEntry -> {
			Integer playerID = PlayerDatabaseProcesses.getID(playerNameEntry.getValue(), teamID);

			if (playerID != null)
				return Stream.of(Map.entry(playerNameEntry, playerID));
			else
				return Stream.empty();

		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public Integer getPlayerID(String playerName, Map<Entry<Integer, String>, Integer> playerDataSet) {
		return playerDataSet.entrySet().stream().filter(entry -> entry.getKey().getValue().equals(playerName))
				.map(Map.Entry::getValue).findFirst().orElse(null);
	}

	public Integer getPlayerID(int shirtNumber, Map<Entry<Integer, String>, Integer> playerDataSet) {
		return playerDataSet.entrySet().stream().filter(entry -> entry.getKey().getKey() == shirtNumber)
				.map(Map.Entry::getValue).findFirst().orElse(null);
	}

	private int getShirtNumber(WebElement player) {
		JavaScriptUtilities.setVisible(player);

		return Integer.parseInt(player.getText().trim());
	}

	public String getName(String playerName) {
		playerName = playerName.replaceAll("(?<=\\p{L})'", "_");
		playerName = playerName.replaceAll("'", "");
		playerName = playerName.replaceAll("[^\\p{L}\\s.\\-_]", "");
		playerName = playerName.replaceAll("_", "'").trim();
		playerName = playerName.replaceAll("[^a-zA-Z\\-.'%]", "%").trim();

		return playerName;
	}

	public int parseMinute(String time) {
		time.trim();
		time = time.replace("'", "");
		if (time.contains("+")) {
			String[] parts = time.split("\\+");
			int regular = Integer.parseInt(parts[0].trim());
			int extra = Integer.parseInt(parts[1].trim());
			return regular + extra;
		} else {
			return Integer.parseInt(time.trim());
		}
	}

	public int getHalf(String time) {
		time = time.replace("'", "");
		if (!time.contains("+")) {
			time = time.replaceAll("[^0-9]", "").trim();

			return Integer.parseInt(time) < 46 ? 1 : 2;
		} else {
			String[] parts = time.split("\\+");
			int regular = Integer.parseInt(parts[0].replaceAll("[^0-9]", "").trim());

			return regular == 45 ? 1 : 2;
		}
	}

	public int getAdditionalTime(String time) {
		time = time.replace("'", "");
		if (time.contains("+")) {
			String extraTime = time.split("\\+")[1].split(":")[0];

			return Integer.parseInt(extraTime);
		} else
			return 0;
	}

	private void setMatchDuration() {
		getRecords();

		WaitUtilities.explicitlyWaitUntilPresent(20, additionalTime);

		waitUntilVisibleThenScroll(10, additionalTime);

		matchDuration += getAdditionalTime(find(additionalTime).getText());

		waitUntilClickableThenScroll(10, firstHalf);
		click(firstHalf);

		waitUntilVisibleThenScroll(10, additionalTime);

		matchDuration += getAdditionalTime(find(additionalTime).getText());
	}

	private String getHomeTeam() {
		WaitUtilities.explicitlyWaitUntilPresent(30, teamDatas);
		waitUntilVisibleThenScroll(30, teamDatas);

		return find(teamDatas).findElement(By.xpath(".//td[2]")).getText();
	}

	private String getAwayTeam() {
		waitUntilVisibleThenScroll(10, teamDatas);

		return find(teamDatas).findElement(By.xpath(".//td[6]")).getText();
	}

	private int getHomeScore() {
		waitUntilVisibleThenScroll(10, teamDatas);
		return Integer.parseInt(find(teamDatas).findElement(By.xpath(".//td[3]/span")).getText().trim());
	}

	private int getAwayScore() {
		waitUntilVisibleThenScroll(10, teamDatas);
		return Integer.parseInt(find(teamDatas).findElement(By.xpath(".//td[5]/span")).getText().trim());
	}

	private Date getMatchDate() {
		String date = null;
		try {
			waitUntilVisibleThenScroll(10, matchDate);
			date = find(matchDate).getText();
		} catch (TimeoutException e) {
			waitUntilVisibleThenScroll(10, matchDate1);
			date = find(matchDate1).getText();
			System.out.println(date);
			e.printStackTrace();
		}

		return dateConverter(date);
	}

	private Date dateConverter(String date) {
		String trimmedDate = date.replaceFirst("^[A-Za-z]+ ", "");

		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(ChronoField.DAY_OF_MONTH)
				.appendLiteral(' ').appendPattern("MMMM yyyy").toFormatter(Locale.ENGLISH)
				.withResolverStyle(ResolverStyle.SMART);
		LocalDate localDate = LocalDate.parse(trimmedDate, formatter);

		return Date.valueOf(localDate);
	}

	private String getVenue() {
		waitUntilVisibleThenScroll(10, additionalDatas);

		return find(additionalDatas).findElement(By.xpath(".//dl[1]/dd")).getText();
	}

	private int getAttendance() {
		waitUntilVisibleThenScroll(10, additionalDatas);
		try {
			String attendance = find(additionalDatas).findElement(By.xpath(".//dl[2]/dd")).getText();
			attendance = attendance.replace(",", "");
			return Integer.parseInt(attendance);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private String getReferee() {
		waitUntilVisibleThenScroll(10, additionalDatas);
		try {
			return find(additionalDatas).findElement(By.xpath(".//dl[3]/dd")).getText();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}

		return find(additionalDatas).findElement(By.xpath(".//dl[2]/dd")).getText();
	}

	/*
	 * public void getXGMap() { getRecords(true);
	 * WaitUtilities.explicitlyWaitUntilPresent(20, xgMap, 2, () ->
	 * getRecords(true)); waitUntilVisibleThenScroll(20, xgMap); click(xgMap); }
	 *
	 * public void setRecordType(int index) { getRecords(true);
	 * WaitUtilities.explicitlyWaitUntilPresent(20, statTypes, 2, () ->
	 * getRecords(true)); waitUntilVisibleThenScroll(20, statTypes); WebElement
	 * element = findElements(statTypes).get(index);
	 * JavaScriptUtilities.clickElement(element); }
	 */

	private void getRecords() {
		waitUntilVisibleThenScroll(20, statRecords);
		click(statRecords);
	}

	public String getXGMapURL() {
		waitUntilVisibleThenScroll(20, xgMap);

		return matchURL + find(xgMap).findElement(By.xpath(".//a")).getDomAttribute("href");
	}

	public void setRecordType(int index) {
		WaitUtilities.explicitlyWaitUntilPresent(20, statTypes, 2, () -> switchFrame());
		waitUntilVisibleThenScroll(20, statTypes);
		WebElement element = findElements(statTypes).get(index);
		JavaScriptUtilities.clickElement(element);
	}

	public String getStatRecordsURL() {
		waitUntilVisibleThenScroll(20, statRecords);

		return matchURL + find(statRecords).findElement(By.xpath(".//a")).getDomAttribute("href");
	}

	public void switchFrame() {
		WaitUtilities.explicitlyWaitUntilPresent(20, iframe);
		WaitUtilities.explicitlyWaitUntilFrame(20, iframe);
	}

	public Map<Entry<Integer, String>, Integer> getHomeTeamPlayerDataMap() {
		return homeTeamPlayerDataMap;
	}

	public Map<Entry<Integer, String>, Integer> getAwayTeamPlayerDataMap() {
		return awayTeamPlayerDataMap;
	}

	public Integer getMatchID() {
		return matchID;
	}

	public Integer getHomeTeamID() {
		return homeTeamID;
	}

	public Integer getAwayTeamID() {
		return awayTeamID;
	}

	public int getMatchDuration() {
		return matchDuration;
	}

	private static String convertURL(String url) {
		int queryIndex = url.indexOf("?");

		String query = url.substring(queryIndex);
		String newUrl = "https://dataviz.theanalyst.com/opta-football-match-centre/" + query;

		return newUrl;
	}

	public String getMatchURL() {
		return matchURL;
	}
}
