package com.example.blackboxapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Blackbox 단말기와 안드로이드 어플리케이션에서 서로의 관계가 맞는지를 검증하는 액티비티 코드로
 * pi에서는 apache2 웹서버로 공유되는 웹페이지에서 해당 코드를 가져와 검증한다
 */
public class comparehascode extends AppCompatActivity {
    EditText editText;
    String ipaddr;
    TextView textView;
    static final String comparecode = "찬용님바보"; // pi에서 받아와 안드로이드 기기와 검증할 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparehascode);
        editText = findViewById(R.id.textView);
        textView = findViewById(R.id.textView2);

        //현재 접속된 wifi의 주소를 출력한다
        editText.setText(getWifiAddr(getApplicationContext()));

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipaddr = editText.getText().toString();
                //pi안의 있는 서버의 데이터와 직접적으로 비교하여 검증하는 목적으로 사용한다
                new CompareTask().execute(ipaddr);
            }


        });
    }

    /**
     * 검증 중 뒤로가기 액션이 감지되면 어플리케이션을 종료함
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * @param context 현재 실행중인 어플리케이션 단말의 wifi addr를 넣어준다
     * @return 들어온 ip addr(int)값으로 되어있는 데이터를
     * ip 형태 ex(127.0.0.1) 문자열로 변경하여 return 한다
     */
    public String getWifiAddr(Context context){
        WifiManager wifiManager =(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ipaddr = Formatter.formatIpAddress(ipAddress);

            if(ipaddr != null){
                return ipaddr;
            }else{
                return "연결된 WIFI가 없습니다";
            }
        }
        return null;
    }

    /**
     * edittext에서 입력받은 ip주소로 접근하여
     * blackbox 단말 코드가 맞는지를 검증하기 위해 해당 단말의 웹서버로 접근하여 처리한다
     * 접속이 안되거나, 맞지 않는 코드라면 false 값을 반환시켜 사용자에게 알려준다
     */
    private class CompareTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String ipaddr = params[0];
            try {
                // PHP 페이지 URL 지정
                URL url = new URL("http://" + ipaddr + "/compare.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                // PHP 페이지 응답 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = reader.readLine();
                reader.close();
                conn.disconnect();
                        //응답에 성공한다면
                        //검증코드가 맞는지를 확인하여 반환한다
                return line.equals(comparecode);

            } catch (UnknownHostException e) {
                return false; // 네트워크 연결 실패
            } catch (IOException e) {
                e.printStackTrace();
                return false; // 기타 예외 처리
            }
        }

        /**
         * @param result The result of the operation computed by {@link #doInBackground}.
         *  비교 검증( blackbox 단말기와 안드로이드 단말기의 확인 절차) 쓰레드에서 응답받은 예외처리(접속실패나 검증실패)
         *  를 감지하면 해당 검증 창을 종료하고 main으로 진입하여 절차를 진행한다
         *
         *  다만 검증에 실패할 경우 사용자에게 실패 문구를 보여준다.
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                MainActivity.raspiurl = ipaddr;
                Intent intent = new Intent();
                intent.putExtra("addr", "1");
                setResult(RESULT_OK, intent);
                finish();
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
