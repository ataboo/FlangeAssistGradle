package com.atasoft.flangeassist.fragments.callout.table;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atasoft.flangeassist.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ataboo on 4/15/2017.
 */

public class DetailRow {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d - H:mm", Locale.CANADA);

    public enum Type {
        GROUP(R.layout.callout_cell_group),
        HEADER(R.layout.callout_cell_header),
        BODY(R.layout.callout_cell_detail),
        HIRE(R.layout.callout_cell_hire);

        private int cellRes;

        Type(int cellRes) {
            this.cellRes = cellRes;
        }
    }

    public Type type;
    private String title;
    private String detail;
    private Integer iconRes = null;

    public DetailRow(Type type, String title, String detail) {
        this.type = type;
        this.title = title;
        this.detail = detail;
    }

    public DetailRow(Type type, String title, String detail, int iconRes) {
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.iconRes = iconRes;
    }

    public View render(LayoutInflater inflater, View convertView) {
        if(convertView == null || convertView.getTag() != this.type) {
            convertView = inflater.inflate(type.cellRes, null);
            convertView.setTag(this.type);
        }

        ((TextView) convertView.findViewById(R.id.title)).setText(title);
        ((TextView) convertView.findViewById(R.id.detail)).setText(detail);

        if (iconRes != null) {
            ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
            iconView.setImageResource(iconRes);
        }

        return convertView;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public static DetailRow makeFromDate(Type type, String title, Date date) {
        String dateString = DATE_FORMAT.format(date);

        return new DetailRow(type, title, dateString);
    }
}
