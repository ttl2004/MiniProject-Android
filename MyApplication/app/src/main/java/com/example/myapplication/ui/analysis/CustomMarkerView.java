package com.example.myapplication.ui.analysis;

import android.content.Context;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.data.dto.AnalysisCategoryDTO;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.Locale;

public class CustomMarkerView extends MarkerView {
    private TextView tvContent;
    private DecimalFormat format = new DecimalFormat("#,### đ");

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tv_marker_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof PieEntry) {
            PieEntry pe = (PieEntry) e;
            if (pe.getData() instanceof AnalysisCategoryDTO) {
                AnalysisCategoryDTO dto = (AnalysisCategoryDTO) pe.getData();
                tvContent.setText(String.format(Locale.getDefault(), "%s\n%s\n%.1f%%",
                        dto.getCategoryName(),
                        format.format(dto.getTotalAmount()),
                        dto.getPercentage()));
            } else {
                tvContent.setText(String.format(Locale.getDefault(), "%s: %.1f%%", pe.getLabel(), pe.getValue()));
            }
        } else {
            tvContent.setText(format.format(e.getY()));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10f);
    }
}
