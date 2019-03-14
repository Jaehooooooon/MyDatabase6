package org.sajae.mydatabase6;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    TextView textView;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);

        textView = findViewById(R.id.textView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databaseName = editText.getText().toString();
                openDatabase(databaseName);
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //SQL문을 실행하려면 먼저 데이터베이스를 오픈해야됨.
                String tableName = editText2.getText().toString();
                createTable(tableName);
            }
        });
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText3.getText().toString().trim();
                String ageStr = editText4.getText().toString().trim();  //trim : 공백 없애기
                String mobile = editText5.getText().toString().trim();

                int age = -1;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (Exception e) {}

                insertData(name, age, mobile);
            }
        });
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = editText2.getText().toString();
                selectData(tableName);
            }
        });

    }

    public void selectData(String tableName) {
        println("selectData() 호출됨.");

        if (database != null) {
            String sql = "select name, age, mobile from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);   //결과값이 리턴되야하니까 rawQuery 사용 //sql문에 ?를 추가하고 null자리에 ?를 대체할 파라미터를 넣을 수 있음
            println("조회된 데이터 개수 : " + cursor.getCount());

            for (int i =0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                int age = cursor.getInt(1);
                String mobile = cursor.getString(2);

                println("#" + i + " -> " + name + ", " + age + ", " + mobile);
            }

            cursor.close();
        }
    }

    public void insertData(String name, int age, String mobile) {
        println("insertData() 호출됨.");

        if (database != null) {
            String sql = "insert into customer(name, age, mobile) values(?, ?, ?)";
            Object[] params = {name, age, mobile};

            database.execSQL(sql, params);

            println("데이터 추가함.");
        } else {
            println("먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void createTable(String tableName) {
        println("createTable() 호출됨.");

        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
            database.execSQL(sql); //결과값을 받지 않아도 되는 SQL문은 이 메소드로 실행

            println("테이블 생성됨.");
        } else {
            println("먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void openDatabase(String databaseName) {
        println("openDatabase() 호출됨.");
        /*
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);   //db 변수로 받아도 ㄴ상관
        if (database != null) {
            println("데이터베이스 오픈됨.");
        }
        */

        DatabaseHelper helper = new DatabaseHelper(this, databaseName, null, 3);
        database = helper.getWritableDatabase();   //데이터베이스에 쓸 수 있는 권한까지 해서 리턴이 됨

    }

    public void println(String data) {
        textView.append(data + "\n");
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("onCreate() 호출됨.");

            String tableName = "customer";
            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
            db.execSQL(sql); //결과값을 받지 않아도 되는 SQL문은 이 메소드로 실행
            //처음 데이터베이스를 생성했을 때 테이블만 만들어 주는 게 아니라 데이터를 미리 넣어놓는 경우, insert문 추가
            println("테이블 생성됨.");
        }
        //onOpen 메소드도 오버라이드 가능
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("onUpgrade 호출됨 : " + oldVersion + ", " + newVersion);

            if (newVersion > 1) {
                String tableName = "customer";
                db.execSQL("drop table if exists " + tableName);    //보통은 얼터테이블해서 칼럼만 변경해줌
                println("테이블 삭제함");

                String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
                db.execSQL(sql); //결과값을 받지 않아도 되는 SQL문은 이 메소드로 실행

                println("테이블 새로 생성됨.");
            }
        }
    }

}
