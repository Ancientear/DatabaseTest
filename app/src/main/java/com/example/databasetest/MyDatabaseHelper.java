package com.example.databasetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/*SQLiteOpenHelper是一个抽象类
*想要使用的话需要自己创建一个自己的帮助类去继承它
* SQLiteOpenHelper中有两个抽象方法，分别是onCreate()和onUpdate()
* 必须在自己的帮助类里面重写这两个方法
* 然后分别在这两个方法中去实现创建、升级数据库的逻辑
*/
public class MyDatabaseHelper extends SQLiteOpenHelper {

    //表Book，存放书的各种详细数据
    public static final String CREATE_BOOK = "create table Book("
            //使用primary key把id列设置为主键，并用autoincrement表示自增长
            + "id integer primary key autoincrement,"
            + "author text,"
            + "price real,"
            + "pages integer,"
            + "name text)";

    //表Category，用于记录图书的分类
    public static final String CREATE_CATEGORY = "create table Category ("
            + "id integer primary key autoincrement, "
            + "category_name text, "
            + "category_code integer)";

    /*这里需要注意，由于BookStore.db文件已经存在了
    *不管如何在主页点击Create database按钮都不会添加新的表了
    * 通过卸载程序新增一张表也太太极端了
    * 解决方案是运用SQLiteOpenHelper升级功能
    * */



    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase的execSQL()方法执行这条建表语句
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);

        //弹出Toast提示创建成功，保证在数据库创建完成的同时还能成功创建Book表
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }




    /*onUpgrade()对数据库进行升级
     *在onUpgrade（）方法中执行了两条DROP语句
     *如果发现数据库中已经有Book或Category表了
     *就将这两个表删除掉，然后再调用onCreate（）方法重新创建
     * 这里先将已经存在的表删除掉，因为如果在创建表的时候发现这个表已经存在了
     * 就会直接报错
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Book");
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }
}