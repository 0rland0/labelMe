package com.pkindustries.labelme;

/**
 * Created by Orlando on 5/8/16.
 * Represents a task which a user should label.
 */
public class LabelingTask {

    enum LabelingTaskState {
        /** Task is not downloaded yet. */
        NOT_DOWNLOADED,
        /** Task is already downloaded but the labeling is not yet completed. */
        DOWNLOADED,
        /** Task is already fully labeled. */
        COMPLETED
    }

    private String id = null;

    /** A human readable name for the task. */
    private String name = null;

    private LabelingTaskState state = LabelingTaskState.NOT_DOWNLOADED;

    public LabelingTask(String name) {
        this.name = name;
    }
}
