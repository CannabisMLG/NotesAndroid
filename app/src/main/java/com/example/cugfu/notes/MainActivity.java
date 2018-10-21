package com.example.cugfu.notes;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;

import models.Item;

public class MainActivity extends AppCompatActivity {

    private LinkedList<Item> itemsnp = new LinkedList<>();
    private LinkedList<Item> itemsp = new LinkedList<>();
    private ArrayAdapter<String> adapternp, adapterp;
    private Item tItem;
    private ListView lvnp, lvp;
    private DBhelper dbHelper;
    private int cPos;
    private boolean flag = false;
    private BottomNavigationView bnv;
    private String type = "Фильм";
    private Toolbar tb;

    private final int MENU_EDIT = 1, MENU_DELETE = 2;

    /**
     * Метод, выполняющийся при создании активити
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        tb = toolbar;
        setSupportActionBar(toolbar);

        final MainActivity th = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(th, EditActivity.class);
                intent.putExtra("title", "Создание");
                intent.putExtra("type", "");
                startActivityForResult(intent, 0);
            }
        });

        lvnp = (ListView) findViewById(R.id.listNP);
        lvp = (ListView) findViewById(R.id.listP);
        dbHelper = new DBhelper(this);

        bnv = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.firstBM:
                        lvnp.setVisibility(View.VISIBLE);
                        lvp.setVisibility(View.INVISIBLE);
                        flag = false;
                        return true;
                        case R.id.lastBM:
                        lvnp.setVisibility(View.INVISIBLE);
                        lvp.setVisibility(View.VISIBLE);
                        flag = true;
                        return true;
                }
                return false;
            }
        });

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        Cursor cursor = dataBase.query("films", null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            int idName = cursor.getColumnIndex("name");
            int idkpRate = cursor.getColumnIndex("kpRate");
            int idCh = cursor.getColumnIndex("ch");
            int idmyRate = cursor.getColumnIndex("myRate");
            int idType = cursor.getColumnIndex("type");
            do {
                if(cursor.getString(idCh).equals("1"))
                {
                    itemsp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh), cursor.getString(idType)));
                }
                else
                {
                    itemsnp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh), cursor.getString(idType)));
                    Log.d("mLog", cursor.getString(idType));
                }
            }while(cursor.moveToNext());

            refNCF();
            refCF();
        }
        else Log.d("mLog", "Член");
        cursor.close();

        lvnp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(th, ViewItem.class);
                intent.putExtra("name", itemsnp.get(position).getName());
                intent.putExtra("myRate", itemsnp.get(position).getMyRate());
                intent.putExtra("kpRate", itemsnp.get(position).getKpRate());
                intent.putExtra("ch", itemsnp.get(position).isCh());
                intent.putExtra("position", position+"");
                intent.putExtra("cath", type);
                startActivityForResult(intent, 1);
            }
        });
        lvp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(th, ViewItem.class);
                intent.putExtra("name", itemsp.get(position).getName());
                intent.putExtra("myRate", itemsp.get(position).getMyRate());
                intent.putExtra("kpRate", itemsp.get(position).getKpRate());
                intent.putExtra("ch", itemsp.get(position).isCh());
                intent.putExtra("position", position+"");
                intent.putExtra("cath", type);
                startActivityForResult(intent, 1);
            }
        });
        registerForContextMenu(lvp);
        registerForContextMenu(lvnp);
    }

    /**
     * Метод, отвечающий за создание контекстного меню
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, MENU_EDIT, 0, "Редактировать");
        menu.add(0, MENU_DELETE, 0, "Удалить");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cPos = info.position;
        if(flag) {
            tItem = itemsp.get(cPos);
        }
        else
        {
            tItem = itemsnp.get(cPos);
        }
    }

    /**
     * Метод, отвечающий за обработку нажатий на элементы контекстного меню
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case MENU_DELETE:
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                if(flag)
                {
                    dataBase.delete("films", "name= ?", new String[]{itemsp.get(cPos).getName()});
                    deleteFile(itemsp.get(cPos).getName());
                    itemsp.remove(cPos);
                    refCF();
                }
                else
                {
                    dataBase.delete("films", "name= ?", new String[]{itemsnp.get(cPos).getName()});
                    deleteFile(itemsnp.get(cPos).getName());
                    itemsnp.remove(cPos);
                    refNCF();
                }
                break;
            case MENU_EDIT:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("oldname", tItem.getName());
                intent.putExtra("oldch", tItem.isCh());
                intent.putExtra("title", "Редактирование");
                intent.putExtra("type", "edit");
                intent.putExtra("position", cPos+"");
                startActivityForResult(intent, 1);
                break;
        }
        return true;
    }

    /**
     * Метод, создающий OptionMenu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //@Override
   public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.choose_books) {
            tb.setTitle("Книги");
            bnv.getMenu().getItem(0).setTitle("Непрочитанные");
            bnv.getMenu().getItem(1).setTitle("Прочитанные");
            type = "Книга";
            refCF();
            refNCF();
            return true;
        }
       if (id == R.id.choose_films) {
           tb.setTitle("Фильмы");
           bnv.getMenu().getItem(0).setTitle("Непросмотренные");
           bnv.getMenu().getItem(1).setTitle("Просмотренные");
           type = "Фильм";
           refCF();
           refNCF();
           return true;
       }
       if (id == R.id.choose_games) {
           tb.setTitle("Игры");
           bnv.getMenu().getItem(0).setTitle("Непройденные");
           bnv.getMenu().getItem(1).setTitle("Пройденные");
           type = "Игра";
           refCF();
           refNCF();
           return true;
       }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод, обновляющий данные в списке просмотренных фильмов
     */
    public void refCF()
    {
        String[] namesp = null;
        if(type.equals("Фильм"))
        {
            int k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Фильм")) k++;
            }
            namesp = new String[k];
            k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Фильм")) {
                    namesp[k] = itemsp.get(i).getName();
                    k++;
                }
            }
        }
        if(type.equals("Книга"))
        {
            int k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Книга")) k++;
            }
            namesp = new String[k];
            k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Книга")) {
                    namesp[k] = itemsp.get(i).getName();
                    k++;
                }
            }
        }
        if(type.equals("Игра"))
        {
            int k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Игра")) k++;
            }
            namesp = new String[k];
            k = 0;
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Игра")) {
                    namesp[k] = itemsp.get(i).getName();
                    k++;
                }
            }
        }
        adapterp = new ArrayAdapter<>(this, R.layout.list_item, namesp);
        lvp.setAdapter(adapterp);
    }
    /**
     * Метод, обновляющий данные в списке просмотренных фильмов
     */
    public void refNCF()
    {
        String[] namesnp = null;
        if(type.equals("Фильм"))
        {
            int k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Фильм")) k++;
            }
            namesnp = new String[k];
            k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Фильм")) {
                    namesnp[k] = itemsnp.get(i).getName();
                    k++;
                }
            }
        }
        if(type.equals("Книга"))
        {
            int k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Книга")) k++;
            }
            namesnp = new String[k];
            k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Книга")) {
                    namesnp[k] = itemsnp.get(i).getName();
                    k++;
                }
            }
        }
        if(type.equals("Игра"))
        {
            int k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Игра")) k++;
            }
            namesnp = new String[k];
            k = 0;
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Игра")) {
                    namesnp[k] = itemsnp.get(i).getName();
                    k++;
                }
            }
        }
        adapternp = new ArrayAdapter<>(this, R.layout.list_item, namesnp);
        lvnp.setAdapter(adapternp);
    }

    /**
     * Метод, обрабатывающий значение, возвращенное каким-либо активити
     * */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Этот блок отвечает за обработку значений, возвращенных при создании нового элемента
         */
        if(requestCode == 0) {
            if (resultCode == -2) {
                if (data.getStringExtra("ch").equals("1")) {
                    itemsp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof")));
                    refCF();
                } else {
                    itemsnp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof")));
                    refNCF();
                }
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put("name", data.getStringExtra("name"));
                contentValues.put("kpRate", data.getStringExtra("kpRate"));
                contentValues.put("ch", data.getStringExtra("ch"));
                contentValues.put("myRate", data.getStringExtra("myRate"));
                contentValues.put("type", data.getStringExtra("typeof"));
                dataBase.insert("films", null, contentValues);
            } else Toast.makeText(MainActivity.this, "Не создано", Toast.LENGTH_SHORT).show();
        }

        /**
         * Этот блок отвечает за обработку значений, возвращенных при редактировании или удалении элемента
         */
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                dataBase.delete("films", "name= ?", new String[]{data.getStringExtra("name")});
                deleteFile(data.getStringExtra("name"));
                if(data.getStringExtra("ch").equals("1"))
                {
                    itemsp.remove(Integer.parseInt(data.getStringExtra("position")));
                    refCF();
                }
                else
                {
                    itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                    refNCF();
                }
            }
            if(resultCode == -2)
            {
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put("name", data.getStringExtra("name"));
                contentValues.put("kpRate", data.getStringExtra("kpRate"));
                contentValues.put("ch", data.getStringExtra("ch"));
                contentValues.put("myRate", data.getStringExtra("myRate"));
                contentValues.put("type", data.getStringExtra("type"));
                Log.d("mLogs", data.getStringExtra("oldname"));
                dataBase.update("films", contentValues, "name=?", new String[]{data.getStringExtra("oldname")});
                //deleteFile(data.getStringExtra("oldname"));
                if(data.getStringExtra("ch").equals("1"))
                {
                    if(data.getStringExtra("oldch").equals("1")){
                        itemsp.remove(Integer.parseInt(data.getStringExtra("position")));
                    }
                    else {
                        itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                        refNCF();
                    }
                    itemsp.add(Integer.parseInt(data.getStringExtra("position")),
                            new Item(
                                    data.getStringExtra("name"),
                                    Double.parseDouble(data.getStringExtra("myRate")),
                                    Double.parseDouble(data.getStringExtra("kpRate")),
                                    Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof")));
                    refCF();
                }
                else{
                    if(data.getStringExtra("oldch").equals("1")){
                        itemsp.remove(Integer.parseInt(data.getStringExtra("position")));
                        refCF();
                    }
                    else itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                    itemsnp.add(Integer.parseInt(data.getStringExtra("position")), new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof")));
                    refNCF();
                }
            }
        }
    }
}
