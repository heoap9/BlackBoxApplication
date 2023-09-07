package com.example.blackboxapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 파일리스트 액티비티에서 파일목록을 선택하면 생성되는 액티비티로
 * 다운로드와 스트림을 제공한다
 */
public class File_list_item_select extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(FileListActivity.filename);
        setContentView(R.layout.activity_file_list_item_select);

        Button return_button = findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v The view that was clicked.
             *          닫기 버튼을 누르면 이전 파일목록으로 돌아가게 한다
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("return","0");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        Button download_button = findViewById(R.id.download_button);
        download_button.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v The view that was clicked.
             *          다운로드 버튼을 누르면 디바이스에 저장과 toast으로 다운로드중인 진척도를 보여준다
             */
            @Override
            public void onClick(View v) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://"+MainActivity.raspiurl+"/Record/"+FileListActivity.filename));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FileListActivity.filename);
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Toast.makeText(File_list_item_select.this,  FileListActivity.filename+"다운로드 중..", Toast.LENGTH_SHORT).show();
                downloadManager.enqueue(request);

            }
        });

        Button stream_button = findViewById(R.id.Stream_button);
        /**
         * @param v The view that was clicked.
         *          스트림 버튼을 누르게 되면 해당 파일을 vlc플레이어에 넘겨주어 미리보기를 제공한다
         */
        stream_button.setOnClickListener(v -> {
            Intent intent = new Intent(File_list_item_select.this, VlcActivity.class);
            VlcActivity.setUrl("http://"+MainActivity.raspiurl+"/Record/"+FileListActivity.filename);
            startActivity(intent);

        });



    }
}