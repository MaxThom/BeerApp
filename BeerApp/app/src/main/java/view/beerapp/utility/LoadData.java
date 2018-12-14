package view.beerapp.utility;

import android.os.AsyncTask;

import java.util.ArrayList;

import view.beerapp.contract.IAsyncResponse;
import view.beerapp.contract.IDatabaseData;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;
import view.beerapp.repository.FirebaseUserRepository;

/**
 * Class used to load the starting data of the application
 */
public class LoadData extends AsyncTask<String, Integer, String> {

    // Use async task, delegate receive the result
    public IAsyncResponse delegate = null;

    /**
     * Process the task, in this case, ask all the data from firebase
     * @param data list of data
     * @return result
     */
    @Override
    protected String doInBackground(String... data) {
        ArrayList<IDatabaseData> listDb = new ArrayList<>();

        listDb.add(FirebaseLocationRepository.getInstance());
        listDb.add(FirebaseDrinkRepository.getInstance());
        listDb.add(FirebaseUserRepository.getInstance());
        publishProgress(20);

        // For each data document, wait their reception
        int i = 0;
        for (IDatabaseData dt : listDb) {
            while (!dt.isDataReady()) {

            }
            publishProgress((int) (((++i) / (float) listDb.size()) * 100));
        }

        return "";
    }

    /**
     * Send progress to the delegate
     * @param progress current progression
     */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        delegate.progressUpdate(progress[0]);
    }

    /**
     * Send result to the delegate
     * @param result the result
     */
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
