package com.ict.librarybooking;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import com.ict.classes.BookClass;
import com.ict.classes.MemberClass;
import com.ict.classes.RentClass;
import com.ict.classes.jajalicalender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBhelper extends SQLiteOpenHelper {

    public static final String BOOK_DB = "BOOK_DB";
    public static final String BOOK_DB_COLUMN_ID = "id";
    public static final String BOOK_DB_COLUMN_NAME = "name";
    public static final String BOOK_DB_COLUMN_YEAR = "year";
    public static final String BOOK_DB_COLUMN_writer = "writer";
    public static final String BOOK_DB_COLUMN_PUBLISHER = "publisher";
    public static final String BOOK_DB_COLUMN_COUNT = "count";

    public static final String MEMBERE_DB_COLUMN_ID = "id";
    public static final String MEMBERE_DB_COLUMN_NAME = "name";
    public static final String MEMBERE_DB_COLUMN_DATE_INT = "dateInt";
    public static final String MEMBERE_DB_COLUMN_DATE_TEXT = "dateText";
    public static final String MEMBERE_DB_COLUMN_PHONE_NO = "phoneNo";

    public static final String RENT_DB_COLUMN_ID = "id";
    public static final String RENT_DB_COLUMN_BOOK_NAME = "bookName";
    public static final String RENT_DB_COLUMN_MEMBER_NAME = "memberName";
    public static final String RENT_DB_COLUMN_DATE_INT = "dateInt";
    public static final String RENT_DB_COLUMN_DATE_TEXT = "dateText";
    public static final String RENT_DB_COLUMN_BOOK_ID = "bookId";
    public static final String RENT_DB_COLUMN_MEMBER_ID = "memberId";
    public static final String RENT_DB_COLUMN_DATE_RETURN_INT = "dateIntReturn";
    public static final String RENT_DB_COLUMN_DATE_RETURN_TEXT = "dateTextReturn";
    public static final String RENT_DB_COLUMN_IS_RETURNED = "isReturned";

    public static final String MEMBERE_DB = "MEMBERE_DB";
    public static final String RENT_DB = "RENT_DB";

    private Context ctx;


    public DBhelper(Context context) {
        super(context, BOOK_DB , null, 2);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ BOOK_DB +
                " (id integer primary key, name text, year integer,writer text, publisher text,count integer)");
        sqLiteDatabase.execSQL("create table "+ MEMBERE_DB +
                " (id integer primary key, name text, dateInt integer,dateText text ,phoneNo text)");
        sqLiteDatabase.execSQL("create table "+ RENT_DB +
                " (id integer primary key, bookName text, memberName text,dateInt integer,dateText text,bookId integer , memberId integer,dateIntReturn integer,dateTextReturn text , isReturned integer DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+BOOK_DB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MEMBERE_DB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+RENT_DB);
        onCreate(sqLiteDatabase);
    }

    public long InsertRented(RentClass rnt )
    {
        return InsertRented(rnt , -1);
    }
    public long EditRent(RentClass rnt)
    {
        return InsertRented(rnt,rnt.id);
    }
    public int DeleteRent(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RENT_DB,
                "id = ? ",
                new String[] { id+"" });
    }
    public long RentReturned(RentClass rnt)
    {
       // Calendar calendar = Calendar.getInstance();
        //Date now = calendar.getTime();
        //SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy/MM/dd");
        //String strDt = simpleDate.format(now);
        jajalicalender jaCalc = new jajalicalender();
        Calendar calendar = Calendar.getInstance();
        calendar.set(jaCalc.getGregorianYear(),jaCalc.getGregorianMonth()-1,jaCalc.getGregorianDay());

        rnt.isReturned = 1;
        rnt.dateTextReturn = jaCalc.getIranianDate();
        rnt.dateIntReturn = calendar.getTimeInMillis();
        return EditRent(rnt);

    }
    public int GetBookRented(String bookId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+RENT_DB+" where "+RENT_DB_COLUMN_IS_RETURNED+"="+0+
                " AND "+RENT_DB_COLUMN_BOOK_ID+"="+bookId+"", null );

        int count = res.getCount();
        if(!res.isClosed())
            res.close();

        return  count;

    }
    public int GetBookNumbers()
    {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
//SELECT
//   SUM(milliseconds)
//FROM
//   tracks;
        Cursor res =  db.rawQuery( "select SUM("+BOOK_DB_COLUMN_COUNT+") FROM "+BOOK_DB+"", null );
        if(res.moveToFirst())
            count = res.getInt(0);

        if (!res.isClosed())
            res.close();
        return count;
    }
    public long InsertRented(RentClass rnt , long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(RENT_DB_COLUMN_BOOK_NAME,rnt.bookClass.name);
        contentValues.put(RENT_DB_COLUMN_MEMBER_NAME,rnt.memberClass.name);
        contentValues.put(RENT_DB_COLUMN_DATE_INT,rnt.dateInt);
        contentValues.put(RENT_DB_COLUMN_DATE_TEXT,rnt.dateText);
        contentValues.put(RENT_DB_COLUMN_BOOK_ID,rnt.bookClass.id);
        contentValues.put(RENT_DB_COLUMN_MEMBER_ID,rnt.memberClass.id);
        contentValues.put(RENT_DB_COLUMN_DATE_RETURN_INT,rnt.dateIntReturn);
        contentValues.put(RENT_DB_COLUMN_DATE_RETURN_TEXT,rnt.dateTextReturn);
        contentValues.put(RENT_DB_COLUMN_IS_RETURNED,rnt.isReturned);
        if(id == -1)
            return db.insert(RENT_DB,null,contentValues);
        else
            return db.update(RENT_DB,contentValues,"id = ?" , new String[]{id+""});

    }
    public long InsertBook(BookClass bk )
    {
       return InsertBook(bk , -1 );
    }
    public long InsertBook(BookClass bk , long id )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_DB_COLUMN_writer,bk.writer);
        contentValues.put(BOOK_DB_COLUMN_COUNT,bk.count);
        contentValues.put(BOOK_DB_COLUMN_NAME,bk.name);
        contentValues.put(BOOK_DB_COLUMN_PUBLISHER,bk.publisher);
        contentValues.put(BOOK_DB_COLUMN_YEAR,bk.year);
        if (id == -1) {
            return db.insert(BOOK_DB, null, contentValues);
        }
        else
        {
            return db.update(BOOK_DB,contentValues,"id = ?",new String[]{id+""});
        }
    }

    public long EditBook(BookClass bk)
    {
        return InsertBook( bk ,bk.id );
    }

    public Integer DeleteBook (Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BOOK_DB,
                "id = ? ",
                new String[] { id+"" });
    }
    public long InsertMember(MemberClass mm )
    {
        return InsertMember(mm ,-1);
    }
    public long EditMember (MemberClass mm)
    {
        return InsertMember( mm ,mm.id);
    }
    public int DeleteMember(Long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MEMBERE_DB,
                "id = ? ",
                new String[] { id+"" });
    }
    public long InsertMember(MemberClass mm , long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MEMBERE_DB_COLUMN_NAME,mm.name);
        contentValues.put(MEMBERE_DB_COLUMN_PHONE_NO,mm.phoneNo);
        contentValues.put(MEMBERE_DB_COLUMN_DATE_INT,System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy/MM/dd");
        String strDt = simpleDate.format(now);
        contentValues.put(MEMBERE_DB_COLUMN_DATE_TEXT, strDt);
        if(id == -1)
             return db.insert(MEMBERE_DB,null,contentValues);
        else
            return db.update(MEMBERE_DB,contentValues,"id = ?",new String[] {id+""});
    }


    public int GetAllRentsCount()
    {
        int a1 = GetRents("" ,0).size();

        return GetRents("" ,1).size() + a1;
    }

    public ArrayList<RentClass> GetRents(String name)
    {
        return GetRents(name ,0);
    }
    public ArrayList<RentClass> GetRents(String name , int isReturned)
    {
        ArrayList<RentClass> rents =new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+RENT_DB+" where "+RENT_DB_COLUMN_IS_RETURNED+"="+isReturned+"", null );
        if(name.length() > 0)
            res =  db.rawQuery( "select * from "+RENT_DB+" where "+RENT_DB_COLUMN_BOOK_NAME+" like '%"+name+"%' OR "+RENT_DB_COLUMN_MEMBER_NAME+"  like '%"+name+"%'", null );

        if (res.moveToLast())
        {
            do {
                RentClass rent = new RentClass();
                rent.bookClass.name = res.getString(res.getColumnIndex(RENT_DB_COLUMN_BOOK_NAME));
                rent.bookClass.id = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_BOOK_ID));
                rent.memberClass.name = res.getString(res.getColumnIndex(RENT_DB_COLUMN_MEMBER_NAME));
                rent.memberClass.id = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_MEMBER_ID));
                rent.dateText = res.getString(res.getColumnIndex(RENT_DB_COLUMN_DATE_TEXT));
                rent.dateInt = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_DATE_INT));
                rent.memberClass.id = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_MEMBER_ID));
                rent.id = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_ID));
                rent.isReturned = res.getInt(res.getColumnIndex(RENT_DB_COLUMN_IS_RETURNED));
                rent.dateTextReturn =  res.getString(res.getColumnIndex(RENT_DB_COLUMN_DATE_RETURN_TEXT));
                rent.dateIntReturn =  res.getInt(res.getColumnIndex(RENT_DB_COLUMN_DATE_RETURN_INT));
                rents.add(rent);

            }while (res.moveToPrevious());

        }
        if(!res.isClosed())
            res.close();
        return rents;
    }


    public ArrayList<MemberClass> GetMembers(String name)
    {
        ArrayList<MemberClass> members =new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MEMBERE_DB+"", null );
        if(name.length() > 0)
            res =  db.rawQuery( "select * from "+MEMBERE_DB+" where "+MEMBERE_DB_COLUMN_NAME+" like '%"+name+"%'", null );

        if (res.moveToLast())
        {
            do {
                MemberClass mm = new MemberClass();
                mm.name  = res.getString(res.getColumnIndex(MEMBERE_DB_COLUMN_NAME));
                mm.phoneNo  = res.getString(res.getColumnIndex(MEMBERE_DB_COLUMN_PHONE_NO));
                mm.id = res.getInt(res.getColumnIndex(MEMBERE_DB_COLUMN_ID));
                members.add(mm);

            }while (res.moveToPrevious());

        }
        if(!res.isClosed())
            res.close();
        return members;
    }

    public ArrayList<BookClass> GetBooks(String name)
    {
        ArrayList<BookClass> books =new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+BOOK_DB+"", null );
        if(name.length() > 0)
            res =  db.rawQuery( "select * from "+BOOK_DB+" where "+BOOK_DB_COLUMN_NAME+" like '%"+name+"%'", null );
        if (res.moveToLast())
        {
            do {
                BookClass bk = new BookClass();
                bk.name  = res.getString(res.getColumnIndex(BOOK_DB_COLUMN_NAME));
                bk.writer  = res.getString(res.getColumnIndex(BOOK_DB_COLUMN_writer));
                bk.publisher  = res.getString(res.getColumnIndex(BOOK_DB_COLUMN_PUBLISHER));
                bk.year  = res.getInt(res.getColumnIndex(BOOK_DB_COLUMN_YEAR));
                bk.count  = res.getInt(res.getColumnIndex(BOOK_DB_COLUMN_COUNT));
                bk.id = res.getInt(res.getColumnIndex(BOOK_DB_COLUMN_ID));
                books.add(bk);

            }while (res.moveToPrevious());

        }
        if(!res.isClosed())
            res.close();
        return books;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }



    public Uri backup(String outFileName) {

        //database path
        final String inFileName = ctx.getDatabasePath(BOOK_DB).toString();

        try {

            ContentResolver cr =  ctx.getContentResolver();
            File dbFile = new File(inFileName);

            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();


            Toast.makeText(ctx, "Backup Completed"+outFileName, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(ctx, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return  Uri.parse("");
    }

    public void importDB(String inFileName) {

        final String outFileName = ctx.getDatabasePath(BOOK_DB).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(ctx, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(ctx, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
 /*   public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }*/

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
       /* SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        */
        return true;
    }

    public Integer deleteContact (Integer id) {
        /*SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
        */
        return 0;
    }

   /* public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }*/
}
