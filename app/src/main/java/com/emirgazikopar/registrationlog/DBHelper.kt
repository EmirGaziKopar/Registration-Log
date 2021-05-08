package com.emirgazikopar.registrationlog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context): SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION){

    companion object{//Veritabanında yer alan sutunların kod içinde temsil edeceğimiz isimlerini tanımladık
        private val DB_NAME ="UsersDB"
        private val DB_VERSION = 1
        private val TABLE_NAME = "users"
        private val ID ="id"
        private  val username="name"
        private val userlast_name="surname"
        private val age = "age"
        private val city = "city"


    }

    /* Hazır gelen onCreate ve onUpgrade ile veritabanımızın tablolarını oluşturuyoruz veya güncelliyoruz */

    override fun onCreate(db: SQLiteDatabase?) {//Bu fonksiyon ile  programımızın veritabanını ve tablolarını oluştururuz
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME"+"($ID Integer PRIMARY KEY , $username TEXT, $userlast_name,TEXT,$age Integer,$city TEXT)"
        db?.execSQL(CREATE_TABLE)
        onUpgrade(db,1,2)//tablo eklemek
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {//veritabanı upgrade olduğu zaman çağırılır yani kullanıcılar uygulamayı güncellerse bu method çağrılır ve bu method içersinde var olan tablolar silinip tekrar oluşturulur, eğer tablo yoksa oluşturulur
        val DROP_TABLE = "DROP TABLE IF EXIST " + TABLE_NAME   //tablo varsa düşürür
        db?.execSQL(DROP_TABLE)
        onCreate(db)//
    }

    fun kayit_ekle(user:Users):Boolean{ //users nesnemizi parametre olarak verdik doneceği değeri boolean olarak belirttik
        val db = this.writableDatabase //Bu method ile veritabanına değer yazabilecek bir SQLiteDatabase nesnesi oluşturulur
        val values = ContentValues() //Bu sınıf aslında insert ve update methodlarının bir parametresidir. Bu sınıf key/value şeklinde verilerin belirtilmesini sağlar
        values.put(username,user.name)//Burada key tablo sutununu value ise değeri temsil eder. Bu satırda yer alan kod ile username sutununa user.name değerini ekle diyoruz
        values.put(userlast_name,user.surname)//Bu satırda yer alan kod ile userlast_name sutununa user.surname değerini ekle diyoruz
        values.put(age,user.age)//butun sutunlara değerlerini böylelikle ekliyoruz
        values.put(city,user.city)
        val _succes = db.insert(TABLE_NAME,null,values)//Bu kod satırı ile bu verileri veritabanına aktarma işlemini gerçekleştiriyoruz
        db.close()
        Log.v("InsertedId","$_succes")
        return (Integer.parseInt("$_succes") != -1 ) // Eğer ekleme işlemi başarılı olursa bu satıda true değer döndürüyoruz

    }

    fun kayit_guncelle(user:Users):Boolean {//Güncelleme ve silme işlemleri de yukarıda anlatılan anlayışa uygun
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(username,user.name)
        values.put(userlast_name,user.surname)
        values.put(age,user.age)
        values.put(city,user.city)

        val _succes = db.update(TABLE_NAME,values,ID+ "=?",arrayOf(user.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_succes") != -1


    }

    fun kayit_sil(_id : Int): Boolean{//Güncelleme ve silme işlemleri de yukarıda anlatılan anlayışa uygun
        val db = this.writableDatabase
        val _succes = db.delete(TABLE_NAME,ID+"=?",arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_succes") != -1
    }

    fun kayit_getir():String{
        var allUser : String = "";
        val db = readableDatabase //okuma modunda olan SQLiteDatabase nesnesi oluşturulur
        var selectAllQuery ="SELECT * FROM $TABLE_NAME"

        //var kod = "DROP TABLE " //readableDatabase bunu kullandıktan sonra istediğimiz sql sorgusunu yazıl değişkenlere atamak bizim elimizde

        val cursor = db.rawQuery(selectAllQuery,null) //sonra rawQuery yazdığımız SQL cumleciğini çalıştırarak tablodaki tüm verileri Cursor tipindeki cursor değişkenine atarız
        //Cursor çalıştırılan SQL cümleciğinin sonucundan dönen her bir satır cursor nesnesi tarafından temsil edilir

        /*
        Bu sınıf içersinde kullanılabilecek belli başlı methodlar
            startManagingCursor(c) Cursor üzerinde işlemler yapabilmeyi sağlar
            getCount() Veritabanındaki kayıt sayısını döndurur
            moveToFirst() Cursor veritabanındaki ilk kayda girer
            moveToNext() Cursor bulunduğu konumdan bir sonraki kayda geçer
            isAfterLast() Son satırdan bir önceki mi ? sorgulaması yapılır
        */
        if (cursor != null ){
            if (cursor.moveToFirst()) {
                do {
                    var id = cursor.getString(cursor.getColumnIndex(ID))
                    var name = cursor.getString(cursor.getColumnIndex(username))
                    var surname = cursor.getString(cursor.getColumnIndex(userlast_name))
                    var city = cursor.getString(cursor.getColumnIndex(city))
                    var age = cursor.getString(cursor.getColumnIndex(age))

                    allUser = "$allUser\n$id $name $surname $city $age"
                    } while(cursor.moveToNext())


                }
            }
            cursor.close()
            db.close()
        return allUser// kod satırları ile cursor veritabanı kapatılır.En son butun kullanıcılar çağrıldığı yere gönderilir
        }

    }

