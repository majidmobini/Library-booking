package com.ict.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ict.classes.BookClass;
import com.ict.classes.MemberClass;
import com.ict.classes.RentClass;
import com.ict.classes.Set_font;
import com.ict.classes.jajalicalender;
import com.ict.librarybooking.DBhelper;
import com.ict.librarybooking.MainActivity;
import com.ict.librarybooking.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatEditText;

public class AddRentDialog {
    public interface OnRentAdded {
        void onRentAdded(RentClass mm);
    }
    private OnRentAdded listener;
    BookClass selectedBook;
    MemberClass selectedMember;
    public void ShowRentDialog(final Context ctx, final OnRentAdded listener )
    {
        ShowRentDialog( ctx,  listener , null);
    }
    public void ShowRentDialog(final Context ctx, final OnRentAdded listener , RentClass rnt)
    {

        this.listener = listener;
        final Dialog dialog =new Dialog(ctx);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_rented_book_dialog,null);
        Set_font.setKodakFont(ctx,layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);

      /*  Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy/MM/dd");
        String strDt = simpleDate.format(now);*/
        final AppCompatAutoCompleteTextView autoTvBookName = layout.findViewById(R.id.etRentBookName);
        final AppCompatAutoCompleteTextView autoTvMember = layout.findViewById(R.id.etRentMemberName);
        final AppCompatEditText etTime = layout.findViewById(R.id.etRentDate);

        jajalicalender jacalc = new jajalicalender();
        etTime.setText(jacalc.getIranianDate());

        if (rnt != null)
        {
            etTime.setText(rnt.dateText);
            autoTvBookName.setText(rnt.bookClass.name);
            autoTvMember.setText(rnt.memberClass.name);
        }

        DBhelper db = new DBhelper(ctx);
        final ArrayList<BookClass> books = db.GetBooks("");
        final ArrayList<MemberClass> members = db.GetMembers("");


        final String[] bookString = new String[books.size()];
        for (int i=0;i<books.size();i++)
        {
            bookString[i] = books.get(i).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item, bookString);
        autoTvBookName.setThreshold(1); //will start working from first character
        autoTvBookName.setAdapter(adapter);




        String[] memberString = new String[members.size()];
        for (int i=0;i<members.size();i++)
        {
            memberString[i] = members.get(i).name;
        }
        ArrayAdapter<String> adapterMember = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item, memberString);
        autoTvMember.setThreshold(1); //will start working from first character
        autoTvMember.setAdapter(adapterMember);


        layout.findViewById(R.id.btOkrent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    DBhelper db = new DBhelper(ctx);
                    Log.d("GetAllRentsCount",db.GetAllRentsCount() + "");
                    if (db.GetAllRentsCount() > 50)
                    {
                        if (!MainActivity.isFullAccess) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setTitle("اتمام استفاده رایگان. جهت استفاده از امکانات برنامه لطفا هزینه آنرا پرداخت کنید.");


                            builder.setPositiveButton("پرداخت", (dialog, which) -> {
                                ctx.startActivity(new Intent(ctx, BuyDialog.class));
                            });
                            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

                            builder.show();
                            return;
                        }
                    }

                    selectedMember = null;
                    selectedBook = null;
                    String bookname = autoTvBookName.getText().toString();
                    String memberName = autoTvMember.getText().toString();
                    Iterator<BookClass> iterator = books.iterator();
                    while (iterator.hasNext()) {
                        BookClass book = iterator.next();
                        if (book.name.equals(bookname)) {
                           selectedBook = book;
                           break;
                        }
                    }
                    Iterator<MemberClass> iterator2 = members.iterator();
                    while (iterator2.hasNext()) {
                        MemberClass member = iterator2.next();
                        if (member.name.equals(memberName)) {
                            selectedMember = member;
                            break;
                        }
                    }


                    if(selectedBook == null || selectedMember == null) {
                        Toast t =Toast.makeText(ctx, "مقادیر را درست وارد کنید", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        return;
                    }

                    if (!selectedMember.name.equals(memberName) || !selectedBook.name.equals(bookname) )
                    {
                        Toast t =Toast.makeText(ctx, "مقادیر را درست وارد کنید", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        return;
                    }
                    String dateStr = etTime.getText().toString();
                    String[] elements = dateStr.split("/");
                    long dateMili = 0;
                    try
                    {
                        jajalicalender jaCalc = new jajalicalender(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]), Integer.parseInt(elements[2]));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(jaCalc.getGregorianYear(),jaCalc.getGregorianMonth()-1,jaCalc.getGregorianDay());
                        dateMili = calendar.getTimeInMillis();

                    }
                   catch (Exception ex)
                   {
                       Toast t =Toast.makeText(ctx, "مقادیر را درست وارد کنید", Toast.LENGTH_SHORT);
                       t.setGravity(Gravity.CENTER,0,0);
                       t.show();
                       return;
                   }

                    RentClass rent;
                    long edit = 1;
                    if(rnt != null)
                    {
                        rent = rnt;
                        rent.bookClass = selectedBook;
                        rent.memberClass = selectedMember;
                        rent.dateText = dateStr;
                        rent.dateInt = dateMili;
                        edit = db.EditRent(rent);
                    }
                    else
                    {
                        rent = new RentClass();
                        rent.bookClass = selectedBook;
                        rent.memberClass = selectedMember;
                        rent.dateInt = dateMili;
                        rent.dateText = dateStr;
                        rent.id = db.InsertRented(rent);
                    }
                    if(rent.id == -1 || edit == 0)
                    {
                        Toast.makeText(ctx, "خطا در ذخیره سازی", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(ctx, "رکورد اضافه شد", Toast.LENGTH_SHORT).show();
                        if (listener != null) {

                            listener.onRentAdded(rent);
                        }
                    }
                }
                catch (Exception ex)
                {
                    Toast t =Toast.makeText(ctx, "مقادیر را درست وارد کنید", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER,0,0);
                    t.show();
                }
            }
        });
        layout.findViewById(R.id.btCancelRent).setOnClickListener(view -> dialog.dismiss());

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);

    }
}
