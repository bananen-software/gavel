package software.bananen.gavel.backend.tasks;

public record TaskStatusData(String taskId,
                             String taskName,
                             float percentageComplete,
                             TaskStatus status,
                             String resultUrl) {
}
