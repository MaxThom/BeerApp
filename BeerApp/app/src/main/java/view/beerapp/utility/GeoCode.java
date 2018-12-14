package view.beerapp.utility;

import android.os.AsyncTask;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import view.beerapp.contract.IAsyncResponse;
import view.beerapp.entities.Location;

/**
 * Class to fetch the precise location of an address
 */
public class GeoCode extends AsyncTask<Location, Integer, Location> {

    // URL and API key to use the Geo Code Api from Google. The data is passed in JSON format
    private static final String URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCv0VfdULt86FJsPWu3HLenaN7dwwOk50Q";

    // Receive the results and progress update
    public IAsyncResponse delegate = null;

    /**
     * Receive the address and ccall the geocode api to receive data from that location
     * @param fullAddress address to sent
     * @return JSON of all the information
     */
    public String getJSONByGoogle(String fullAddress) {
        try {
            URL url = new URL(URL + "?address=" + URLEncoder.encode(fullAddress, "UTF-8") + "&key=" + API_KEY);

            // Open the Connection
            URLConnection conn = url.openConnection();
            ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
            IOUtils.copy(conn.getInputStream(), output);
            output.close();

            return output.toString();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Execute the ASYNC Task, in this case, to get the data of an address.
     * Convert the data in a location model
     * @param locs The location to get
     * @return The location updated with all information
     */
    @Override
    protected Location doInBackground(Location... locs) {
        String response = getJSONByGoogle(locs[0].getFormattedAddress());
        JSONObject obj = null;
        Location loc = locs[0];

        try {
            obj = new JSONObject(response);

            JSONArray arr = obj.getJSONArray("results");
            loc.setFormattedAddress(arr.getJSONObject(0).getString("formatted_address"));
            loc.setLat(Double.valueOf(arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat")));
            loc.setLng(Double.valueOf(arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng")));

        } catch (JSONException e) {
            return null;
        }

        return loc;
    }

    /**
     * Return the result of the task
     * @param result result to be sent to the delegate
     */
    @Override
    protected void onPostExecute(Location result) {
        delegate.processFinish(result);
    }
}
