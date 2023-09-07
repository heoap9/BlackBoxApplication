package com.example.blackboxapplication;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static String raspiurl;
    //단말기 검증이 완료 되면 해당 단말기의 url주소를 저장한다

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //처음 실행시키면 먼저 단말기의 검증을 마치고 해당 단말의 ip를 따라갈 수 있게
        //새로운 intent(창) 으로 실행시키는코드
        //뒤로가기나 다른 화면으로의 접근을 금지시킨다
        Intent intent = new Intent(MainActivity.this, comparehascode.class);
        startActivity(intent);

        //Stream,Filelist를 접근하는 코드로
        //해당되는 Activity로 접근하게 한다
        Button streambutton = findViewById(R.id.stream_button);
        streambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VlcActivity.class);
                VlcActivity.setUrl("rtsp://" + raspiurl + ":8555/unicast");
                startActivity(intent);
            }
        });

        Button fileListbutton = findViewById(R.id.file_list_button);
        fileListbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
            }


        });

    }
}