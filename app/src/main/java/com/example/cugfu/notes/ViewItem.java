package com.example.cugfu.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.io.InputStreamReader;

public class ViewItem extends AppCompatActivity {
    private TextView viewNote, RateKp, myRate, RateMy, kpRate, genre;
    private String name, pos, oz, genreText;
    private boolean ch = false;
    private DBhelper db;
    private final int is_ch = -5500, not_ch = -5550;
    private String cath = "";

    /**
     * Метод, выполняющийся при создании активити
     * @param savedInstanceState
     */
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
        genre = (TextView) findViewById(R.id.textGenre);
        myRate = (TextView) findViewById(R.id.myRate);
        RateMy = (TextView) findViewById(R.id.RateMy);
        kpRate = (TextView) findViewById(R.id.kpRate);

        /**
         * Чтение описания из файла и добавление его в TextView
         */
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(openFileInput(intent.getStringExtra("name"))));
            String s = "";
            while (rd.ready())
            {
                s += rd.readLine();
            }
            viewNote.setText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RateKp.setText(intent.getStringExtra("kpRate"));
        genre.setText(genre.getText() + " " + intent.getStringExtra("genre"));

        // если элемент просмотрен, то добавляем личную оценку
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
        genreText = intent.getStringExtra("genre");
        if(!cath.equals("Фильм"))
        {
            kpRate.setVisibility(View.INVISIBLE);
            RateKp.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Создание меню с учетом типа элемента
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);

        if(ch)
        {
            if(cath.equals("Фильм"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непросмотренное");
            if(cath.equals("Игра"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непройденное");
            if(cath.equals("Книга"))menu.add(Menu.NONE, is_ch, Menu.NONE, "Отметить как непрочитанное");
        }
        else
        {
            if(cath.equals("Фильм"))menu.add(Menu.NONE, not_ch, Menu.NONE, "Отметить как просмотренное");
            if(cath.equals("Игра"))menu.add(Menu.NONE, not_ch, Menu.NONE, "Отметить как пройденное");
            if(cath.equals("Книга"))menu.add(Menu.NONE, not_ch, Menu.NONE, "Отметить как прочитанное");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //если выбрано "удалить", то активити закрывается и в MainActivity предаются команды на удаление
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
        /*если выбрано "редактировать", то открывается EditActivity и в нем вводятся новые данные
          об элементе*/
        if(id == R.id.action_edit)
        {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("title", "Редактирование");
            intent.putExtra("oldname", name);
            if(ch)
                intent.putExtra("oldch", "1");
            else
                intent.putExtra("oldch", "0");
            intent.putExtra("type", "edit");
            intent.putExtra("position", pos);
            startActivityForResult(intent, 0);
        }
        /*если элемент был порсмотрен и нажат третий элемент меню, то обновляется элемент изменив
            статут с "просмотренного" на "непросмотренный" */
        if(id == is_ch)
        {
            Intent intent = new Intent();
            intent.putExtra("oldname", name);
            intent.putExtra("oldch", getIntent().getStringExtra("ch"));
            intent.putExtra("name", name);
            intent.putExtra("kpRate", RateKp.getText().toString());
            intent.putExtra("myRate", RateMy.getText().toString());
            intent.putExtra("genre", genreText);
            intent.putExtra("ch", "0");
            intent.putExtra("typeof", getIntent().getStringExtra("typeof"));
            intent.putExtra("position", pos + "");
            setResult(-2, intent);
            finish();
        }
        /*если элемент был не порсмотрен и нажат третий элемент меню, то вызывается диалог для
        оценки и обновляется элемент изменив статут с "непросмотренного" на "просмотренный"
        и добавляется полученная оценка*/
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
                        intent.putExtra("genre", genreText);
                        intent.putExtra("ch", "1");
                        intent.putExtra("typeof", cath);
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

    /**
     * Этот метод обрабатывает значения, возвращенные в случае редактирования элемента
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
            intent.putExtra("genre", data.getStringExtra("genre"));
            intent.putExtra("typeof", data.getStringExtra("typeof"));
            setResult(-2, intent);
            finish();
        }
    }
}
