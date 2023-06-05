package com.ict.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ict.classes.MemberClass;
import com.ict.classes.Set_font;
import com.ict.librarybooking.DBhelper;
import com.ict.librarybooking.R;

public class AddUserDialog {
    public interface OnMemberAdded {
        void onMemberAdded(MemberClass mm);
    }
    private OnMemberAdded listener;

    public void ShowMemberDialog(final Context ctx, final OnMemberAdded listener)
    {
        ShowMemberDialog(ctx,listener,null);
    }

    public void ShowMemberDialog(final Context ctx, final OnMemberAdded listener,MemberClass mmEdit)
    {
        this.listener = listener;
        final Dialog dialog =new Dialog(ctx);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_member_dialog,null);
        Set_font.setKodakFont(ctx,layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        if (mmEdit != null)
        {
            ((EditText)layout.findViewById(R.id.etMemberName)).setText(mmEdit.name);
            ((EditText)layout.findViewById(R.id.etMemberPhone)).setText(mmEdit.phoneNo);
        }

        layout.findViewById(R.id.btOkAddMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)layout.findViewById(R.id.etMemberName)).getText().toString();
                String phone = ((EditText)layout.findViewById(R.id.etMemberPhone)).getText().toString();

                DBhelper db = new DBhelper(ctx);
                MemberClass mm = new MemberClass();
                long edit = 1;
                try {
                   mm.name = name;
                   if (name.length() < 1)
                   {
                       Toast t =Toast.makeText(ctx, "مقادیر را درست وارد کنید", Toast.LENGTH_SHORT);
                       t.setGravity(Gravity.CENTER,0,0);
                       t.show();
                       return;
                   }
                   mm.phoneNo = phone;
                   if(mmEdit != null)
                   {
                       mm.id = mmEdit.id;
                       edit = db.EditMember(mm);
                   }
                   else {
                       mm.id = db.InsertMember(mm);
                   }
                   if(mm.id == -1 || edit == 0)
                   {
                       Toast.makeText(ctx, "خطا در ذخیره سازی", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       dialog.dismiss();
                       Toast.makeText(ctx, "رکورد اضافه شد", Toast.LENGTH_SHORT).show();
                       if (listener != null)
                           listener.onMemberAdded(mm);
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
        layout.findViewById(R.id.btCancelAddMember).setOnClickListener(new View.OnClickListener() {
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
