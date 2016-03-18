package android.bishe.Utils;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Lenovo on 2016/2/29.
 */
public class HttpData extends AsyncTask<String, Void, String> {

    private HttpClient mHttpClient;
    private HttpGet mHttpGet;
    private HttpResponse mHttpResponse;
    private HttpEntity mHttpEntity;
    private HttpGetDataListener listener;

    private InputStream in;

    private String url;

    public HttpData(String url,HttpGetDataListener listener) {
        this.url=url;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            mHttpClient=new DefaultHttpClient();
            mHttpGet=new HttpGet(url);
            mHttpResponse=mHttpClient.execute(mHttpGet);
            mHttpEntity=mHttpResponse.getEntity();
            in=mHttpEntity.getContent();
            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String line=null;
            StringBuffer sb=new StringBuffer();
            while ((line=br.readLine())!=null) {
                sb.append(line);
            }
            return sb.toString();

        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "".toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "".toString();
        }
    }
    @Override
    protected void onPostExecute(String result) {
        listener.getDataUrl(result);
        super.onPostExecute(result);
    }



}
