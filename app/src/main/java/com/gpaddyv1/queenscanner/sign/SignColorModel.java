package com.joshuabutton.queenscanner.sign;

import android.content.Context;
import android.graphics.Color;

import com.todobom.queenscanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phí Văn Tuấn on 26/11/2018.
 */

public class SignColorModel {
    private String name;
    private int resColor;
    private Context context;

    public SignColorModel(Context context) {
        this.context=context;
    }

    public SignColorModel(String name, int resColor) {
        this.name = name;
        this.resColor = resColor;
    }

    public String getName() {
        return name;
    }

    public int getResColor() {
        return resColor;
    }

    public List<SignColorModel> getSignColor(){
        List<SignColorModel> lst=new ArrayList<>();
        lst.add(new SignColorModel("Black",Color.BLACK));
        lst.add(new SignColorModel("Red",Color.RED));
        lst.add(new SignColorModel("Blue",Color.BLUE));
        lst.add(new SignColorModel("Green",Color.GREEN));
        lst.add(new SignColorModel("Purple",context.getResources().getColor(R.color.purple)));
        lst.add(new SignColorModel("Yellow", Color.YELLOW));
        return lst;
    }
}
