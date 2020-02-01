package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*MyDatabaseHelper构造函数一般适应的是接收4个参数
        *第一个参数：context，必须有它才能对数据库进行操作
        *第二个参数：数据库名，创建数据库时使用的就是这里指定的名称
        *第三个参数：允许在查询数据是返回一个自定义的Cursor，一般都是传入null
        *第四个参数：当前数据库的版本号，可用于对数据库进行升级操作
        */


        //改变版本号进行升级
        dbHelper = new MyDatabaseHelper(this,"BookStore.db",null,2);
        final Button createDatabase = (Button)findViewById(R.id.create_database);

        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*当第一次点击Create database按钮时
                *就会检测当前程序中并没有BookStore.db这个数据库
                *于是会创建该数据库并调用MyDatabaseHelper中的onCreate()方法
                * 这样Book表就得到了创建
                * 再次点击Create database按钮的时候，会发现此时已经存在BookStore.db数据库
                * 不会再创建一次
                * */
                dbHelper.getWritableDatabase();
                /*getReadableDatabase()和getWritableDatabase()
                 * 这两个方法都可以创建或者打开一个现有的数据库
                 * 并返回一个可对数据库进行读写操作的对象
                 * 不同的是：
                 * 当数据库不可写入的时候（如磁盘空间已满）
                 * getReadableDatabase()方法返回的对象将以只读的方式去打开数据库
                 * getWritableDatabase()方法将出现异常
                 * */
            }
        });


        //增加数据
        Button addData = (Button)findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                //开始组装第一条数据
                //id没有赋值是因为自增长
                values.put("name","The Da Vinci Code");
                values.put("author","Dan Brown");
                values.put("pages",454);
                values.put("price",16.96);
                /*insert专门用来添加数据的，它接收三个参数
                * 第一个参数：表名
                * 第二个参数：用于在未指定添加数据的情况下给某些可为空的列自动赋值为NULL
                * 第二个参数一般用不到，直接传入null即可
                * 第三个参数：一个ContentValues对象，提供了一系列的put()方法重载
                * 用于向ContentValues中添加数据，只需要将表中的每个列名以及相应的待添加数据传入即可
                * */
                db.insert("Book",null,values);
                values.clear();

                //开始组装第二条数据
                values.put("name","The Lost Symbol");
                values.put("author","Dan Brown");
                values.put("pages",510);
                values.put("price",19.95);
                db.insert("Book",null,values);//插入第二条数据

            }
        });

        Button updateData = (Button)findViewById(R.id.update_data);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                //构建了一个ContentValues对象，并且只指定了一组数据
                ContentValues values = new ContentValues();
                //也就是说只想把价格这一列的数据更新为10.99
                values.put("price",10.99);
                /*然后调用SQLiteDatabase的update()方法去执行具体的更新操作
                第三个参数对应的SQL语句的where部分，表示更新所有name等于？的行
                第四个参数提供的一个字符串数组为第三个参数的每个占位符指定相应的内容  */
                db.update("Book",values,"name = ?",new String[]{
                        "The Da Vinci Code"
                });
            }
        });



        Button deleteButton = (Button)findViewById(R.id.delete_data);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //删除页码超过500页的书
                db.delete("Book","pages > ?",new String[]{
                        "500"
                });
            }
        });



        Button queryButton = (Button)findViewById(R.id.query_data);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                /*查询表中所以的数据
                * 参数1：表名
                * 参数2：用于指定去查询哪几列，如果不指定默认所有列
                * 参数3：指定where的约束条件where column = value
                * 参数4：为where中的占位符提供具体的值，不指定默认所有行
                * 参数5：指定需要group by的列 group by column
                * 参数6：对group by后的结果进行进一步的约束 having column = value
                * 参数7：指定查询结果的排序方式
                * */
                //查询之后得到一个cursor对象
                Cursor cursor = db.query("Book",null,null,null,null,null,null);

                //调用moveToFirst()方法将数据的指针移动到第一行的位置
                // 然后进入一个循环之中，遍历查询到的每一行数据。
                if(cursor.moveToFirst()){
                    do{
                        //遍历Cursor对象，取出数据并打印
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        //getColumnIndex获取到某一列在表中对应的位置索引，然后将这个索引传入到相应的取值方法中
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));

                        Log.d("MainActivity","book name is " + name);
                        Log.d("MainActivity","book author is " + author);
                        Log.d("MainActivity","book pages is " + pages);
                        Log.d("MainActivity","book price is " + price);
                    }while (cursor.moveToNext());
                }
               cursor.close();
            }
        });

    }
}
