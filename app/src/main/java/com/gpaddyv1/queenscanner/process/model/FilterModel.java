package com.joshuabutton.queenscanner.process.model;

import java.util.ArrayList;
import java.util.List;

import it.chengdazhi.styleimageview.Styler;

/**
 * Created by Phí Văn Tuấn on 27/11/2018.
 */

public class FilterModel {
    private String title;
    private int mode;

    public FilterModel() {
    }

    public FilterModel(String title, int mode) {
        this.title = title;
        this.mode = mode;
    }

    public String getTitle() {
        return title;
    }

    public int getMode() {
        return mode;
    }

    public List<FilterModel> getFilterModels() {
        List<FilterModel> lst = new ArrayList<>();
        lst.add(new FilterModel("Original",Styler.Mode.NONE));
        lst.add(new FilterModel("Grey Scale",Styler.Mode.GREY_SCALE));
        lst.add(new FilterModel("Invert",Styler.Mode.INVERT));
        lst.add(new FilterModel("RGB to BGR",Styler.Mode.RGB_TO_BGR));
        lst.add(new FilterModel("Sepia",Styler.Mode.SEPIA));
        lst.add(new FilterModel("Black & White",Styler.Mode.BLACK_AND_WHITE));
        lst.add(new FilterModel("BRIGHT",Styler.Mode.BRIGHT));
        lst.add(new FilterModel("Vintage Pinhole",Styler.Mode.VINTAGE_PINHOLE));
        lst.add(new FilterModel("Kodachrome",Styler.Mode.KODACHROME));
        lst.add(new FilterModel("Technicolor",Styler.Mode.TECHNICOLOR));
        lst.add(new FilterModel("Saturation",Styler.Mode.SATURATION));
        return lst;
    }
}
