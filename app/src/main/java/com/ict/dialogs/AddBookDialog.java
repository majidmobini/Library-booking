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
import android.widget.EditText;
import android.widget.Toast;

import com.ict.classes.BookClass;
import com.ict.classes.Set_font;
import com.ict.librarybooking.DBhelper;
import com.ict.librarybooking.MainActivity;
import com.ict.librarybooking.R;

import androidx.appcompat.app.AlertDialog;

public class AddBookDialog {

    public interface OnBookAdded {
        void onBookAdded(BookClass bk);
    }
    private OnBookAdded listener;
    public void ShowAddBook(final Context ctx, final OnBookAdded listener)
    {
        ShowAddBook(ctx, listener ,null);
    }
    public void ShowAddBook(final Context ctx, final OnBookAdded listener , BookClass bookEdit)
    {
       this.listener = listener;
        final Dialog dialog =new Dialog(ctx);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_book,null);
        Set_font.setKodakFont(ctx,layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        if(bookEdit != null)
        {
            ((EditText)layout.findViewById(R.id.etAddBookName)).setText(bookEdit.name);
            ((EditText)layout.findViewById(R.id.etAddBookWriter)).setText(bookEdit.writer);
            ((EditText)layout.findViewById(R.id.etAddBookPublisher)).setText(bookEdit.publisher);
            ((EditText)layout.findViewById(R.id.etAddBookYear)).setText(bookEdit.year+"");
            ((EditText)layout.findViewById(R.id.etAddBookCount)).setText(bookEdit.count+"");
        }

        layout.findViewById(R.id.btOkAddBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBhelper db = new DBhelper(ctx);
                Log.d("GetBookNumbers",db.GetBookNumbers() + "");
                if (db.GetBookNumbers() > 1000)
                {
                    if (!MainActivity.isFullAccess)
                    {
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
                String name = ((EditText)layout.findViewById(R.id.etAddBookName)).getText().toString();
                String writer = ((EditText)layout.findViewById(R.id.etAddBookWriter)).getText().toString();
                String publisher = ((EditText)layout.findViewById(R.id.etAddBookPublisher)).getText().toString();
                String year = ((EditText)layout.findViewById(R.id.etAddBookYear)).getText().toString();
                String count = ((EditText)layout.findViewById(R.id.etAddBookCount)).getText().toString();

                BookClass bk = new BookClass();
                try {
                    bk.count = Integer.parseInt(count);
                    bk.name = name;
                    bk.publisher = publisher;
                    bk.writer = writer;
                    bk.year =  Integer.parseInt(year);
                    long edit = 1;
                    if (bookEdit != null)
                    {
                        bk.id = bookEdit.id;
                        edit = db.EditBook(bk);
                    }
                    else {
                        bk.id = db.InsertBook(bk);
                    }
                    if (bk.id == -1 || edit == 0)
                    {
                        Toast.makeText(ctx, "خطا در ذخیره سازی", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(ctx, "رکورد اضافه شد", Toast.LENGTH_SHORT).show();
                        if (listener != null)
                            listener.onBookAdded(bk);
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
        layout.findViewById(R.id.btCancelAddBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);

    }
}
