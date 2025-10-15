package tasks;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskUtilities {

	public static void getFutures(List<Future<Void>> futureList) {
		futureList.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	public static <T> List<T> getFuturesThenCollect(List<Future<T>> futureList) {
		return futureList.stream().map(future -> {
			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();

				return null;
			}
		}).toList();
	}

	public static void shutdownExecutorService(int second, ExecutorService executorService) {
		executorService.shutdown();

		try {
			if (executorService.awaitTermination(second, TimeUnit.SECONDS))
				executorService.shutdownNow();
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
	}
}
