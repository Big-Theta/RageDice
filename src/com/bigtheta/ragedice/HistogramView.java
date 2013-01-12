package com.bigtheta.ragedice;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HistogramView extends View {
    private final Paint m_paint = new Paint();
    private XYMultipleSeriesDataset m_dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer m_renderer = new XYMultipleSeriesRenderer();
    private BarChart m_histogram;
    private XYSeries m_currentSeries;
    private XYSeriesRenderer m_currentRenderer;
    private GraphicalView m_chartView;

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
        m_renderer.setApplyBackgroundColor(true);
        m_renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        m_renderer.setAxisTitleTextSize(16);
        m_renderer.setChartTitleTextSize(20);
        m_renderer.setLabelsTextSize(15);
        m_renderer.setLegendTextSize(15);
        m_renderer.setMargins(new int[] { 20, 30, 15, 0 });
        m_renderer.setZoomButtonsVisible(true);
        m_renderer.setPointSize(10);
        */
        double[] minValues = new double[] { -24, -19, -10, -1, 7, 12, 15, 14, 9, 1, -11, -16 };
        double[] maxValues = new double[] { 7, 12, 24, 28, 33, 35, 37, 36, 28, 19, 11, 4 };
        
        m_dataset = new XYMultipleSeriesDataset();
        m_renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        m_currentRenderer = renderer;
        m_renderer.addSeriesRenderer(renderer);

        XYSeries series = new XYSeries("Rolls");
        int length = minValues.length;
        for (int k = 0; k < length; k++) {
          series.add(minValues[k], maxValues[k]);
        }
        m_dataset.addSeries(series);
        m_chartView = ChartFactory.getBarChartView(context, m_dataset, m_renderer, BarChart.Type.DEFAULT);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        m_chartView.draw(canvas);
//        m_chartView.repaint();
    }
}
