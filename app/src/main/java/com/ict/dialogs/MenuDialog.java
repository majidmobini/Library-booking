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
import android.widget.Button;

import com.ict.classes.Set_font;
import com.ict.librarybooking.R;

public class MenuDialog {
    public interface OnItemClicked {
        void onItemClicked(int row);
    }
    private OnItemClicked listener;
    Context ctx;
    public void ShowMenuDialog(Context ctx,String[] data,OnItemClicked listener)
    {
        this.listener = listener;
        final Dialog dialog =new Dialog(ctx);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.custom_menu,null);
        Set_font.setKodakFont(ctx,layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        Button[] bts = new Button[5];
        bts[0] = layout.findViewById(R.id.bt1);
        bts[1] = layout.findViewById(R.id.bt2);
        bts[2] = layout.findViewById(R.id.bt3);
        bts[3] = layout.findViewById(R.id.bt4);
        bts[4] = layout.findViewById(R.id.bt5);

        for(int i = 0 ; i<data.length;i++)
        {
            bts[i].setVisibility(View.VISIBLE);
            bts[i].setText(data[i]);
            bts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId())
                    {
                        case R.id.bt1:
                            listener.onItemClicked(1);
                            break;
                        case R.id.bt2:
                            listener.onItemClicked(2);
                            break;
                        case R.id.bt3:
                            listener.onItemClicked(3);
                            break;
                        case R.id.bt4:
                            listener.onItemClicked(4);
                            break;
                        case R.id.bt5:
                            listener.onItemClicked(5);
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
}
