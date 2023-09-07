package com.example.blackboxapplication;


import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * 파일 목록을 띄우기 위한 클래스로
 * blackbox 단말기에서는 접근할때마다 파일 정보를 갱신하여 어플리케이션으로 보여준다
 */
public class FileListActivity extends AppCompatActivity {
    static String filename;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        listView = findViewById(R.id.listView);
        // PHP 페이지에서 데이터 가져오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // PHP 페이지 URL 지정
                    URL url = new URL("http://"+MainActivity.raspiurl+"/list_files.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    // PHP 페이지 응답 읽기
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = reader.readLine();
                    reader.close();

                    // 문자열 분리
                    // 해당 php코드를 긁어오게되면 줄 구분을 나누어 arraylist에 나누어 구분하여 저장한다
                    String[] lines = line.split("<br>");
                    ArrayList<String> list = new ArrayList<>();
                    for (String l : lines) {
                        if(l.equals(".") ){//뒤로가기 디렉터리는 삭제시킨다
                            continue;
                        }else if( l.equals("..")){
                            continue;
                        }
                        list.add(l);
                    }

                    // 리스트 뷰에 데이터 표시
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(FileListActivity.this, android.R.layout.simple_list_item_1, list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ListView에 아까 저장해두었던 arrylist를 할당한다
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItem = (String) parent.getItemAtPosition(position);

                                    // Toast 메시지로 클릭한 문자열 표시
                                    FileListActivity.filename = selectedItem;
                                    Intent intent = new Intent(getApplicationContext(), File_list_item_select.class);

                                    startActivityForResult(intent,0);


                                }
                            });


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
