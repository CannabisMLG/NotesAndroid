package com.example.cugfu.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean flag = false;               //flag, определяющий какой из списков сейчас активен(просмотренных/непросмотренных)
    private BottomNavigationView bnv;
    private String type = "Фильм";              //определяет какой тип элементов активен
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

        /**
         *  Определение BottomNavigationMenu и добавление слушателя
         */
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

        /**
         * Подключение к БД и заполнение списков просмотренных и непросмотренных элементов
         */
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        Cursor cursor = dataBase.query("films", null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            int idName = cursor.getColumnIndex("name");
            int idkpRate = cursor.getColumnIndex("kpRate");
            int idCh = cursor.getColumnIndex("ch");
            int idmyRate = cursor.getColumnIndex("myRate");
            int idType = cursor.getColumnIndex("type");
            int idGenre = cursor.getColumnIndex("genre");
            do {
                if(cursor.getString(idCh).equals("1"))
                {
                    itemsp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh), cursor.getString(idType), cursor.getString(idGenre)));
                }
                else
                {
                    itemsnp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh), cursor.getString(idType), cursor.getString(idGenre)));
                    Log.d("mLog", cursor.getString(idType));
                }
            }while(cursor.moveToNext());
            refNCF();
            refCF();
        }
        cursor.close();

        /**
         * подключение слушателей к ListView
         */
        lvnp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = 0;
                for(int i = 0;i < itemsnp.size(); i++)
                    if(itemsnp.get(i).getName().equals(((TextView)view.findViewById(R.id.it)).getText())) pos = i;
                Intent intent = new Intent(th, ViewItem.class);
                intent.putExtra("name", itemsnp.get(pos).getName());
                intent.putExtra("myRate", itemsnp.get(pos).getMyRate());
                intent.putExtra("kpRate", itemsnp.get(pos).getKpRate());
                intent.putExtra("ch", itemsnp.get(pos).isCh());
                intent.putExtra("position", pos+"");
                intent.putExtra("genre", itemsnp.get(pos).getGenre());
                intent.putExtra("cath", type);
                startActivityForResult(intent, 1);
            }
        });
        lvp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = 0;
                for(int i = 0;i < itemsp.size(); i++)
                    if(itemsp.get(i).getName().equals(((TextView)view.findViewById(R.id.it)).getText())) pos = i;
                Intent intent = new Intent(th, ViewItem.class);
                intent.putExtra("name", itemsp.get(pos).getName());
                intent.putExtra("myRate", itemsp.get(pos).getMyRate());
                intent.putExtra("kpRate", itemsp.get(pos).getKpRate());
                intent.putExtra("ch", itemsp.get(pos).isCh());
                intent.putExtra("position", pos+"");
                intent.putExtra("genre", itemsp.get(pos).getGenre());
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

    /**
     * метод, отвечающий за переключение списков(книги/фильмы/игры)
     * @param item
     * @return
     */
    @Override
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
     * Метод, обновляющий данные в ListView для просмотренных элементов
     */
    public void refCF()
    {
        LinkedList<Item> names = new LinkedList<>();
        if(type.equals("Фильм"))
        {
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Фильм")) names.add(itemsp.get(i));
            }
        }
        if(type.equals("Книга"))
        {
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Книга")) names.add(itemsp.get(i));
            }
        }
        if(type.equals("Игра"))
        {
            for(int i = 0;i < itemsp.size();i++)
            {
                if(itemsp.get(i).getType().equals("Игра")) names.add(itemsp.get(i));
            }
        }
        adapterp = new ItemAdapter(this, names);
        lvp.setAdapter(adapterp);
    }
    /**
     * Метод, обновляющий данные в ListView для непросмотренных элементов
     */
    public void refNCF()
    {
        LinkedList<Item> names = new LinkedList<>();
        if(type.equals("Фильм"))
        {
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Фильм")) names.add(itemsnp.get(i));
            }
        }
        if(type.equals("Книга"))
        {
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Книга")) names.add(itemsnp.get(i));
            }
        }
        if(type.equals("Игра"))
        {
            for(int i = 0;i < itemsnp.size();i++)
            {
                if(itemsnp.get(i).getType().equals("Игра")) names.add(itemsnp.get(i));
            }
        }
        adapternp = new ItemAdapter(this, names);
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
                    itemsp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof"), data.getStringExtra("genre")));
                    refCF();
                } else {
                    itemsnp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof"), data.getStringExtra("genre")));
                    refNCF();
                }
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put("name", data.getStringExtra("name"));
                contentValues.put("kpRate", data.getStringExtra("kpRate"));
                contentValues.put("ch", data.getStringExtra("ch"));
                contentValues.put("myRate", data.getStringExtra("myRate"));
                contentValues.put("type", data.getStringExtra("typeof"));
                contentValues.put("genre", data.getStringExtra("genre"));
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
                contentValues.put("type", data.getStringExtra("typeof"));
                contentValues.put("genre", data.getStringExtra("genre"));
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
                    itemsp.add(
                            new Item(
                                    data.getStringExtra("name"),
                                    Double.parseDouble(data.getStringExtra("myRate")),
                                    Double.parseDouble(data.getStringExtra("kpRate")),
                                    Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof"),
                                    data.getStringExtra("genre")));
                    refCF();
                }
                else{
                    if(data.getStringExtra("oldch").equals("1")){
                        itemsp.remove(Integer.parseInt(data.getStringExtra("position")));
                        refCF();
                    }
                    else itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                    itemsnp.add(Integer.parseInt(data.getStringExtra("position")), new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch")), data.getStringExtra("typeof"), data.getStringExtra("genre")));
                    refNCF();
                }
            }
        }
    }
}
