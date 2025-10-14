package optaAnalystPages;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import database.memberProcesses.MatchDatabaseProcesses;
import database.memberProcesses.StatDatabaseProcesses;
import members.stat.StatRecord;
import members.stat.XGRecord;
import tasks.TaskUtilities;
import utilities.JavaScriptUtilities;
import utilities.WaitUtilities;

public class StatPage extends BasePage {

	// private By statType = By.xpath("//*[contains(@id,
	// 'Opta_')]/div/div/div[1]/div/ul/li");
	// private By statRecordTypeOfStatType = By.xpath("//*[contains(@id,
	// 'Opta_')]/div/div/div[1]/ul/li[contains(@class,
	// 'Opta-On')]/div/dl/dd/ul/li/span[2]");

	private MatchPage mp;

	private By timePanel = By.xpath(
			"/html/body/div[1]/div[3]/div[3]/div/div/div/div[2]/div[2]/div[2]/div/div[2]/*[local-name() = 'svg']/*[local-name() = 'rect']");
	private By records = By.xpath(
			"/html/body/div[1]/div[3]/div[3]/div/div/div/div[2]/div[2]/div[1]/*[local-name() = 'svg']/*[local-name() = 'g'][4]/*[local-name() = 'g']");
	private By homeTeamRecordsOfStatPanel = By.xpath(
			"//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[1]/*[local-name() = 'svg']/*[local-name() = 'g']/*[local-name() = 'g']");
	private By awayTeamRecordsOfStatPanel = By.xpath(
			"//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[3]/*[local-name() = 'svg']/*[local-name() = 'g']/*[local-name() = 'g']");

