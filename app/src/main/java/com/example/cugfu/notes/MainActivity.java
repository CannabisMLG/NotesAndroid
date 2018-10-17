package com.example.cugfu.notes;

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
    ArrayAdapter<String> adapternp, adapterp;
    private Item tItem;
    private ListView lvnp, lvp;
    private DBhelper dbHelper;
    private int cPos;
    private boolean flag = false;

    private final int MENU_EDIT = 1, MENU_DELETE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        //setSupportActionBar(toolbar);

        final MainActivity th = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem();
            }
        });

        lvnp = (ListView) findViewById(R.id.listNP);
        lvp = (ListView) findViewById(R.id.listP);
        dbHelper = new DBhelper(this);
        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
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
        //dataBase.delete("films", null, null);
        if(cursor.moveToFirst())
        {
            int idName = cursor.getColumnIndex("name");
            int idkpRate = cursor.getColumnIndex("kpRate");
            int idCh = cursor.getColumnIndex("ch");
            int idmyRate = cursor.getColumnIndex("myRate");
            do {
                if(cursor.getString(idCh).equals("1"))
                {
                    itemsp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh)));
                }
                else
                {
                    itemsnp.add(new Item(cursor.getString(idName), Double.parseDouble(cursor.getString(idmyRate)), Double.parseDouble(cursor.getString(idkpRate)),cursor.getInt(idCh)));
                }
            }while(cursor.moveToNext());
            refNP();

            refP();
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
                startActivityForResult(intent, 1);
            }
        });
        registerForContextMenu(lvp);
        registerForContextMenu(lvnp);
    }

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
                    refP();
                }
                else
                {
                    dataBase.delete("films", "name= ?", new String[]{itemsnp.get(cPos).getName()});
                    deleteFile(itemsnp.get(cPos).getName());
                    itemsnp.remove(cPos);
                    refNP();
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if (resultCode == -2) {
                if (data.getStringExtra("ch").equals("1")) {
                    itemsp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch"))));
                    refP();
                } else {
                    itemsnp.add(new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch"))));
                    refNP();
                }
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put("name", data.getStringExtra("name"));
                contentValues.put("kpRate", data.getStringExtra("kpRate"));
                contentValues.put("ch", data.getStringExtra("ch"));
                contentValues.put("myRate", data.getStringExtra("myRate"));
                dataBase.insert("films", null, contentValues);
            } else Toast.makeText(MainActivity.this, "Не создано", Toast.LENGTH_SHORT).show();
        }
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
                    refP();
                }
                else
                {
                    itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                    refNP();
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
                        refNP();
                    }
                    itemsp.add(Integer.parseInt(data.getStringExtra("position")),
                            new Item(
                                    data.getStringExtra("name"),
                                    Double.parseDouble(data.getStringExtra("myRate")),
                                    Double.parseDouble(data.getStringExtra("kpRate")),
                                    Integer.parseInt(data.getStringExtra("ch"))));
                    refP();
                }
                else{
                    if(data.getStringExtra("oldch").equals("1")){
                        itemsp.remove(Integer.parseInt(data.getStringExtra("position")));
                        refP();
                    }
                    else itemsnp.remove(Integer.parseInt(data.getStringExtra("position")));
                    itemsnp.add(Integer.parseInt(data.getStringExtra("position")), new Item(data.getStringExtra("name"), Double.parseDouble(data.getStringExtra("myRate")), Double.parseDouble(data.getStringExtra("kpRate")), Integer.parseInt(data.getStringExtra("ch"))));
                    refNP();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //@Override
   /* public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.lastBM) {
            Log.d("mLogs", "хуй");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    public void editItem()
    {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("title", "Создание");
        intent.putExtra("type", "");
        startActivityForResult(intent, 0);
    }

    public void refP()
    {
        String[] namesp = new String[itemsp.size()];
        for (int i = 0;i<itemsp.size();i++)
        {
            namesp[i] = itemsp.get(i).getName();
        }
        adapterp = new ArrayAdapter<>(this, R.layout.list_item, namesp);
        lvp.setAdapter(adapterp);
    }
    public void refNP()
    {
        String[] namesnp = new String[itemsnp.size()];
        for (int i = 0;i<itemsnp.size();i++)
        {
            namesnp[i] = itemsnp.get(i).getName();
        }
        adapternp = new ArrayAdapter<>(this, R.layout.list_item, namesnp);
        lvnp.setAdapter(adapternp);
    }
}
