package com.cookandroid.capstone;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Com_writeActivity extends Activity {

    Button btn_write; //글작성 버튼
    ArrayAdapter<CharSequence> adapter = null;
    Spinner spinner = null;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        btn_write = findViewById(R.id.reg_button);
        btn_cancel = findViewById(R.id.btn_cancel);

        adapter = ArrayAdapter.createFromResource(this, R.array.alba, android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.spncom);
        spinner.setAdapter(adapter);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia_write();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Com_detailActivity.class);
                startActivity(intent);
            }
        });

    }
    public class dia_write() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Com_writeActivity.this);
            builder.setTitle("안내");
            builder.setMessage("등록 하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"등록 되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Com_detailActivity.class);
                startActivity(intent);
                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), Com_writeActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

    }
}