	private Function<WebElement, String> dataExtractor = element -> {
		try {
			return element.getDomAttribute("data-id");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	};

	private ExecutorService executorService;

	public StatPage(MatchPage mp) {
		super();
		this.mp = mp;
		executorService = Executors.newFixedThreadPool(5);
	}

	public void createRecords() {
		List<Future<Void>> futures = new ArrayList<>();

		IntStream.range(0, 6).forEach(i -> {
			if (i != 1)
				futures.add(executorService.submit(new StatRecordTask(i)));
		});

		TaskUtilities.getFutures(futures);
		futures.clear();

		createDistributionRecords(futures);

		TaskUtilities.shutdownExecutorService(60, executorService);

		MatchDatabaseProcesses.updateMatchURLs(mp.getMatchURL(), 1);
	}

	public void createDistributionRecords(List<Future<Void>> futures) {
		futures.add(executorService.submit(new DistributionRecordTask(0, 1, 1)));
		futures.add(executorService.submit(new DistributionRecordTask(0, 1, 2)));
		futures.add(executorService.submit(new DistributionRecordTask(0, 2, 1)));
		futures.add(executorService.submit(new DistributionRecordTask(0, 2, 2)));
		futures.add(executorService.submit(new DistributionRecordTask(1, 3, 3)));

		TaskUtilities.getFutures(futures);
	}

	public void createXGRecords() {
		List<Future<Void>> futures = new ArrayList<>();
		futures.add(executorService.submit(new XGRecordTask()));

		TaskUtilities.getFutures(futures);

		TaskUtilities.shutdownExecutorService(60, executorService);
	}

	public class StatRecordTask extends BasePage implements Callable<Void> {

		private int statTypeIndex;
		protected int timePanelWidth;

		protected List<WebElement> homeTeamPlayerRecords;
		protected List<WebElement> awayTeamPlayerRecords;
		protected List<WebElement> homeTeamPlayerRecordsOfStatPanel;
		protected List<WebElement> awayTeamPlayerRecordsOfStatPanel;

		public StatRecordTask(int statTypeIndex) {
			super();
			this.statTypeIndex = statTypeIndex;
		}

		public void createRecords() {
			setStatRecordTypes();
			setRecords();
			createRecords(homeTeamPlayerRecords, homeTeamPlayerRecordsOfStatPanel, mp.getHomeTeamPlayerDataMap());
			createRecords(awayTeamPlayerRecords, awayTeamPlayerRecordsOfStatPanel, mp.getAwayTeamPlayerDataMap());
		}

		protected void setStatRecordTypes() {
			MatchPage mp = new MatchPage();
			mp.switchFrame();
			mp.setRecordType(statTypeIndex);
			timePanelWidth = getTimePanelWidth();
		}

		protected void setRecords() {
			homeTeamPlayerRecords = getRecords("Opta-Home");
			awayTeamPlayerRecords = getRecords("Opta-Away");
			homeTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(homeTeamRecordsOfStatPanel);
			awayTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(awayTeamRecordsOfStatPanel);
		}

		protected void createRecords(List<WebElement> playerRecords, List<WebElement> playerRecordsOfStatPanel,
				Map<Entry<Integer, String>, Integer> playerDataMap) {
			if (playerRecordsOfStatPanel.size() > 0) {
				Map<WebElement, List<WebElement>> recordPairs = getRecordPairs(playerRecords, playerRecordsOfStatPanel);

				recordPairs.entrySet().forEach(recordPair -> {

					String statRecordID = recordPair.getKey().getDomAttribute("data-id");
					int shirtNumber = Integer.parseInt(
							recordPair.getKey().findElement(By.xpath(".//*[local-name() = 'text']")).getText().trim());
					Integer playerID = mp.getPlayerID(shirtNumber, playerDataMap);
					int matchID = mp.getMatchID();
					int second = getTimeOfRecord(recordPair.getKey().getDomAttribute("transform").trim());

					recordPair.getValue().forEach(statRecord -> {
						String statRecordType = getText(
								statRecord.findElement(By.xpath(".//*[local-name() = 'text'][2]/div/h3/span[1]")))
								.trim();

						int half = mp.getHalf(getText(
								statRecord.findElement(By.xpath(".//*[local-name() = 'text'][2]/div/h3/span[2]"))));

						Point position = convertPositionPointOfStatRecord(
								statRecord.getDomAttribute("transform").trim());
						double startX = position.getX();
						double startY = position.getY();

						double endX = -1;
						double endY = -1;

						Optional<WebElement> line = statRecord
								.findElements(By.xpath("preceding-sibling::*[local-name() = 'line'][1]")).stream()
								.findFirst();

						if (line.isPresent()) {
							WebElement element = line.get();
							endX = convertPositionEndOfStatRecord(element.getDomAttribute("x2").trim());
							endY = convertPositionEndOfStatRecord(element.getDomAttribute("y2").trim());
						}

						if (playerID != null) {
							StatRecord sr = new StatRecord(statRecordID, statRecordType, playerID, matchID, startX,
									startY, endX, endY, second, half);
							StatDatabaseProcesses.insertStatRecord1(sr);
						}
					});
				});
			}
		}

		protected Map<WebElement, List<WebElement>> getRecordPairs(List<WebElement> playerRecords,
				List<WebElement> playerRecordsOfStatPanel) {
			ConcurrentMap<String, List<WebElement>> playerRecordsDataID = getDataIDOfPlayerRecords(playerRecords);
			Map<String, WebElement> playerRecordsOfStatPanelDataID = getDataIDOfStatPanel(playerRecordsOfStatPanel);

			AtomicInteger i = new AtomicInteger();

			List<List<Entry<String, WebElement>>> playerRecordsOfStatPanelDataIDList = playerRecordsOfStatPanelDataID
					.entrySet().stream().collect(Collectors.groupingBy(e -> i.incrementAndGet() / 20)).values().stream()
					.toList();

			ExecutorService executorService = Executors.newFixedThreadPool(playerRecordsOfStatPanelDataIDList.size());

			List<Future<Map<WebElement, List<WebElement>>>> futures = new ArrayList<>();

			Map<WebElement, List<WebElement>> recordPairs = new HashMap<>();

			playerRecordsOfStatPanelDataIDList
					.forEach(list -> futures.add(executorService.submit(new DataSearcher(playerRecordsDataID, list))));

			futures.forEach(future -> {
				try {
					recordPairs.putAll(future.get());
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

			return recordPairs;
		}

		protected ConcurrentMap<String, WebElement> getDataIDOfStatPanel(List<WebElement> records) {
			return records.stream()
					.sorted((e1, e2) -> e1.getDomAttribute("data-id").compareTo(e2.getDomAttribute("data-id"))).collect(
							Collectors.toConcurrentMap(record -> record.getDomAttribute("data-id"), Function.identity(),
									(existingValue, newValue) -> existingValue, ConcurrentSkipListMap::new));
		}

		protected ConcurrentMap<String, List<WebElement>> getDataIDOfPlayerRecords(List<WebElement> records) {
			return records.stream()
					.sorted((e1, e2) -> e1.getDomAttribute("data-id").compareTo(e2.getDomAttribute("data-id")))
					.collect(Collectors.toConcurrentMap(record -> record.getDomAttribute("data-id"), record -> {
						List<WebElement> list = new ArrayList<>();
						list.add(record);
						return list;
					}, (existingList, newList) -> {
						List<WebElement> filteredList = newList.stream()
								.filter(newElement -> existingList.stream().noneMatch(existingElement -> {
									String newText = getText(newElement
											.findElement(By.xpath(".//*[local-name() = 'text'][2]/div/h3/span[1]")))
											.trim();
									String existingText = getText(existingElement
											.findElement(By.xpath(".//*[local-name() = 'text'][2]/div/h3/span[1]")))
											.trim();
									return existingText.equals(newText);
								})).collect(Collectors.toList());

						existingList.addAll(filteredList);

						return existingList;
					}, ConcurrentSkipListMap::new));
		}

		protected List<WebElement> getRecords(String team) {
			WaitUtilities.explicitlyWaitUntilPresent(20, records);
			waitUntilVisibleThenScroll(20, records);

			return findElements(records).stream().filter(element -> element.getDomAttribute("class").contains(team))
					.toList();
		}

		protected List<WebElement> getTeamRecordOfStatPanel(By teamRecordOfStatPanel) {
			// waitUntilVisibleThenScroll(20, teamRecordOfStatPanel);

			return findElements(teamRecordOfStatPanel).stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(dataExtractor))), ArrayList::new));
		}

		protected int getTimePanelWidth() {
			WaitUtilities.explicitlyWaitUntilPresent(60, timePanel);
			waitUntilVisibleThenScroll(20, timePanel);

			return Integer.parseInt(find(timePanel).getDomAttribute("width").trim());
		}

		protected Point convertPositionPointOfStatRecord(String position) {
			position = position.substring(position.indexOf('(') + 1, position.indexOf(')'));
			String[] parts = position.split(",");

			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);

			BigDecimal bdX = new BigDecimal(x).setScale(3, RoundingMode.HALF_UP);
			BigDecimal bdY = new BigDecimal(y).setScale(3, RoundingMode.HALF_UP);

			return new Point(bdX.doubleValue(), bdY.doubleValue());
		}

