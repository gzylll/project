package com.teachingassistant.Activity.Search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.MyApplication;


public class SearchMainActivity extends AppCompatActivity {

    private MyApplication app;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);
        initialNext();
    }

    private void initialNext()
    {
        app = new MyApplication();
        actionBar = getSupportActionBar();
        actionBar.setTitle(app.readAccount()+"的个人教务");
        actionBar.show();
    }

    public void cxOnClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.kbcx:
                if(app.readCourseTableName().size()==0) {
                    intent = new Intent(SearchMainActivity.this, CourseSearchActivity.class);
                    startActivity(intent);
                }else{
                    intent = new Intent(SearchMainActivity.this,CourseActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.cjcx:
                intent = new Intent(SearchMainActivity.this,GradeActivity.class);
                startActivity(intent);
                break;
            case R.id.kscx:
                intent = new Intent(SearchMainActivity.this,ExamActivity.class);
                startActivity(intent);
                break;
            case R.id.kjscx:
                Toast.makeText(SearchMainActivity.this,"努力中...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.grxx:
                Toast.makeText(SearchMainActivity.this,"努力中...",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
