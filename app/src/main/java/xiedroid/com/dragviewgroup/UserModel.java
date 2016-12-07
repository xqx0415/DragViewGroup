package xiedroid.com.dragviewgroup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;


import java.io.Serializable;

/**
 * Created by XieQXiong on 2016/10/31.
 */

public class UserModel implements Serializable{
    private String viewName;
    private int viewRole;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public int getViewRole() {
        return viewRole;
    }

    public void setViewRole(int viewRole) {
        this.viewRole = viewRole;
    }

    public void setPicForUser(Context context, TextView textView){
        Drawable drawable = null;
        if (getViewRole() == 1){
            drawable = context.getResources().getDrawable(R.drawable.ic_one);

        }else if(getViewRole() == 2){
            drawable = context.getResources().getDrawable(R.drawable.ic_three);

        }else {
            drawable = context.getResources().getDrawable(R.drawable.ic_two);

        }

        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);

    }
    public void setCurrentDelegate(Context context, TextView textView){
        Drawable drawable = null;
        if (getViewRole() == 1){
            drawable = context.getResources().getDrawable(R.drawable.ic_one_selected);

        }else if(getViewRole() == 2){
            drawable = context.getResources().getDrawable(R.drawable.ic_three_selected);

        }else {
            drawable = context.getResources().getDrawable(R.drawable.ic_two_selected);

        }

        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);

    }
}