		protected double convertPositionEndOfStatRecord(String position) {
			double value = Double.parseDouble(position);

			BigDecimal bd = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);

			return bd.doubleValue();
		}

		protected int getTimeOfRecord(String position) {
			double positionX = convertPanelPositionXOfRecord(position) - 29.5;

			return (int) ((positionX / timePanelWidth) * mp.getMatchDuration() * 60);
		}

		protected double convertPanelPositionXOfRecord(String position) {
			position = position.replaceAll(".*?\\(([^,]+),.*", "$1");

			return Double.parseDouble(position);
		}

		protected String getText(WebElement element) {
			return JavaScriptUtilities.getText(element);
		}

		protected void setDriver() {
			driver = DriverManager.getDriver();
			driver.get(mp.getMatchURL() + "#tab-chalkboard");
			driver.manage().window().maximize();
		}

		@Override
		public Void call() throws Exception {
			setDriver();
			OptaAnalystNavigatorPage oanp = new OptaAnalystNavigatorPage();
			oanp.acceptCookies();

			try {
				System.out.println("****************************** Stat Type: " + statTypeIndex
						+ " is started Match URL: " + mp.getMatchURL() + " ******************************");
				createRecords();
			} catch (Exception e) {
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Stat Type: " + statTypeIndex
						+ " is not finished completely Match URL: " + mp.getMatchURL()
						+ " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				e.printStackTrace();
			} finally {
				DriverManager.quitDriver();
			}

			System.out.println("------------------------------ Stat Type: " + statTypeIndex
					+ " is finished successfully Match URL: " + mp.getMatchURL() + "Match ID: " + mp.getMatchID()
					+ "  ------------------------------");

			return null;
		}
	}

	private class DistributionRecordTask extends StatRecordTask {

		private int taskType;
		private int half;
		private int team;

		private By distribution = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[1]/div/ul/li[2]");
		private By distributionTypes = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[1]/ul/li[2]/div/dl/dd/ul/li");
		private By firstHalf = By
				.xpath("//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[4]/button[2]");
		private By secondHalf = By
				.xpath("//*[contains(@id, 'Opta_')]/div/div/div[2]/div[2]/div[2]/div/div[4]/button[3]");
		private By homeTeam = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[2]/div[1]/div/div/dl/dt[1]/span[1]");
		private By awayTeam = By.xpath("//*[contains(@id, 'Opta_')]/div/div/div[2]/div[3]/div/div/dl/dt[1]/span[1]");

		public DistributionRecordTask(int taskType, int half, int team) {
			super(1);
			this.taskType = taskType;
			this.half = half;
			this.team = team;
		}

		@Override
		public void createRecords() {
			setStatRecordTypes();
			setRecords();
			if (team == 1)
				createRecords(homeTeamPlayerRecords, homeTeamPlayerRecordsOfStatPanel, mp.getHomeTeamPlayerDataMap());
			else if (team == 2)
				createRecords(awayTeamPlayerRecords, awayTeamPlayerRecordsOfStatPanel, mp.getAwayTeamPlayerDataMap());
			else {
				createRecords(homeTeamPlayerRecords, homeTeamPlayerRecordsOfStatPanel, mp.getHomeTeamPlayerDataMap());
				createRecords(awayTeamPlayerRecords, awayTeamPlayerRecordsOfStatPanel, mp.getAwayTeamPlayerDataMap());
			}
		}

		@Override
		protected void setRecords() {
			if (team == 1) {
				homeTeamPlayerRecords = getRecords("Opta-Home");
				homeTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(homeTeamRecordsOfStatPanel);
			} else if (team == 2) {
				awayTeamPlayerRecords = getRecords("Opta-Away");
				awayTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(awayTeamRecordsOfStatPanel);

			} else {
				homeTeamPlayerRecords = getRecords("Opta-Home");
				awayTeamPlayerRecords = getRecords("Opta-Away");
				homeTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(homeTeamRecordsOfStatPanel);
				awayTeamPlayerRecordsOfStatPanel = getTeamRecordOfStatPanel(awayTeamRecordsOfStatPanel);
			}
		}

		@Override
		protected void setStatRecordTypes() {
			MatchPage mp = new MatchPage();
			mp.switchFrame();
			timePanelWidth = getTimePanelWidth();
			selectDistributionType();
			selectHalf(half);

			try {
				selectTeam(team);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		protected void selectDistributionType() {
			WaitUtilities.explicitlyWaitUntilPresent(60, distribution, 2, () -> setStatRecordTypes());
			waitUntilVisibleThenScroll(10, distribution);
			JavaScriptUtilities.clickElement(distribution);

			List<WebElement> elements = findElements(distributionTypes);

			if (taskType == 0)
				JavaScriptUtilities.clickElement(elements.get(0).findElement(By.xpath(".//span[1]")));
			else
				elements.forEach(element -> {
					if (!getText(element.findElement(By.xpath(".//span[2]"))).trim().equals("Successful passes"))
						JavaScriptUtilities.clickElement(element.findElement(By.xpath(".//span[1]")));
				});

			JavaScriptUtilities.clickElement(distribution);
		}

		private void selectHalf(int index) {
			if (index == 1) {
				waitUntilClickableThenScroll(20, firstHalf, 2, () -> selectDistributionType());
				click(firstHalf);
			} else if (index == 2) {
				waitUntilClickableThenScroll(20, secondHalf, 2, () -> selectDistributionType());
				click(secondHalf);
			}
		}

		private void selectTeam(int index) throws InterruptedException {
			if (index == 1) {
				waitUntilVisibleThenScroll(20, awayTeam);
				JavaScriptUtilities.clickElement(awayTeam);
				Thread.sleep(Duration.ofMillis(3000));
			} else if (index == 2) {
				waitUntilVisibleThenScroll(20, homeTeam);
				JavaScriptUtilities.clickElement(homeTeam);
				Thread.sleep(Duration.ofMillis(3000));
			}
		}
	}

	private class XGRecordTask extends BasePage implements Callable<Void> {

		private By outcome = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/div[2]/div[1]/h3/span");
		private By outcomeTypes = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/div[2]/div[1]/div/ul/li");
		private By patternOfPlay = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/div[2]/div[2]/h3/span");
		private By patternOfPlayTypes = By.xpath("//*[contains(@id, 'Opta_')]/div/div[1]/div[2]/div[2]/div/ul/li");
		private By xgRecord = By.xpath(
				"/html/body/div[1]/div[3]/div[5]/div/div/div[1]/div[3]/div/div/*[local-name() = 'svg']/*[local-name() = 'g']/*[local-name() = 'g']/*[local-name() = 'g'][2]/*[local-name() = 'g']");

		public void createXGRecords() {
			MatchPage mp1 = new MatchPage();
			mp1.switchFrame();

			IntStream.range(0, 4).forEach(i -> {
				String outcomeType = selectOutcomeType(i);

				IntStream.range(0, 6).forEach(j -> {
					String patternOfPlayType = selectPatternOfPlayType(j);

					try {
						Thread.sleep(Duration.ofMillis(2000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					findElements(xgRecord).forEach(xgRecord -> {
						if (xgRecord.isDisplayed()) {
							XGRecord record;

							String playerName = getText(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'foreignObject']/div/h3/span[1]")))
									.trim();
							Integer playerID;

							if (xgRecord.getDomAttribute("class").contains("Opta-Home"))
								playerID = mp.getPlayerID(playerName, mp.getHomeTeamPlayerDataMap());
							else
								playerID = mp.getPlayerID(playerName, mp.getAwayTeamPlayerDataMap());

							int matchID = mp.getMatchID();
							String hitType = getText(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'foreignObject']/div/dl/div[3]/dd")))
									.trim();
							double xgValue = Double.parseDouble(getText(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'foreignObject']/div/h3/span[2]")))
									.trim());
							int second = getTimeOfRecord(getText(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'foreignObject']/div/dl/div[2]/dd")))
									.trim());
							int half = mp.getHalf(getText(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'foreignObject']/div/dl/div[2]/dd"))));
							double startX = convertPositionOfXGRecord(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'circle']")).getDomAttribute("cx"));
							double startY = convertPositionOfXGRecord(xgRecord
									.findElement(By.xpath(".//*[local-name() = 'circle']")).getDomAttribute("cy"));

							if (playerID != null) {
								record = new XGRecord(playerID, matchID, outcomeType, patternOfPlayType, hitType,
										xgValue, startX, startY, second, half);

								StatDatabaseProcesses.insertXGRecord(record);
							}
						}
					});
				});
			});
		}

		private String selectOutcomeType(int index) {
			WaitUtilities.explicitlyWaitUntilPresent(20, outcome, 2, null);
			waitUntilVisibleThenScroll(10, outcome);
			JavaScriptUtilities.clickElement(outcome);

			waitUntilVisibleThenScroll(10, outcomeTypes);

			List<WebElement> elements = findElements(outcomeTypes);

			elements.forEach(element -> {
				if (element.getDomAttribute("class").equals("Opta-On"))
					JavaScriptUtilities.clickElement(element.findElement(By.xpath(".//span[1]")));

			});

			JavaScriptUtilities.clickElement(elements.get(index).findElement(By.xpath(".//span[1]")));

			JavaScriptUtilities.clickElement(outcome);

			return getText(elements.get(index).findElement(By.xpath(".//span[2]"))).trim();
		}

		private String selectPatternOfPlayType(int index) {
			WaitUtilities.explicitlyWaitUntilPresent(20, patternOfPlay, 2, null);
			waitUntilVisibleThenScroll(10, patternOfPlay);
			JavaScriptUtilities.clickElement(patternOfPlay);

			waitUntilVisibleThenScroll(10, patternOfPlayTypes);

			List<WebElement> elements = findElements(patternOfPlayTypes);

			elements.forEach(element -> {
				if (element.getDomAttribute("class").equals("Opta-On"))
					JavaScriptUtilities.clickElement(element.findElement(By.xpath(".//span[1]")));
			});

			JavaScriptUtilities.clickElement(elements.get(index).findElement(By.xpath(".//span[1]")));

			JavaScriptUtilities.clickElement(patternOfPlay);

			return getText(elements.get(index).findElement(By.xpath(".//span[2]"))).trim();
		}

		private double convertPositionOfXGRecord(String position) {
			double value = Double.parseDouble(position);

			BigDecimal bd = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);

			return bd.doubleValue();
		}

		private int getTimeOfRecord(String time) {
			time = time.replaceAll("[^0-9+:]", "");
			String[] parts = time.split(":");
			String minutePart = parts[0];
			int seconds = Integer.parseInt(parts[1]);

			int minutes;
			if (minutePart.contains("+")) {
				String[] minuteSplit = minutePart.split("\\+");
				minutes = Integer.parseInt(minuteSplit[0]) + Integer.parseInt(minuteSplit[1]);
			} else {
				minutes = Integer.parseInt(minutePart);
			}

			return minutes * 60 + seconds;
		}

		private String getText(WebElement element) {
			return JavaScriptUtilities.getText(element);
		}

		private void setDriver() {
			driver = DriverManager.getDriver();
			driver.get(mp.getMatchURL() + "#tab-xg-map");
			driver.manage().window().maximize();
		}

		@Override
		public Void call() throws Exception {
			setDriver();
			OptaAnalystNavigatorPage oanp = new OptaAnalystNavigatorPage();
			oanp.acceptCookies();

			try {
				System.out.println("****************************** XGRecordTask is started Match URL: "
						+ mp.getMatchURL() + " ******************************");
				createXGRecords();
			} catch (Exception e) {
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! XGRecordTask is not finished completely Match URL: "
						+ mp.getMatchURL() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				e.printStackTrace();
			} finally {
				DriverManager.quitDriver();
			}

			System.out.println("------------------------------ XGRecordTask is finished successfully Match URL: "
					+ mp.getMatchURL() + " ------------------------------");

			return null;
		}
	}

	private class DataSearcher implements Callable<Map<WebElement, List<WebElement>>> {

		private ConcurrentMap<String, List<WebElement>> playerRecords;
		private List<Entry<String, WebElement>> playerRecordsOfStatPanel;

		public DataSearcher(ConcurrentMap<String, List<WebElement>> playerRecords,
				List<Entry<String, WebElement>> playerRecordsOfStatPanel) {
			this.playerRecords = playerRecords;
			this.playerRecordsOfStatPanel = playerRecordsOfStatPanel;
		}

		private Map<WebElement, List<WebElement>> findRecord() {
			return playerRecordsOfStatPanel.stream()
					.collect(Collectors.toMap(record -> record.getValue(), record -> findRecord(record)));
		}

		private List<WebElement> findRecord(Entry<String, WebElement> recordOfStatPanel) {
			List<WebElement> record = playerRecords.get(recordOfStatPanel.getKey());
			playerRecords.remove(recordOfStatPanel.getKey());

			return record;
		}

		@Override
		public Map<WebElement, List<WebElement>> call() throws Exception {
			return findRecord();
		}
	}

	public class Point {
		private double x;
		private double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}

	/*
	 * public void createTypes() { getRecords(); createStatTypes();
	 * createStatRecordType(); }
	 *
	 * private void createStatTypes() { WaitUtilities.explicitlyWaitUntilPresent(20,
	 * statType); waitUntilVisibleThenScroll(20, statType);
	 *
	 * findElements(statType).forEach(element -> StatDatabaseProcesses
	 * .insertStatType(element.findElement(By.xpath(".//span[2]/a")).getText())); }
	 *
	 * private void createStatRecordType() {
	 * WaitUtilities.explicitlyWaitUntilPresent(20, statType);
	 * waitUntilVisibleThenScroll(20, statType);
	 *
	 * findElements(statType).forEach(element -> {
	 * JavaScriptUtilities.clickElement(element); String statType =
	 * element.findElement(By.xpath(".//span[2]/a")).getText();
	 * findElements(statRecordTypeOfStatType).forEach( statRecordType ->
	 * StatDatabaseProcesses.insertStatRecordType(statType,
	 * statRecordType.getText())); }); }
	 */

}
