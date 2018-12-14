package view.beerapp.contract;

/**
 * Used to verify if all the data from firebase has arrived
 */
public interface IDatabaseData {
    /**
     * Is the data ready to be used
     * @return true if ready
     */
    boolean isDataReady();
}
