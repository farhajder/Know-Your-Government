package com.example.knowyourgovernment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DataFetchTask extends AsyncTask<String, Void, DataFetchTask.Result> {

    private DLCallBack<String> mCallback;

    DataFetchTask(DLCallBack<String> callback) {
        setCallback(callback);
    }

    void setCallback(DLCallBack<String> callback) {
        mCallback = callback;
    }

    static class Result {
        public String mResultValue;
        public Exception mException;
        public Result(String resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
    }

    @Override
    protected void onPreExecute() {
        Log.d("DownloadTask","OnPreExcute called");
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                mCallback.updateFromDownload(null);
                cancel(true);
            }
        }
    }

    @Override
    protected DataFetchTask.Result doInBackground(String... urls) {
        Log.d("DownloadTask","doInBackground called "+urls[0]);
        Log.d("DownloadTask","doInBackground called "+!isCancelled());
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No response received.");
                }
            } catch(Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        Log.d("DownloadTask","onPostExceute called");
        if (result != null && mCallback != null) {
            if (result.mException != null) {
                mCallback.updateFromDownload(result.mException.getMessage());
            } else if (result.mResultValue != null) {
                mCallback.updateFromDownload(result.mResultValue);
            }
            mCallback.finishDownloading();
        }
    }

    @Override
    protected void onCancelled(Result result) {
    }

    private String downloadUrl(URL url) throws IOException {
        Log.d("DownloadTask","Download called");
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            Log.d("DownloadTask","getResponseCode called ");
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) { throw new IOException("HTTP error code: " + responseCode); }
            stream = connection.getInputStream();
            Log.d("DownloadTask", "getInputStream called " + stream);
            if (stream != null) { result = readStream(stream, Integer.MAX_VALUE - 5); }
        }
        catch(Exception e){ e.printStackTrace(); }
        finally {
            if (stream != null) { stream.close(); }
            if (connection != null) { connection.disconnect(); }
        }
        return result;
    }

    private synchronized String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[2147483];
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        Log.d("DownloadTask","Result " + result);
        return result;
    }
}
