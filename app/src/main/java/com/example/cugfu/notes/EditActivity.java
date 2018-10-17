package com.example.cugfu.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EditActivity extends AppCompatActivity {

    private EditText name, note, kpRate, myRate;
    private CheckBox ch;
    private TextView textView;
    private String oldname, oldch, pos;
    private boolean edit = false, check = false;

    //создание кноки add
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else {
            Intent intent = new Intent();
            if (name.getText().length() != 0 && note.getText().length() != 0 && kpRate.getText().length() != 0) {
                if (myRate.getText().length()!=0 && Double.parseDouble(myRate.getText().toString()) <= 10 &&  Double.parseDouble(kpRate.getText().toString()) <= 10) {
                    intent.putExtra("name", name.getText().toString().trim());
                    try {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(name.getText().toString().trim(), MODE_PRIVATE)));
                        bw.write(note.getText().toString());
                        bw.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ch.isChecked()) {
                        intent.putExtra("ch", "1");
                        intent.putExtra("myRate", myRate.getText().toString());
                    } else {
                        intent.putExtra("ch", "0");
                        intent.putExtra("myRate", "0");
                    }
                    intent.putExtra("kpRate", kpRate.getText().toString());

                    if (edit) {
                        intent.putExtra("oldname", oldname);
                        intent.putExtra("oldch", oldch);
                        intent.putExtra("position", pos);
                        Log.d("mLogs", pos);
                    }
                    if(check && myRate.getText().length() == 0)
                        return false;
                    else {
                        setResult(-2, intent);
                        finish();
                        return true;
                    }
                } else {
                    Toast.makeText(EditActivity.this, "Оценка идёт по 10-ти бальной шкале", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(EditActivity.this, "Заполните все поля", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        edit = intent.getStringExtra("type").equals("edit");
        if(edit)
        {
            oldname = intent.getStringExtra("oldname");
            oldch = intent.getStringExtra("oldch");
            pos = intent.getStringExtra("position");
            Log.d("mLogs", pos);
        }

        name = (EditText) findViewById(R.id.editTextName);
        note = (EditText) findViewById(R.id.editTextNote);
        kpRate = (EditText) findViewById(R.id.editTextkpRate);
        myRate = (EditText) findViewById(R.id.editTextmyRate);
        ch = (CheckBox) findViewById(R.id.checkBox);
        textView = (TextView) findViewById(R.id.textView4);
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    myRate.setVisibility(View.VISIBLE);
                    myRate.setText("");
                    textView.setVisibility(View.VISIBLE);
                    check = true;
                }
                else
                {
                    myRate.setText("0");
                    myRate.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    check = false;
                }
            }
        });
    }
}