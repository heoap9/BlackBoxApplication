# Blackbox raspi with Andrid Application
#### USB 저장은 물론  WIFI AP 혹은 LAN 통신으로
#### 실시간 녹화 중인 상황을 표시하며 저장된 파일의 목록을 확인 할 수 있다!

+ 프로젝트에 사용된 요소
  + Rasspi
    + V4L2 RTSP ( Stream )
    + >https://github.com/mpromonet/v4l2rtspserver
    + FFmpeg (Record)
    + Apache2
  + Android Studio
    + LibVLC

* index
  * 본 프로젝트에 사용된 구조 (이미지)
  * <img width="821" alt="image" src="https://github.com/heoap9/BlackBoxApplication/assets/83992590/e6b9f613-44af-4847-9ca7-f4e676e9343c">


  

## Demo
* Blackbox 검증 페이지 이미지, 핵심코드
* ![Animation1](https://github.com/heoap9/BlackBoxApplication/assets/83992590/e28a01ea-fced-4231-bb48-9d74b1a2f369)
    * ~~~ JAVA
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
    * 

* 메인 화면에서 선택기능 버튼을 보여줌
* ![Animation2](https://github.com/heoap9/BlackBoxApplication/assets/83992590/7d946f09-0927-45f3-bc5e-c79f87ad87ba)
  * ~~~JAVA
    
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


* FIleList 목록
* ![Animation3](https://github.com/heoap9/BlackBoxApplication/assets/83992590/d840b46a-6605-4649-8636-11f3b50dc1bc)

  * ~~~JAVA
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
  
* Filelist 선택 시 다운로드
* ![Animation4](https://github.com/heoap9/BlackBoxApplication/assets/83992590/53e61c01-ac4f-420f-9823-7679c4c8ddd1)

  * ~~~JAVA
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

* Stream And 녹화된 파일 재생
  * ~~~JAVA
        @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlc);

        libVlc = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVlc);
        videoLayout = findViewById(R.id.video_layout);
    }