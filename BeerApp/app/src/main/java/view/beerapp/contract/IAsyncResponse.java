package view.beerapp.contract;

/**
 * Interface for receiving response from Async task
 * @param <T>
 */
public interface IAsyncResponse<T> {
    /**
     * Receive the result when task end
     * @param output the result
     */
    void processFinish(T output);

    /**
     * Receive update of the process on the task
     * @param progress the progress indicator
     */
    void progressUpdate(int progress);
}
