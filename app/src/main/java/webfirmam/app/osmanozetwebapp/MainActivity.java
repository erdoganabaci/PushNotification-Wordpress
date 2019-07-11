package webfirmam.app.osmanozetwebapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.onesignal.OneSignal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private CustomWebViewClient webViewClient;
    private String Url = "http://webfirmam.net";
    ProgressDialog mProgressDialog;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Yükleniyor...");

        webViewClient = new CustomWebViewClient();
        webView = findViewById(R.id.webview);//webview mızı xml anasayfa.xml deki webview bağlıyoruz
        webView.getSettings().setBuiltInZoomControls(true); //zoom yapılmasına izin verir
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient); //oluşturduğumuz webViewClient objesini webViewımıza set ediyoruz
        webView.loadUrl(Url);
        //handler ile thread kullanarak zamanı yakalıyoruz

        final DownloadData downloadData = new DownloadData();

        try {
            //aşağıdaki asynctaskin stringine parametre geçirmeye yarıyor
            final String url = "http://osmanozet.webfirmam.com.tr/appconfig/getpost.php";
            //her saniye execute et


            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try{
                        downloadData.execute(url);

                        Toast.makeText(getApplicationContext(),"Calısıyor ..",Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {

                    }
                    finally{
                        //also call the same runnable to call it at regular interval
                        handler.postDelayed(this, 3000);
                    }
                }
            };
            handler.postDelayed(runnable, 3000);



        } catch (Exception e) {
            System.out.println("Api not respond maybe site down");
        }




    }

    private class CustomWebViewClient extends WebViewClient {

        //Alttaki methodların hepsini kullanmak zorunda deilsiniz
        //Hangisi işinize yarıyorsa onu kullanabilirsiniz.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { //Sayfa yüklenirken çalışır
            super.onPageStarted(view, url, favicon);

            if(!mProgressDialog.isShowing())//mProgressDialog açık mı kontrol ediliyor
            {

                mProgressDialog.show();//mProgressDialog açık değilse açılıyor yani gösteriliyor ve yükleniyor yazısı çıkıyor
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 5000);

            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {//sayfamız yüklendiğinde çalışıyor.
            super.onPageFinished(view, url);

            if(mProgressDialog.isShowing()){//mProgressDialog açık mı kontrol açıksa kapat ama sayfa yüklenmesi tamalanırsa
                mProgressDialog.dismiss();//mProgressDialog açıksa kapatılıyor
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Bu method açılan sayfa içinden başka linklere tıklandığında açılmasına yarıyor.
            //Bu methodu override etmez yada edip içini boş bırakırsanız ilk url den açılan sayfa dışında başka sayfaya geçiş yapamaz

            view.loadUrl(url);//yeni tıklanan url i açıyor
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

            //	if(errorCode !=null){
            //		Timeout
            //	} şeklinde kullanabilirsiniz



        }
    }
    public void onBackPressed() //Android Back Buttonunu Handle ediyoruz. Back butonu bir önceki sayfaya geri dönecek
    {
        if(webView.canGoBack()){//eğer varsa bir önceki sayfaya gidecek
            webView.goBack();
        }else{//Sayfa yoksa uygulamadan çıkacak
            super.onBackPressed();
        }
    }


}
