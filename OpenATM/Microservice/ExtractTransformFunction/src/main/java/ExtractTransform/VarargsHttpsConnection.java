/**
 * Name: Cai Yuejun Leon
 */
package ExtractTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import com.google.common.base.MoreObjects;

public class VarargsHttpsConnection {

	/**
	 * The API Key is not supposed to be embedded here but outside the code level and hashed by HMAC and etc as metadata. 
	 * This is due to limited time constraint in programming assignment.
	 */
	private static final String API_KEY = "G9Tw58HE6HDzyq94HFmnd2yOymAuU32k2mEgL3oTVbhLl6E1opu5Hqxb5BASwCWv";
	/**
	 * The browser user agent is provided.
	 */
	private static final String USER_AGENT = "Mozilla/5.0";

	/**
	 * GET/ request.
	 * For the programming assignment, it is simpler to program using HttpsURLConnection to open the connection instead of constructing CRUD (e.g. spring framework) for it.
	 * 
	 * @param getUrl Varying string array for the get url request to obtain a response.
	 * @throws IOException IOException
	 * @throws Exception Exception
	 */
	public static String sendGET(String... getUrl) throws IOException, Exception {
		// this is to typecheck
		String params[] = getUrl;
		if (params.length > 2) {
			throw new Exception("sendGet has invalid arguments");
		}
		String url = Constants.EMPTYSTR;
		String newUserAgent = Constants.EMPTYSTR;
		if (params.length > 1 && params.length <= 2) {
			newUserAgent = MoreObjects.firstNonNull(params[1], USER_AGENT);
		}
		if (params.length <= 1) {
			url = MoreObjects.firstNonNull(params[0], Constants.EMPTYSTR);
		}
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("accept", "application/json");
		con.addRequestProperty("Authorization","api-key");
		con.setRequestProperty("api-key", API_KEY);
		con.setRequestProperty("User-Agent", newUserAgent);
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode + " for URL " + url);
		if (responseCode == HttpsURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// System.out.println(response.toString());
			return response.toString();
		} else {
			System.out.println("GET request not worked");
			return Constants.EMPTYSTR;
		}

	}

	/**
	 * POST/ request.
	 * For the programming assignment, it is simpler to program using HttpsURLConnection to open the connection instead of constructing CRUD (e.g. spring framework) for it.
	 * 
	 * @param postUrl Varying string array for post request to obtain a response.
	 * @param postParams Post parameter
	 * 
	 * @throws IOException IOException
	 * @throws Exception Exception
	 */
	public void sendPOST(String... postUrl) throws IOException, Exception {
		// this is to typecheck
		String params[] = postUrl;
		if (params.length > 3) {
			throw new Exception("sendPOST has invalid arguments");
		}
		// typechecking codes
		String url = MoreObjects.firstNonNull(postUrl[0], Constants.EMPTYSTR);
		String newPostParams = MoreObjects.firstNonNull(postUrl[1], Constants.EMPTYSTR);
		String newUserAgent = MoreObjects.firstNonNull(postUrl[2], Constants.EMPTYSTR);

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", newUserAgent);

		// For POST only - START
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(newPostParams.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request not worked");
		}
	}

}