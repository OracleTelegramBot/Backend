package dev.sammy_ulfh.ai.service;

import dev.sammy_ulfh.ai.entity.TaskEntity;
import dev.sammy_ulfh.ai.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorSearchService {

    private static final Logger log = LoggerFactory.getLogger(VectorSearchService.class);

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Returns IDs of the top-K tasks in the sprint most semantically similar
     * to the query. Embeddings are generated and persisted lazily on first access.
     */
    public Set<Long> findSimilarTaskIds(String query, Long sprintId, int topK) {
        List<TaskEntity> tasks = taskRepository.findBySprintId(sprintId);
        if (tasks.isEmpty()) return Collections.emptySet();

        for (TaskEntity task : tasks) {
            if (task.getEmbedding() == null && task.getName() != null) {
                try {
                    float[] vector = embeddingService.embed(task.getName());
                    task.setEmbedding(embeddingService.toBytes(vector));
                    taskRepository.save(task);
                } catch (Exception e) {
                    log.warn("Could not embed task {}: {}", task.getId(), e.getMessage());
                }
            }
        }

        float[] queryVector;
        try {
            queryVector = embeddingService.embed(query);
        } catch (Exception e) {
            log.warn("Could not embed query, falling back to full context: {}", e.getMessage());
            return fallbackAllIds(tasks, topK);
        }

        record Scored(Long id, double score) {}

        List<Scored> scored = tasks.stream()
                .filter(t -> t.getEmbedding() != null)
                .map(t -> new Scored(
                        t.getId(),
                        embeddingService.cosineSimilarity(
                                queryVector,
                                embeddingService.fromBytes(t.getEmbedding()))))
                .sorted(Comparator.comparingDouble(Scored::score).reversed())
                .limit(topK)
                .toList();

        Set<Long> ids = new LinkedHashSet<>();
        scored.forEach(s -> ids.add(s.id()));
        return ids;
    }

    private Set<Long> fallbackAllIds(List<TaskEntity> tasks, int topK) {
        Set<Long> ids = new LinkedHashSet<>();
        tasks.stream().limit(topK).forEach(t -> ids.add(t.getId()));
        return ids;
    }
}
