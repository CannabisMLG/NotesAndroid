package com.example.cugfu.notes;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

public class ViewItem extends AppCompatActivity {
    private TextView viewNote, RateKp, myRate, RateMy;
    private String name, pos, oz;
    private boolean ch = false;
    private DBhelper db;
    private final int is_ch = -5500, not_ch = -5550;
    private String cath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarview);
        Intent intent = getIntent();
        toolbar.setTitle(intent.getStringExtra("name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewNote = (TextView) findViewById(R.id.viewNote);
        RateKp = (TextView) findViewById(R.id.RateKp);
        myRate = (TextView) findViewById(R.id.myRate);
        RateMy = (TextView) findViewById(R.id.RateMy);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(openFileInput(intent.getStringExtra("name"))));
            String s = "";
            while (rd.ready())
            {
                s += rd.readLine();
            }
            viewNote.setText(s);
            Log.d("mLogs", "yay");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RateKp.setText(intent.getStringExtra("kpRate"));

        if(intent.getStringExtra("ch").equals("1"))
        {
            myRate.setVisibility(View.VISIBLE);
            RateMy.setText(intent.getStringExtra("myRate"));
            RateMy.setVisibility(View.VISIBLE);
            ch = true;
        }
        name = intent.getStringExtra("name");
        pos = intent.getStringExtra("position");
        cath = intent.getStringExtra("cath");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);

        if(ch)
        {
            if(cath.equals("films"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непросмотренное");
            if(cath.equals("games"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непройденное");
            if(cath.equals("books"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непрочитанное");
        }
        else
        {
            if(cath.equals("films"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как просмотренное");
            if(cath.equals("games"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как пройденное");
            if(cath.equals("books"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как прочитанное");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            Intent intent = new Intent();
            intent.putExtra("Del", "Del");
            intent.putExtra("name", name);
            if(ch) intent.putExtra("ch", "1");
            else intent.putExtra("ch", "0");
            intent.putExtra("position", pos);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        if(id == R.id.action_edit)
        {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("title", "Редактирование");
            intent.putExtra("type", "");
            startActivityForResult(intent, 0);
        }
        if(id == is_ch)
        {
            Intent intent = new Intent();
            intent.putExtra("oldname", name);
            intent.putExtra("oldch", getIntent().getStringExtra("ch"));
            intent.putExtra("name", name);
            intent.putExtra("kpRate", RateKp.getText().toString());
            intent.putExtra("myRate", RateMy.getText().toString());
            intent.putExtra("ch", "0");
            intent.putExtra("position", pos + "");
            setResult(-2, intent);
            finish();
        }
        if(id == not_ch)
        {
            LayoutInflater li = LayoutInflater.from(this);
            View promtsView = li.inflate(R.layout.prompt, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(promtsView);
            final EditText input = (EditText) promtsView.findViewById(R.id.input);
            builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    oz = input.getText().toString();
                    if(Double.parseDouble(oz) <= 10) {
                        Intent intent = new Intent();
                        intent.putExtra("oldname", name);
                        intent.putExtra("oldch", getIntent().getStringExtra("ch"));
                        intent.putExtra("name", name);
                        intent.putExtra("myRate", Double.parseDouble(oz) + "");
                        intent.putExtra("kpRate", RateKp.getText().toString());
                        intent.putExtra("ch", "1");
                        intent.putExtra("position", pos + "");
                        setResult(-2, intent);
                        finish();
                    }
                    else Toast.makeText(ViewItem.this, "Оценка идёт по 10-ти бальной шкале", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        if(id == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -2) {
            Intent intent = new Intent();
            intent.putExtra("oldname", name);
            intent.putExtra("oldch", getIntent().getStringExtra("ch"));
            intent.putExtra("name", data.getStringExtra("name"));
            intent.putExtra("kpRate", data.getStringExtra("kpRate"));
            intent.putExtra("myRate", data.getStringExtra("myRate"));
            intent.putExtra("ch", data.getStringExtra("ch"));
            intent.putExtra("position", pos + "");
            intent.putExtra("type", data.getStringExtra("type"));
            setResult(-2, intent);
            finish();
        }
    }
}
