package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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

public class HistogramPlayerTimeView extends View {
    private XYMultipleSeriesDataset m_dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer m_renderer = new XYMultipleSeriesRenderer();
    private GraphicalView m_chartView;

    public HistogramPlayerTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_dataset = new XYMultipleSeriesDataset();
        m_renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(getResources().getColor(R.color.histogram_bar));
        m_renderer.addSeriesRenderer(renderer);
        m_renderer.setBarSpacing(0.1);
        m_renderer.setYAxisMin(0.0);
        m_renderer.setBackgroundColor(getResources().getColor(R.color.histogram_background));
        m_renderer.setApplyBackgroundColor(true);
        m_renderer.setLabelsTextSize(40);
        m_renderer.setLegendTextSize(40);
        // Order is top, left, bottom, right
        int[] margins = {60, 80, 80, 20};
        m_renderer.setMargins(margins);
        m_renderer.setFitLegend(true);
        m_renderer.setXLabelsColor(getResources().getColor(R.color.histogram_labels));
        m_renderer.setYLabelsColor(0, getResources().getColor(R.color.histogram_labels));
        m_renderer.setYLabelsAlign(Paint.Align.RIGHT);
        updateDataset();
        m_chartView = ChartFactory.getBarChartView(context, m_dataset, m_renderer, BarChart.Type.DEFAULT);
    }

    public HistogramPlayerTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void updateDataset() {
        XYSeries series = new XYSeries("Average time (seconds)");
        for (int i = 0; i < m_dataset.getSeriesCount(); i++) {
            m_dataset.removeSeries(0);
        }

        Game game = MainActivity.getGame();
        if (game != null) {
            HashMap<Long, Long> times = DiceRoll.getAverageTimes(game.getId());
            m_renderer.setXLabels(0);
            double space = 1.0;
            Long min = null;
            Long max = null;
            ArrayList<Long> playerIds = new ArrayList<Long>(times.keySet());
            Collections.sort(playerIds);
            for (Long playerId : playerIds) {
                Long milis = times.get(playerId);
                if (milis == null) {
                    series.add((double)playerId, 0.0);
                } else {
                    series.add((double)playerId, (double)(milis / 1000));
                }
                if (min == null || playerId < min) {
                    min = playerId;
                }
                if (max == null || playerId > max) {
                    max = playerId;
                }
                m_renderer.addXTextLabel(space, Player.retrieve(playerId).getPlayerName());
                Log.e("label", Player.retrieve(playerId).getPlayerName());
                space += 1.0;
            }
            if (min != null && max != null) {
                m_renderer.setXAxisMin(min - 0.5);
                m_renderer.setXAxisMax(max + 0.5);
            }
        }
        m_dataset.addSeries(series);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateDataset();
        m_chartView.draw(canvas);
    }
}
