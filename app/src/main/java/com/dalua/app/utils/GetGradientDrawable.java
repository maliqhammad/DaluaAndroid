package com.dalua.app.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.dalua.app.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GetGradientDrawable {


    public static int[] getGradientDrawable(Context context) {

        return new int[]{

                ContextCompat.getColor(context, R.color.gradient_color_1),
                ContextCompat.getColor(context, R.color.gradient_color_2),
                ContextCompat.getColor(context, R.color.gradient_color_3),
                ContextCompat.getColor(context, R.color.gradient_color_4),
                ContextCompat.getColor(context, R.color.gradient_color_5),
                ContextCompat.getColor(context, R.color.gradient_color_6),
                ContextCompat.getColor(context, R.color.gradient_color_7),
                ContextCompat.getColor(context, R.color.gradient_color_8),
                ContextCompat.getColor(context, R.color.gradient_color_9),
                ContextCompat.getColor(context, R.color.gradient_color_10),
                ContextCompat.getColor(context, R.color.gradient_color_11),
                ContextCompat.getColor(context, R.color.gradient_color_12),
                ContextCompat.getColor(context, R.color.gradient_color_13),
                ContextCompat.getColor(context, R.color.gradient_color_14),
                ContextCompat.getColor(context, R.color.gradient_color_15),
                ContextCompat.getColor(context, R.color.gradient_color_16),
                ContextCompat.getColor(context, R.color.gradient_color_17),
                ContextCompat.getColor(context, R.color.gradient_color_18),
                ContextCompat.getColor(context, R.color.gradient_color_19),
                ContextCompat.getColor(context, R.color.gradient_color_20),
                ContextCompat.getColor(context, R.color.gradient_color_21),
                ContextCompat.getColor(context, R.color.gradient_color_22),
                ContextCompat.getColor(context, R.color.gradient_color_23),
                ContextCompat.getColor(context, R.color.gradient_color_24),
                ContextCompat.getColor(context, R.color.gradient_color_25),
                ContextCompat.getColor(context, R.color.gradient_color_26),
                ContextCompat.getColor(context, R.color.gradient_color_27),
                ContextCompat.getColor(context, R.color.gradient_color_28),
                ContextCompat.getColor(context, R.color.gradient_color_29),
                ContextCompat.getColor(context, R.color.gradient_color_30),
                ContextCompat.getColor(context, R.color.gradient_color_31),
                ContextCompat.getColor(context, R.color.gradient_color_32),
                ContextCompat.getColor(context, R.color.gradient_color_33),
                ContextCompat.getColor(context, R.color.gradient_color_34),
                ContextCompat.getColor(context, R.color.gradient_color_35),
                ContextCompat.getColor(context, R.color.gradient_color_36),
                ContextCompat.getColor(context, R.color.gradient_color_37),
                ContextCompat.getColor(context, R.color.gradient_color_38),
                ContextCompat.getColor(context, R.color.gradient_color_39),
                ContextCompat.getColor(context, R.color.gradient_color_40),
                ContextCompat.getColor(context, R.color.gradient_color_41),
                ContextCompat.getColor(context, R.color.gradient_color_42),
                ContextCompat.getColor(context, R.color.gradient_color_43),
                ContextCompat.getColor(context, R.color.gradient_color_44),
                ContextCompat.getColor(context, R.color.gradient_color_45),
                ContextCompat.getColor(context, R.color.gradient_color_46),
                ContextCompat.getColor(context, R.color.gradient_color_47),
                ContextCompat.getColor(context, R.color.gradient_color_48),
                ContextCompat.getColor(context, R.color.gradient_color_49),
                ContextCompat.getColor(context, R.color.gradient_color_50),
                ContextCompat.getColor(context, R.color.gradient_color_51),
                ContextCompat.getColor(context, R.color.gradient_color_52),
                ContextCompat.getColor(context, R.color.gradient_color_53),
                ContextCompat.getColor(context, R.color.gradient_color_54),
                ContextCompat.getColor(context, R.color.gradient_color_55),
                ContextCompat.getColor(context, R.color.gradient_color_56),
                ContextCompat.getColor(context, R.color.gradient_color_57),
                ContextCompat.getColor(context, R.color.gradient_color_58),
                ContextCompat.getColor(context, R.color.gradient_color_59),
                ContextCompat.getColor(context, R.color.gradient_color_60),
                ContextCompat.getColor(context, R.color.gradient_color_61),
                ContextCompat.getColor(context, R.color.gradient_color_62),
                ContextCompat.getColor(context, R.color.gradient_color_63),
                ContextCompat.getColor(context, R.color.gradient_color_64),
                ContextCompat.getColor(context, R.color.gradient_color_65),
                ContextCompat.getColor(context, R.color.gradient_color_66),
                ContextCompat.getColor(context, R.color.gradient_color_67),
                ContextCompat.getColor(context, R.color.gradient_color_68),
                ContextCompat.getColor(context, R.color.gradient_color_69),
                ContextCompat.getColor(context, R.color.gradient_color_70),
                ContextCompat.getColor(context, R.color.gradient_color_71),
                ContextCompat.getColor(context, R.color.gradient_color_72),
                ContextCompat.getColor(context, R.color.gradient_color_73),
                ContextCompat.getColor(context, R.color.gradient_color_74),
                ContextCompat.getColor(context, R.color.gradient_color_75),
                ContextCompat.getColor(context, R.color.gradient_color_76),
                ContextCompat.getColor(context, R.color.gradient_color_77),
                ContextCompat.getColor(context, R.color.gradient_color_79),
                ContextCompat.getColor(context, R.color.gradient_color_80),
                ContextCompat.getColor(context, R.color.gradient_color_81),
                ContextCompat.getColor(context, R.color.gradient_color_82),
                ContextCompat.getColor(context, R.color.gradient_color_83),
                ContextCompat.getColor(context, R.color.gradient_color_84),
                ContextCompat.getColor(context, R.color.gradient_color_85),
                ContextCompat.getColor(context, R.color.gradient_color_86),
                ContextCompat.getColor(context, R.color.gradient_color_87),
                ContextCompat.getColor(context, R.color.gradient_color_88),
                ContextCompat.getColor(context, R.color.gradient_color_89),
                ContextCompat.getColor(context, R.color.gradient_color_90),
                ContextCompat.getColor(context, R.color.gradient_color_91),
                ContextCompat.getColor(context, R.color.gradient_color_92),
                ContextCompat.getColor(context, R.color.gradient_color_93),
                ContextCompat.getColor(context, R.color.gradient_color_94),
                ContextCompat.getColor(context, R.color.gradient_color_95),
                ContextCompat.getColor(context, R.color.gradient_color_97),
                ContextCompat.getColor(context, R.color.gradient_color_98),
                ContextCompat.getColor(context, R.color.gradient_color_99),
                ContextCompat.getColor(context, R.color.gradient_color_100),
                ContextCompat.getColor(context, R.color.gradient_color_101),
                ContextCompat.getColor(context, R.color.gradient_color_102),
                ContextCompat.getColor(context, R.color.gradient_color_103),
                ContextCompat.getColor(context, R.color.gradient_color_104),
                ContextCompat.getColor(context, R.color.gradient_color_105),
                ContextCompat.getColor(context, R.color.gradient_color_106),
                ContextCompat.getColor(context, R.color.gradient_color_107),
                ContextCompat.getColor(context, R.color.gradient_color_108),
                ContextCompat.getColor(context, R.color.gradient_color_109),
                ContextCompat.getColor(context, R.color.gradient_color_110),
                ContextCompat.getColor(context, R.color.gradient_color_111),
                ContextCompat.getColor(context, R.color.gradient_color_112),
                ContextCompat.getColor(context, R.color.gradient_color_113),
                ContextCompat.getColor(context, R.color.gradient_color_114),
                ContextCompat.getColor(context, R.color.gradient_color_115),
                ContextCompat.getColor(context, R.color.gradient_color_116),
                ContextCompat.getColor(context, R.color.gradient_color_117),
                ContextCompat.getColor(context, R.color.gradient_color_118),
                ContextCompat.getColor(context, R.color.gradient_color_119),
                ContextCompat.getColor(context, R.color.gradient_color_120),
                ContextCompat.getColor(context, R.color.gradient_color_121),
                ContextCompat.getColor(context, R.color.gradient_color_122),
                ContextCompat.getColor(context, R.color.gradient_color_123),
                ContextCompat.getColor(context, R.color.gradient_color_124),
                ContextCompat.getColor(context, R.color.gradient_color_125),
                ContextCompat.getColor(context, R.color.gradient_color_126),
                ContextCompat.getColor(context, R.color.gradient_color_127),
                ContextCompat.getColor(context, R.color.gradient_color_128),
                ContextCompat.getColor(context, R.color.gradient_color_129),
                ContextCompat.getColor(context, R.color.gradient_color_130),
                ContextCompat.getColor(context, R.color.gradient_color_131),
                ContextCompat.getColor(context, R.color.gradient_color_132),
                ContextCompat.getColor(context, R.color.gradient_color_133),
                ContextCompat.getColor(context, R.color.gradient_color_134),
                ContextCompat.getColor(context, R.color.gradient_color_135),
                ContextCompat.getColor(context, R.color.gradient_color_136),
                ContextCompat.getColor(context, R.color.gradient_color_137),
                ContextCompat.getColor(context, R.color.gradient_color_138),
                ContextCompat.getColor(context, R.color.gradient_color_139),
                ContextCompat.getColor(context, R.color.gradient_color_140),
                ContextCompat.getColor(context, R.color.gradient_color_141),
                ContextCompat.getColor(context, R.color.gradient_color_142),
                ContextCompat.getColor(context, R.color.gradient_color_143),
                ContextCompat.getColor(context, R.color.gradient_color_144),
                ContextCompat.getColor(context, R.color.gradient_color_145),
                ContextCompat.getColor(context, R.color.gradient_color_146),
                ContextCompat.getColor(context, R.color.gradient_color_147),
                ContextCompat.getColor(context, R.color.gradient_color_148),
                ContextCompat.getColor(context, R.color.gradient_color_149),
                ContextCompat.getColor(context, R.color.gradient_color_150),
                ContextCompat.getColor(context, R.color.gradient_color_151),
                ContextCompat.getColor(context, R.color.gradient_color_152),
                ContextCompat.getColor(context, R.color.gradient_color_153),
                ContextCompat.getColor(context, R.color.gradient_color_154),
                ContextCompat.getColor(context, R.color.gradient_color_156),
                ContextCompat.getColor(context, R.color.gradient_color_157),
                ContextCompat.getColor(context, R.color.gradient_color_158),
                ContextCompat.getColor(context, R.color.gradient_color_159),
                ContextCompat.getColor(context, R.color.gradient_color_160),
                ContextCompat.getColor(context, R.color.gradient_color_161),
                ContextCompat.getColor(context, R.color.gradient_color_162),
                ContextCompat.getColor(context, R.color.gradient_color_163),
                ContextCompat.getColor(context, R.color.gradient_color_154),
                ContextCompat.getColor(context, R.color.gradient_color_165),
                ContextCompat.getColor(context, R.color.gradient_color_166),
                ContextCompat.getColor(context, R.color.gradient_color_167),
                ContextCompat.getColor(context, R.color.gradient_color_168),
                ContextCompat.getColor(context, R.color.gradient_color_169),
                ContextCompat.getColor(context, R.color.gradient_color_170),
                ContextCompat.getColor(context, R.color.gradient_color_171),
                ContextCompat.getColor(context, R.color.gradient_color_172),
                ContextCompat.getColor(context, R.color.gradient_color_173),
                ContextCompat.getColor(context, R.color.gradient_color_174),
                ContextCompat.getColor(context, R.color.gradient_color_175),
                ContextCompat.getColor(context, R.color.gradient_color_176),
                ContextCompat.getColor(context, R.color.gradient_color_177),
                ContextCompat.getColor(context, R.color.gradient_color_178),
                ContextCompat.getColor(context, R.color.gradient_color_179),
                ContextCompat.getColor(context, R.color.gradient_color_180),
                ContextCompat.getColor(context, R.color.gradient_color_181),
                ContextCompat.getColor(context, R.color.gradient_color_182),
                ContextCompat.getColor(context, R.color.gradient_color_183),
                ContextCompat.getColor(context, R.color.gradient_color_184),
                ContextCompat.getColor(context, R.color.gradient_color_185),
                ContextCompat.getColor(context, R.color.gradient_color_186),
                ContextCompat.getColor(context, R.color.gradient_color_187),
                ContextCompat.getColor(context, R.color.gradient_color_188),
                ContextCompat.getColor(context, R.color.gradient_color_189),
                ContextCompat.getColor(context, R.color.gradient_color_190),
                ContextCompat.getColor(context, R.color.gradient_color_191),
                ContextCompat.getColor(context, R.color.gradient_color_192),
                ContextCompat.getColor(context, R.color.gradient_color_193),
                ContextCompat.getColor(context, R.color.gradient_color_194),
                ContextCompat.getColor(context, R.color.gradient_color_195),
                ContextCompat.getColor(context, R.color.gradient_color_196),
                ContextCompat.getColor(context, R.color.gradient_color_197),
                ContextCompat.getColor(context, R.color.gradient_color_198),
                ContextCompat.getColor(context, R.color.gradient_color_199),
                ContextCompat.getColor(context, R.color.gradient_color_200),
                ContextCompat.getColor(context, R.color.gradient_color_201),
                ContextCompat.getColor(context, R.color.gradient_color_202),
        };
    }

    public static HashMap<Integer, String> isContainList() {
        HashMap<Integer, String> xAxisValues = new HashMap<>();
        xAxisValues.put(0, "380");
        xAxisValues.put(20, "391");
        xAxisValues.put(40, "419");
        xAxisValues.put(60, "438");
        xAxisValues.put(80, "458");
        xAxisValues.put(100, "477");
        xAxisValues.put(120, "497");
        xAxisValues.put(140, "416");
        xAxisValues.put(160, "536");
        xAxisValues.put(180, "555");
        xAxisValues.put(200, "575");
        xAxisValues.put(220, "594");
        xAxisValues.put(240, "614");
        xAxisValues.put(260, "623");
        xAxisValues.put(280, "653");
        xAxisValues.put(300, "672");
        xAxisValues.put(320, "692");
        xAxisValues.put(340, "711");
        xAxisValues.put(360, "731");
        xAxisValues.put(380, "751");
        xAxisValues.put(400, "770");
        return xAxisValues;
    }


    public static ArrayList<Entry> getLineDataSet(String waterType, Double aSliderValue, Double bSliderValue, Double cSliderValue) {

        if (waterType.toLowerCase().equals("marine")) {
            ArrayList<Double> aChannelList = new ArrayList<>();
            ArrayList<Double> bChannelList = new ArrayList<>();
            ArrayList<Double> cChannelList = new ArrayList<>();

            for (Double a : channelA_marine) {
                aChannelList.add(((aSliderValue / 100) * a) * 0.15);
            }

            for (Double b : channelB_marine) {
                bChannelList.add(((bSliderValue / 100) * b) * 0.45);
//                bChannelList.add(((bSliderValue / 100) * b) * 0.15);
            }

            for (Double c : channelC_marine) {
                cChannelList.add(((cSliderValue / 100) * c) * 0.4);
//                cChannelList.add(((cSliderValue / 100) * c) * 0.15);
            }

            ArrayList<Double> sumValues = new ArrayList<>();
            ArrayList<Integer> labels = new ArrayList<>();
            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<Entry> valuesFinal = new ArrayList<>();

            for (int i = 0; i < channelA_marine.length; i++) {
                sumValues.add(aChannelList.get(i) + bChannelList.get(i) + cChannelList.get(i));
                labels.add((380) + i);
            }

            Integer k = channelA_marine.length;
            for (int i = 0; i < channelA_marine.length; i++) {
                values.add(new Entry(i, sumValues.get(i).floatValue()));
                //            values.add(new Entry(labels.get(i), sumValues.get(i).floatValue()));
            }

            Double maxValue = Collections.max(sumValues);

            for (int i = 0; i < sumValues.size(); i++) {
                valuesFinal.add(new Entry(i, Float.parseFloat((sumValues.get(i) / maxValue) + "")));
            }
            return valuesFinal;
        } else {
            ArrayList<Double> aChannelList = new ArrayList<>();
            ArrayList<Double> bChannelList = new ArrayList<>();
            ArrayList<Double> cChannelList = new ArrayList<>();

            for (Double a : channelA_fresh) {
                aChannelList.add(((aSliderValue / 100) * a) * 0.15);
            }

            for (Double b : channelB_fresh) {
                bChannelList.add(((bSliderValue / 100) * b) * 0.45);
//                bChannelList.add(((bSliderValue / 100) * b) * 0.15);
            }

            for (Double c : channelC_fresh) {
                cChannelList.add(((cSliderValue / 100) * c) * 0.4);
//                cChannelList.add(((cSliderValue / 100) * c) * 0.15);
            }

            ArrayList<Double> sumValues = new ArrayList<>();
            ArrayList<Integer> labels = new ArrayList<>();
            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<Entry> valuesFinal = new ArrayList<>();

            for (int i = 0; i < channelA_fresh.length; i++) {
                sumValues.add(aChannelList.get(i) + bChannelList.get(i) + cChannelList.get(i));
                labels.add((380) + i);
            }

            Integer k = channelA_fresh.length;
            for (int i = 0; i < channelA_fresh.length; i++) {
                values.add(new Entry(i, sumValues.get(i).floatValue()));
                //            values.add(new Entry(labels.get(i), sumValues.get(i).floatValue()));
            }

            Double maxValue = Collections.max(sumValues);

            for (int i = 0; i < sumValues.size(); i++) {
                valuesFinal.add(new Entry(i, Float.parseFloat((sumValues.get(i) / maxValue) + "")));
            }
            return valuesFinal;
        }

    }


    public static List<GradientColor> getLineDataSetGradientColors(Context context) {

        List<GradientColor> gradientColorsList = new ArrayList<>();
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_1),
                ContextCompat.getColor(context, R.color.gradient_color_2)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_2),
                ContextCompat.getColor(context, R.color.gradient_color_3)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_3),
                ContextCompat.getColor(context, R.color.gradient_color_4)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_4),
                ContextCompat.getColor(context, R.color.gradient_color_5)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_5),
                ContextCompat.getColor(context, R.color.gradient_color_6)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_6),
                ContextCompat.getColor(context, R.color.gradient_color_7)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_8),
                ContextCompat.getColor(context, R.color.gradient_color_9)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_9),
                ContextCompat.getColor(context, R.color.gradient_color_10)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_10),
                ContextCompat.getColor(context, R.color.gradient_color_11)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_11),
                ContextCompat.getColor(context, R.color.gradient_color_12)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_12),
                ContextCompat.getColor(context, R.color.gradient_color_13)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_13),
                ContextCompat.getColor(context, R.color.gradient_color_14)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_14),
                ContextCompat.getColor(context, R.color.gradient_color_15)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_15),
                ContextCompat.getColor(context, R.color.gradient_color_16)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_17),
                ContextCompat.getColor(context, R.color.gradient_color_18)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_19),
                ContextCompat.getColor(context, R.color.gradient_color_20)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_20),
                ContextCompat.getColor(context, R.color.gradient_color_21)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_21),
                ContextCompat.getColor(context, R.color.gradient_color_22)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_22),
                ContextCompat.getColor(context, R.color.gradient_color_23)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_23),
                ContextCompat.getColor(context, R.color.gradient_color_24)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_24),
                ContextCompat.getColor(context, R.color.gradient_color_25)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_25),
                ContextCompat.getColor(context, R.color.gradient_color_26)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_26),
                ContextCompat.getColor(context, R.color.gradient_color_27)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_27),
                ContextCompat.getColor(context, R.color.gradient_color_28)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_28),
                ContextCompat.getColor(context, R.color.gradient_color_29)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_29),
                ContextCompat.getColor(context, R.color.gradient_color_30)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_30),
                ContextCompat.getColor(context, R.color.gradient_color_31)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_31),
                ContextCompat.getColor(context, R.color.gradient_color_32)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_32),
                ContextCompat.getColor(context, R.color.gradient_color_33)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_33),
                ContextCompat.getColor(context, R.color.gradient_color_34)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_34),
                ContextCompat.getColor(context, R.color.gradient_color_35)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_35),
                ContextCompat.getColor(context, R.color.gradient_color_36)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_36),
                ContextCompat.getColor(context, R.color.gradient_color_37)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_37),
                ContextCompat.getColor(context, R.color.gradient_color_38)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_38),
                ContextCompat.getColor(context, R.color.gradient_color_39)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_39),
                ContextCompat.getColor(context, R.color.gradient_color_40)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_40),
                ContextCompat.getColor(context, R.color.gradient_color_41)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_41),
                ContextCompat.getColor(context, R.color.gradient_color_42)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_42),
                ContextCompat.getColor(context, R.color.gradient_color_43)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_43),
                ContextCompat.getColor(context, R.color.gradient_color_44)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_44),
                ContextCompat.getColor(context, R.color.gradient_color_45)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_45),
                ContextCompat.getColor(context, R.color.gradient_color_46)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_46),
                ContextCompat.getColor(context, R.color.gradient_color_47)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_47),
                ContextCompat.getColor(context, R.color.gradient_color_48)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_48),
                ContextCompat.getColor(context, R.color.gradient_color_49)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_49),
                ContextCompat.getColor(context, R.color.gradient_color_50)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_50),
                ContextCompat.getColor(context, R.color.gradient_color_51)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_51),
                ContextCompat.getColor(context, R.color.gradient_color_52)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_52),
                ContextCompat.getColor(context, R.color.gradient_color_53)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_53),
                ContextCompat.getColor(context, R.color.gradient_color_54)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_54),
                ContextCompat.getColor(context, R.color.gradient_color_55)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_55),
                ContextCompat.getColor(context, R.color.gradient_color_56)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_56),
                ContextCompat.getColor(context, R.color.gradient_color_57)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_57),
                ContextCompat.getColor(context, R.color.gradient_color_58)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_58),
                ContextCompat.getColor(context, R.color.gradient_color_59)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_59),
                ContextCompat.getColor(context, R.color.gradient_color_60)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_60),
                ContextCompat.getColor(context, R.color.gradient_color_61)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_61),
                ContextCompat.getColor(context, R.color.gradient_color_62)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_62),
                ContextCompat.getColor(context, R.color.gradient_color_63)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_63),
                ContextCompat.getColor(context, R.color.gradient_color_64)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_64),
                ContextCompat.getColor(context, R.color.gradient_color_65)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_65),
                ContextCompat.getColor(context, R.color.gradient_color_66)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_66),
                ContextCompat.getColor(context, R.color.gradient_color_67)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_67),
                ContextCompat.getColor(context, R.color.gradient_color_68)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_68),
                ContextCompat.getColor(context, R.color.gradient_color_69)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_69),
                ContextCompat.getColor(context, R.color.gradient_color_70)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_70),
                ContextCompat.getColor(context, R.color.gradient_color_71)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_71),
                ContextCompat.getColor(context, R.color.gradient_color_72)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_72),
                ContextCompat.getColor(context, R.color.gradient_color_73)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_73),
                ContextCompat.getColor(context, R.color.gradient_color_74)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_74),
                ContextCompat.getColor(context, R.color.gradient_color_75)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_75),
                ContextCompat.getColor(context, R.color.gradient_color_76)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_76),
                ContextCompat.getColor(context, R.color.gradient_color_77)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_77),
                ContextCompat.getColor(context, R.color.gradient_color_78)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_78),
                ContextCompat.getColor(context, R.color.gradient_color_79)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_79),
                ContextCompat.getColor(context, R.color.gradient_color_80)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_80),
                ContextCompat.getColor(context, R.color.gradient_color_81)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_81),
                ContextCompat.getColor(context, R.color.gradient_color_82)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_82),
                ContextCompat.getColor(context, R.color.gradient_color_83)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_83),
                ContextCompat.getColor(context, R.color.gradient_color_84)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_84),
                ContextCompat.getColor(context, R.color.gradient_color_85)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_85),
                ContextCompat.getColor(context, R.color.gradient_color_86)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_86),
                ContextCompat.getColor(context, R.color.gradient_color_87)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_87),
                ContextCompat.getColor(context, R.color.gradient_color_88)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_88),
                ContextCompat.getColor(context, R.color.gradient_color_89)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_89),
                ContextCompat.getColor(context, R.color.gradient_color_90)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_90),
                ContextCompat.getColor(context, R.color.gradient_color_91)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_91),
                ContextCompat.getColor(context, R.color.gradient_color_92)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_92),
                ContextCompat.getColor(context, R.color.gradient_color_93)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_93),
                ContextCompat.getColor(context, R.color.gradient_color_94)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_94),
                ContextCompat.getColor(context, R.color.gradient_color_95)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_95),
                ContextCompat.getColor(context, R.color.gradient_color_97)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_97),
                ContextCompat.getColor(context, R.color.gradient_color_98)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_98),
                ContextCompat.getColor(context, R.color.gradient_color_99)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_99),
                ContextCompat.getColor(context, R.color.gradient_color_100)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_100),
                ContextCompat.getColor(context, R.color.gradient_color_101)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_101),
                ContextCompat.getColor(context, R.color.gradient_color_102)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_102),
                ContextCompat.getColor(context, R.color.gradient_color_103)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_103),
                ContextCompat.getColor(context, R.color.gradient_color_104)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_104),
                ContextCompat.getColor(context, R.color.gradient_color_105)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_105),
                ContextCompat.getColor(context, R.color.gradient_color_106)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_106),
                ContextCompat.getColor(context, R.color.gradient_color_107)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_107),
                ContextCompat.getColor(context, R.color.gradient_color_108)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_108),
                ContextCompat.getColor(context, R.color.gradient_color_109)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_109),
                ContextCompat.getColor(context, R.color.gradient_color_110)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_110),
                ContextCompat.getColor(context, R.color.gradient_color_111)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_111),
                ContextCompat.getColor(context, R.color.gradient_color_112)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_112),
                ContextCompat.getColor(context, R.color.gradient_color_113)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_113),
                ContextCompat.getColor(context, R.color.gradient_color_114)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_114),
                ContextCompat.getColor(context, R.color.gradient_color_115)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_115),
                ContextCompat.getColor(context, R.color.gradient_color_116)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_116),
                ContextCompat.getColor(context, R.color.gradient_color_117)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_117),
                ContextCompat.getColor(context, R.color.gradient_color_118)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_118),
                ContextCompat.getColor(context, R.color.gradient_color_119)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_119),
                ContextCompat.getColor(context, R.color.gradient_color_120)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_120),
                ContextCompat.getColor(context, R.color.gradient_color_121)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_121),
                ContextCompat.getColor(context, R.color.gradient_color_122)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_122),
                ContextCompat.getColor(context, R.color.gradient_color_123)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_123),
                ContextCompat.getColor(context, R.color.gradient_color_124)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_124),
                ContextCompat.getColor(context, R.color.gradient_color_125)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_125),
                ContextCompat.getColor(context, R.color.gradient_color_126)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_126),
                ContextCompat.getColor(context, R.color.gradient_color_127)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_127),
                ContextCompat.getColor(context, R.color.gradient_color_128)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_128),
                ContextCompat.getColor(context, R.color.gradient_color_129)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_129),
                ContextCompat.getColor(context, R.color.gradient_color_130)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_130),
                ContextCompat.getColor(context, R.color.gradient_color_131)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_131),
                ContextCompat.getColor(context, R.color.gradient_color_132)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_132),
                ContextCompat.getColor(context, R.color.gradient_color_133)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_133),
                ContextCompat.getColor(context, R.color.gradient_color_134)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_134),
                ContextCompat.getColor(context, R.color.gradient_color_135)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_135),
                ContextCompat.getColor(context, R.color.gradient_color_136)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_136),
                ContextCompat.getColor(context, R.color.gradient_color_137)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_137),
                ContextCompat.getColor(context, R.color.gradient_color_138)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_138),
                ContextCompat.getColor(context, R.color.gradient_color_139)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_139),
                ContextCompat.getColor(context, R.color.gradient_color_140)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_140),
                ContextCompat.getColor(context, R.color.gradient_color_141)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_141),
                ContextCompat.getColor(context, R.color.gradient_color_142)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_142),
                ContextCompat.getColor(context, R.color.gradient_color_143)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_143),
                ContextCompat.getColor(context, R.color.gradient_color_144)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_144),
                ContextCompat.getColor(context, R.color.gradient_color_145)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_145),
                ContextCompat.getColor(context, R.color.gradient_color_146)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_146),
                ContextCompat.getColor(context, R.color.gradient_color_147)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_147),
                ContextCompat.getColor(context, R.color.gradient_color_148)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_148),
                ContextCompat.getColor(context, R.color.gradient_color_149)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_149),
                ContextCompat.getColor(context, R.color.gradient_color_150)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_150),
                ContextCompat.getColor(context, R.color.gradient_color_151)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_151),
                ContextCompat.getColor(context, R.color.gradient_color_152)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_152),
                ContextCompat.getColor(context, R.color.gradient_color_153)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_153),
                ContextCompat.getColor(context, R.color.gradient_color_154)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_154),
                ContextCompat.getColor(context, R.color.gradient_color_156)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_156),
                ContextCompat.getColor(context, R.color.gradient_color_157)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_157),
                ContextCompat.getColor(context, R.color.gradient_color_158)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_158),
                ContextCompat.getColor(context, R.color.gradient_color_159)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_159),
                ContextCompat.getColor(context, R.color.gradient_color_160)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_160),
                ContextCompat.getColor(context, R.color.gradient_color_161)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_161),
                ContextCompat.getColor(context, R.color.gradient_color_162)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_162),
                ContextCompat.getColor(context, R.color.gradient_color_163)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_163),
                ContextCompat.getColor(context, R.color.gradient_color_164)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_164),
                ContextCompat.getColor(context, R.color.gradient_color_165)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_165),
                ContextCompat.getColor(context, R.color.gradient_color_166)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_166),
                ContextCompat.getColor(context, R.color.gradient_color_167)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_167),
                ContextCompat.getColor(context, R.color.gradient_color_168)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_168),
                ContextCompat.getColor(context, R.color.gradient_color_169)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_169),
                ContextCompat.getColor(context, R.color.gradient_color_170)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_170),
                ContextCompat.getColor(context, R.color.gradient_color_171)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_171),
                ContextCompat.getColor(context, R.color.gradient_color_172)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_172),
                ContextCompat.getColor(context, R.color.gradient_color_173)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_173),
                ContextCompat.getColor(context, R.color.gradient_color_174)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_174),
                ContextCompat.getColor(context, R.color.gradient_color_175)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_175),
                ContextCompat.getColor(context, R.color.gradient_color_176)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_176),
                ContextCompat.getColor(context, R.color.gradient_color_177)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_177),
                ContextCompat.getColor(context, R.color.gradient_color_178)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_178),
                ContextCompat.getColor(context, R.color.gradient_color_179)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_179),
                ContextCompat.getColor(context, R.color.gradient_color_180)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_180),
                ContextCompat.getColor(context, R.color.gradient_color_181)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_181),
                ContextCompat.getColor(context, R.color.gradient_color_182)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_182),
                ContextCompat.getColor(context, R.color.gradient_color_183)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_183),
                ContextCompat.getColor(context, R.color.gradient_color_184)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_184),
                ContextCompat.getColor(context, R.color.gradient_color_185)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_185),
                ContextCompat.getColor(context, R.color.gradient_color_186)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_186),
                ContextCompat.getColor(context, R.color.gradient_color_187)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_187),
                ContextCompat.getColor(context, R.color.gradient_color_188)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_188),
                ContextCompat.getColor(context, R.color.gradient_color_189)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_189),
                ContextCompat.getColor(context, R.color.gradient_color_190)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_190),
                ContextCompat.getColor(context, R.color.gradient_color_191)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_191),
                ContextCompat.getColor(context, R.color.gradient_color_192)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_192),
                ContextCompat.getColor(context, R.color.gradient_color_193)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_193),
                ContextCompat.getColor(context, R.color.gradient_color_194)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_194),
                ContextCompat.getColor(context, R.color.gradient_color_196)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_194),
                ContextCompat.getColor(context, R.color.gradient_color_197)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_194),
                ContextCompat.getColor(context, R.color.gradient_color_198)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_194),
                ContextCompat.getColor(context, R.color.gradient_color_199)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_197),
                ContextCompat.getColor(context, R.color.gradient_color_200)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_198),
                ContextCompat.getColor(context, R.color.gradient_color_201)));
        gradientColorsList.add(new GradientColor(ContextCompat.getColor(context, R.color.gradient_color_199),
                ContextCompat.getColor(context, R.color.gradient_color_202)));
        return gradientColorsList;

    }

    public static String[] colors = {"#610061", "#640066", "#67006a", "#6a006f", "#6d0073", "#6f0077", "#72007c", "#740080", "#760084", "#780088", "#79008d", "#7b0091", "#7c0095", "#7e0099", "#7f009d", "#8000a1", "#8100a5", "#8100a9", "#8200ad", "#8200b1", "#8300b5", "#8300b9", "#8300bc", "#8300c0", "#8200c4", "#8200c8", "#8100cc", "#8100cf", "#8000d3", "#7f00d7", "#7e00db", "#7c00de", "#7b00e2", "#7900e6", "#7800e9", "#7600ed", "#7400f1", "#7100f4", "#6f00f8", "#6d00fb", "#6a00ff", "#6600ff", "#6100ff", "#5d00ff", "#5900ff", "#5400ff", "#5000ff", "#4b00ff", "#4600ff", "#4200ff", "#3d00ff", "#3800ff", "#3300ff", "#2e00ff", "#2800ff", "#2300ff", "#1d00ff", "#1700ff", "#1100ff", "#0a00ff", "#0000ff", "#000bff", "#0013ff", "#001bff", "#0022ff", "#0028ff", "#002fff", "#0035ff", "#003bff", "#0041ff", "#0046ff", "#004cff", "#0051ff", "#0057ff", "#005cff", "#0061ff", "#0066ff", "#006cff", "#0071ff", "#0076ff", "#007bff", "#007fff", "#0084ff", "#0089ff", "#008eff", "#0092ff", "#0097ff", "#009cff", "#00a0ff", "#00a5ff", "#00a9ff", "#00aeff", "#00b2ff", "#00b7ff", "#00bbff", "#00c0ff", "#00c4ff", "#00c8ff", "#00cdff", "#00d1ff", "#00d5ff", "#00daff", "#00deff", "#00e2ff", "#00e6ff", "#00eaff", "#00efff", "#00f3ff", "#00f7ff", "#00fbff", "#00ffff", "#00fff5", "#00ffea", "#00ffe0", "#00ffd5", "#00ffcb", "#00ffc0", "#00ffb5", "#00ffa9", "#00ff9e", "#00ff92", "#00ff87", "#00ff7b", "#00ff6e", "#00ff61", "#00ff54", "#00ff46", "#00ff38", "#00ff28", "#00ff17", "#00ff00", "#09ff00", "#0fff00", "#15ff00", "#1aff00", "#1fff00", "#24ff00", "#28ff00", "#2dff00", "#31ff00", "#36ff00", "#3aff00", "#3eff00", "#42ff00", "#46ff00", "#4aff00", "#4eff00", "#52ff00", "#56ff00", "#5aff00", "#5eff00", "#61ff00", "#65ff00", "#69ff00", "#6cff00", "#70ff00", "#73ff00", "#77ff00", "#7bff00", "#7eff00", "#81ff00", "#85ff00", "#88ff00", "#8cff00", "#8fff00", "#92ff00", "#96ff00", "#99ff00", "#9cff00", "#a0ff00", "#a3ff00", "#a6ff00", "#a9ff00", "#adff00", "#b0ff00", "#b3ff00", "#b6ff00", "#b9ff00", "#bdff00", "#c0ff00", "#c3ff00", "#c6ff00", "#c9ff00", "#ccff00", "#cfff00", "#d2ff00", "#d5ff00", "#d8ff00", "#dbff00", "#deff00", "#e1ff00", "#e4ff00", "#e7ff00", "#eaff00", "#edff00", "#f0ff00", "#f3ff00", "#f6ff00", "#f9ff00", "#fcff00", "#ffff00", "#fffc00", "#fff900", "#fff600", "#fff200", "#ffef00", "#ffec00", "#ffe900", "#ffe600", "#ffe200", "#ffdf00", "#ffdc00", "#ffd900", "#ffd500", "#ffd200", "#ffcf00", "#ffcb00", "#ffc800", "#ffc500", "#ffc100", "#ffbe00", "#ffbb00", "#ffb700", "#ffb400", "#ffb000", "#ffad00", "#ffa900", "#ffa600", "#ffa200", "#ff9f00", "#ff9b00", "#ff9800", "#ff9400", "#ff9100", "#ff8d00", "#ff8900", "#ff8600", "#ff8200", "#ff7e00", "#ff7b00", "#ff7700", "#ff7300", "#ff6f00", "#ff6b00", "#ff6700", "#ff6300", "#ff5f00", "#ff5b00", "#ff5700", "#ff5300", "#ff4f00", "#ff4b00", "#ff4600", "#ff4200", "#ff3e00", "#ff3900", "#ff3400", "#ff3000", "#ff2b00", "#ff2600", "#ff2100", "#ff1b00", "#ff1600", "#ff1000", "#ff0900", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000", "#fd0000", "#fb0000", "#fa0000", "#f80000", "#f60000", "#f40000", "#f20000", "#f10000", "#ef0000", "#ed0000", "#eb0000", "#e90000", "#e80000", "#e60000", "#e40000", "#e20000", "#e00000", "#de0000", "#dc0000", "#db0000", "#d90000", "#d70000", "#d50000", "#d30000", "#d10000", "#cf0000", "#ce0000", "#cc0000", "#ca0000", "#c80000", "#c60000", "#c40000", "#c20000", "#c00000", "#be0000", "#bc0000", "#ba0000", "#b90000", "#b70000", "#b50000", "#b30000", "#b10000", "#af0000", "#ad0000", "#ab0000", "#a90000", "#a70000", "#a50000", "#a30000", "#a10000", "#9f0000", "#9d0000", "#9b0000", "#990000", "#970000", "#950000", "#930000", "#910000", "#8f0000", "#8d0000", "#8a0000", "#880000", "#860000", "#840000", "#820000", "#800000", "#7e0000", "#7c0000", "#7a0000", "#770000", "#750000", "#730000", "#710000", "#6f0000", "#6d0000", "#6a0000", "#680000", "#660000", "#640000", "#610000"};

    static Double[] channelA_marine = {0.011355, 0.013041, 0.015038, 0.017291, 0.020976, 0.024853, 0.029213, 0.034649, 0.041593, 0.05048, 0.060967, 0.074604, 0.089718, 0.109809, 0.131041, 0.158107, 0.186158, 0.221789, 0.259023, 0.302187, 0.347873, 0.398319, 0.450178, 0.503566, 0.552479, 0.598584, 0.631518, 0.66004, 0.6637, 0.663727, 0.645085, 0.623525, 0.586121, 0.546163, 0.500011, 0.452855, 0.404361, 0.360542, 0.320303, 0.287705, 0.258318, 0.236751, 0.216646, 0.202318, 0.188926, 0.181526, 0.175609, 0.17388, 0.174221, 0.177634, 0.184654, 0.194698, 0.209045, 0.225359, 0.247153, 0.270077, 0.299429, 0.329643, 0.364984, 0.402209, 0.444723, 0.489803, 0.538599, 0.589906, 0.643222, 0.697959, 0.753297, 0.80628, 0.858844, 0.900506, 0.940014, 0.967416, 0.989536, 0.999456, 1.0, 0.989278, 0.967269, 0.937957, 0.897774, 0.854179, 0.80369, 0.752377, 0.698588, 0.645609, 0.595355, 0.54699, 0.501619, 0.460089, 0.421755, 0.388198, 0.356632, 0.32893, 0.301973, 0.280023, 0.258702, 0.24002, 0.222907, 0.20862, 0.196362, 0.185959, 0.178049, 0.171263, 0.16788, 0.165142, 0.165249, 0.166054, 0.169715, 0.173785, 0.178559, 0.185044, 0.192984, 0.200882, 0.208763, 0.217858, 0.227009, 0.23483, 0.242307, 0.248719, 0.254449, 0.259248, 0.261448, 0.261937, 0.262696, 0.26353, 0.262127, 0.26073, 0.259784, 0.258212, 0.255411, 0.253056, 0.251103, 0.250096, 0.249469, 0.248987, 0.248544, 0.248344, 0.247909, 0.246891, 0.245923, 0.245009, 0.243016, 0.240507, 0.237562, 0.234463, 0.230182, 0.225702, 0.22069, 0.215263, 0.209376, 0.203216, 0.196926, 0.190872, 0.184783, 0.177998, 0.171494, 0.165665, 0.15956, 0.153184, 0.147631, 0.142408, 0.137285, 0.132247, 0.127685, 0.123349, 0.119421, 0.115535, 0.11168, 0.108351, 0.105149, 0.100963, 0.097127, 0.094443, 0.091728, 0.088978, 0.086251, 0.083536, 0.081241, 0.079017, 0.076972, 0.075035, 0.073283, 0.071608, 0.069984, 0.068633, 0.067309, 0.065586, 0.064096, 0.063138, 0.061924, 0.060494, 0.059256, 0.058089, 0.057586, 0.057025, 0.056301, 0.055633, 0.055018, 0.054231, 0.053393, 0.052878, 0.052328, 0.051682, 0.051247, 0.051013, 0.050521, 0.049963, 0.049862, 0.049698, 0.049379, 0.04906, 0.048741, 0.048875, 0.049081, 0.048776, 0.048735, 0.049225, 0.049553, 0.049774, 0.050404, 0.051136, 0.052125, 0.053319, 0.054804, 0.056007, 0.057084, 0.059494, 0.061942, 0.064364, 0.067072, 0.070039, 0.073757, 0.077792, 0.08319, 0.089022, 0.095567, 0.102112, 0.108655, 0.117534, 0.126772, 0.136774, 0.147823, 0.159753, 0.173355, 0.187444, 0.202576, 0.218548, 0.235599, 0.252529, 0.269289, 0.283277, 0.296181, 0.30707, 0.315223, 0.321955, 0.321629, 0.319872, 0.315034, 0.3071, 0.297059, 0.285168, 0.272898, 0.259959, 0.248418, 0.238017, 0.23085, 0.225, 0.222586, 0.222643, 0.224939, 0.229303, 0.234673, 0.242914, 0.252616, 0.263678, 0.276053, 0.288939, 0.303033, 0.316335, 0.328934, 0.341412, 0.353023, 0.361549, 0.367903, 0.372565, 0.371315, 0.367537, 0.358245, 0.345494, 0.330568, 0.308814, 0.286456, 0.263306, 0.238404, 0.212748, 0.188115, 0.164425, 0.142087, 0.123142, 0.105683, 0.091237, 0.078662, 0.06784, 0.059276, 0.051575, 0.045489, 0.039506, 0.033617, 0.029317, 0.026033, 0.024242, 0.02191, 0.019584, 0.017958, 0.016621, 0.015506, 0.014517, 0.013629, 0.012917, 0.011827, 0.010938, 0.011946, 0.011832, 0.010797, 0.010163, 0.009542, 0.008907, 0.008703, 0.008461, 0.007449, 0.007239, 0.007578, 0.007893, 0.007838, 0.007288, 0.007796, 0.008251, 0.008064, 0.007302, 0.006248, 0.004893, 0.004784, 0.005656, 0.005593, 0.005584, 0.005654, 0.00613, 0.006343, 0.005838, 0.005121, 0.004487, 0.004446, 0.004963, 0.005748, 0.00629, 0.005847, 0.004644, 0.004204, 0.004311, 0.005015, 0.004888, 0.004863, 0.005061, 0.005614, 0.005764, 0.005066, 0.00518, 0.005213, 0.004647, 0.005741, 0.006416, 0.004603, 0.003824, 0.00384, 0.005299, 0.00584, 0.006063, 0.006364, 0.00538, 0.004563, 0.006088, 0.005361, 0.00436, 0.005291, 0.00476, 0.004173, 0.004937, 0.005278, 0.005112, 0.004003, 0.005051, 0.005766, 0.004777, 0.004442, 0.004723, 0.005796, 0.006532, 0.006291, 0.004947, 0.004883, 0.005152, 0.005734};

    static Double[] channelB_marine = {0.002496, 0.002362, 0.002884, 0.003277, 0.002944, 0.002706, 0.002702, 0.002803, 0.003051, 0.003172, 0.003188, 0.003029, 0.002788, 0.002528, 0.002264, 0.002594, 0.002932, 0.00296, 0.002884, 0.00242, 0.002255, 0.002656, 0.003037, 0.003397, 0.003785, 0.004191, 0.004568, 0.004937, 0.005677, 0.006476, 0.007764, 0.009082, 0.010562, 0.012257, 0.014475, 0.01705, 0.020101, 0.02373, 0.027802, 0.032916, 0.03847, 0.045342, 0.05246, 0.061404, 0.070667, 0.081989, 0.094389, 0.10983, 0.126213, 0.143997, 0.164493, 0.187262, 0.212508, 0.238885, 0.26947, 0.300921, 0.337925, 0.375808, 0.419144, 0.463942, 0.512848, 0.563683, 0.617313, 0.672651, 0.729356, 0.783727, 0.837112, 0.883063, 0.927705, 0.956996, 0.983501, 0.994345, 1.0, 0.993672, 0.980131, 0.957924, 0.928811, 0.895229, 0.856166, 0.815383, 0.776127, 0.737268, 0.701705, 0.66728, 0.63669, 0.608055, 0.582521, 0.558315, 0.535214, 0.513426, 0.492185, 0.470724, 0.449219, 0.427397, 0.405384, 0.382565, 0.359887, 0.337465, 0.315398, 0.293656, 0.272729, 0.25217, 0.233629, 0.215472, 0.199031, 0.183121, 0.169387, 0.15618, 0.143878, 0.132473, 0.121832, 0.112211, 0.103, 0.094685, 0.086562, 0.079597, 0.072836, 0.066715, 0.06102, 0.055907, 0.051023, 0.04629, 0.042139, 0.038149, 0.035131, 0.032228, 0.02978, 0.027281, 0.02468, 0.022373, 0.02033, 0.018762, 0.017384, 0.015939, 0.014522, 0.013451, 0.012345, 0.011152, 0.010314, 0.00986, 0.009205, 0.008454, 0.007865, 0.007282, 0.006478, 0.005747, 0.005211, 0.004898, 0.004835, 0.004542, 0.004138, 0.003997, 0.003885, 0.00371, 0.003506, 0.003234, 0.003049, 0.00295, 0.002887, 0.002838, 0.002736, 0.002623, 0.002469, 0.002287, 0.002057, 0.001976, 0.002005, 0.002083, 0.002168, 0.001835, 0.0016, 0.001687, 0.001695, 0.001609, 0.001726, 0.00194, 0.00156, 0.001178, 0.001164, 0.001187, 0.001273, 0.00136, 0.001447, 0.001513, 0.001552, 0.001264, 0.001069, 0.001085, 0.001204, 0.001411, 0.00149, 0.00152, 0.001061, 0.000698, 0.000596, 0.00069, 0.000967, 0.000805, 0.000511, 0.000713, 0.000882, 0.000954, 0.000907, 0.000749, 0.000673, 0.000621, 0.000541, 0.000413, 0.000163, 0.000134, 0.000291, 0.000438, 0.000551, 0.00022, 0.0, 0.0, 0.000123, 0.000328, 0.000445, 0.000501, 0.000266, 0.000097, 0.000023, 0.000041, 0.000102, 0.000206, 0.000264, 0.000173, 0.000131, 0.000131, 0.000069, 0.000038, 0.000455, 0.000832, 0.001144, 0.000959, 0.000525, 0.000522, 0.000511, 0.000417, 0.000255, 0.000035, 0.0, 0.0, 0.0, 0.000125, 0.000412, 0.000536, 0.000603, 0.0007, 0.000606, 0.000154, 0.000118, 0.000297, 0.000447, 0.000596, 0.000746, 0.000591, 0.000228, 0.000365, 0.000519, 0.000572, 0.00056, 0.000494, 0.000233, 0.0, 0.0, 0.000064, 0.000187, 0.000282, 0.000369, 0.000442, 0.000451, 0.0004, 0.0003, 0.000217, 0.000236, 0.000492, 0.000958, 0.000482, 0.000082, 0.000463, 0.000577, 0.000485, 0.000603, 0.000671, 0.000537, 0.000706, 0.001066, 0.000733, 0.000554, 0.000739, 0.000772, 0.000716, 0.000265, 0.000009, 0.000029, 0.000198, 0.000407, 0.000601, 0.00072, 0.000766, 0.000349, 0.000076, 0.000361, 0.000564, 0.000708, 0.000264, 0.000083, 0.000287, 0.000367, 0.000439, 0.000627, 0.000671, 0.000604, 0.000243, 0.0, 0.0, 0.0, 0.000075, 0.000586, 0.000535, 0.000024, 0.00078, 0.0011, 0.00053, 0.000516, 0.000643, 0.000646, 0.000527, 0.000326, 0.000249, 0.00015, 0.00002, 0.0, 0.000015, 0.000068, 0.000198, 0.000371, 0.000617, 0.000851, 0.001076, 0.00042, 0.000031, 0.000004, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.000097, 0.000266, 0.000601, 0.000476, 0.000007, 0.000412, 0.000474, 0.000164, 0.000419, 0.000434, 0.000048, 0.0, 0.000082, 0.000319, 0.000166, 0.000147, 0.000635, 0.000693, 0.000715, 0.000975, 0.001352, 0.001558, 0.001089, 0.000496, 0.000078, 0.000429, 0.001188, 0.001782, 0.001237, 0.001066, 0.001046, 0.001056, 0.001258, 0.001561, 0.001968, 0.000994, 0.0, 0.0, 0.000185, 0.000417, 0.000654, 0.000817, 0.000855, 0.000719, 0.000899, 0.001014, 0.001025, 0.00032, 0.000398, 0.001061};

    static Double[] channelC_marine = {0.007269, 0.007663, 0.00958, 0.011526, 0.013625, 0.016408, 0.020914, 0.025814, 0.031268, 0.038095, 0.046054, 0.055919, 0.066677, 0.081394, 0.097018, 0.118386, 0.1406, 0.168854, 0.198811, 0.235075, 0.274163, 0.318587, 0.366011, 0.416683, 0.466892, 0.516809, 0.559737, 0.600323, 0.627215, 0.65207, 0.664762, 0.67581, 0.677938, 0.679547, 0.679898, 0.681827, 0.685858, 0.697977, 0.716294, 0.743461, 0.774349, 0.814445, 0.856262, 0.895717, 0.933754, 0.961834, 0.985573, 0.997046, 1.0, 0.990299, 0.971342, 0.944624, 0.908256, 0.867476, 0.824431, 0.780918, 0.740861, 0.702064, 0.672016, 0.644798, 0.625529, 0.610768, 0.602531, 0.597045, 0.593763, 0.59202, 0.590929, 0.588228, 0.585241, 0.576464, 0.566752, 0.551789, 0.535302, 0.515295, 0.492887, 0.467595, 0.440107, 0.411199, 0.381951, 0.352596, 0.324502, 0.29678, 0.272371, 0.248712, 0.227581, 0.207506, 0.189107, 0.172341, 0.156934, 0.142929, 0.129508, 0.117423, 0.105598, 0.095574, 0.085883, 0.077591, 0.069925, 0.063385, 0.05686, 0.050349, 0.045354, 0.041045, 0.037583, 0.03427, 0.031204, 0.028254, 0.025781, 0.023518, 0.02162, 0.019732, 0.017854, 0.016373, 0.015052, 0.013145, 0.011268, 0.010856, 0.010328, 0.009435, 0.008626, 0.007932, 0.007555, 0.007385, 0.006696, 0.005864, 0.00565, 0.005406, 0.004876, 0.004441, 0.004193, 0.004056, 0.00402, 0.003743, 0.003369, 0.003327, 0.003271, 0.002731, 0.002377, 0.002485, 0.002394, 0.002088, 0.002017, 0.002058, 0.001829, 0.001601, 0.001887, 0.001974, 0.001534, 0.001505, 0.001933, 0.001906, 0.001662, 0.001742, 0.00189, 0.002301, 0.002305, 0.001334, 0.001028, 0.001376, 0.001623, 0.00183, 0.001877, 0.001893, 0.001801, 0.001708, 0.001615, 0.00142, 0.00115, 0.001338, 0.001639, 0.001203, 0.000922, 0.00115, 0.00143, 0.001774, 0.001594, 0.001169, 0.001427, 0.001688, 0.001539, 0.001433, 0.001398, 0.001462, 0.001592, 0.001314, 0.000979, 0.00098, 0.001027, 0.001178, 0.001384, 0.001636, 0.001773, 0.001868, 0.001573, 0.001276, 0.000976, 0.00106, 0.001507, 0.000911, 0.000005, 0.000438, 0.000709, 0.00052, 0.000494, 0.00062, 0.000555, 0.000442, 0.000661, 0.000856, 0.000989, 0.000852, 0.000489, 0.00054, 0.000674, 0.000563, 0.000452, 0.00034, 0.000467, 0.000753, 0.000593, 0.000395, 0.000377, 0.000279, 0.000066, 0.000431, 0.001056, 0.000492, 0.000227, 0.001016, 0.001007, 0.000277, 0.000427, 0.000756, 0.000778, 0.000693, 0.000432, 0.000602, 0.000988, 0.001084, 0.000979, 0.000307, 0.000211, 0.000601, 0.000561, 0.000439, 0.000315, 0.000355, 0.000604, 0.000501, 0.000281, 0.00024, 0.000265, 0.000413, 0.000283, 0.000009, 0.000289, 0.000468, 0.000341, 0.000303, 0.000326, 0.000369, 0.0005, 0.000919, 0.001464, 0.002111, 0.00105, 0.000058, 0.000352, 0.000433, 0.000321, 0.000582, 0.000869, 0.00099, 0.000795, 0.000307, 0.000724, 0.001156, 0.000993, 0.000828, 0.000663, 0.001029, 0.001299, 0.000936, 0.000682, 0.000511, 0.000637, 0.000685, 0.000434, 0.000496, 0.000756, 0.000314, 0.000086, 0.000349, 0.000689, 0.001035, 0.000799, 0.000684, 0.000742, 0.000481, 0.000232, 0.000594, 0.000829, 0.000945, 0.000434, 0.0, 0.0, 0.0, 0.000013, 0.000596, 0.000802, 0.000454, 0.000829, 0.001234, 0.000881, 0.000489, 0.000067, 0.00002, 0.0, 0.0, 0.000047, 0.000091, 0.000028, 0.000035, 0.000099, 0.000441, 0.000927, 0.001646, 0.001037, 0.000322, 0.000953, 0.001532, 0.002059, 0.001021, 0.000429, 0.000431, 0.000257, 0.000118, 0.000153, 0.000631, 0.001114, 0.000341, 0.000481, 0.00134, 0.00085, 0.000819, 0.001408, 0.000656, 0.000128, 0.000539, 0.000414, 0.000317, 0.001007, 0.000721, 0.000062, 0.000652, 0.000581, 0.000004, 0.000248, 0.000435, 0.000561, 0.000888, 0.000799, 0.000088, 0.000038, 0.000112, 0.000245, 0.000123, 0.000041, 0.000177, 0.001074, 0.001626, 0.000611, 0.000969, 0.001458, 0.00089, 0.000778, 0.001007, 0.001828, 0.002295, 0.002314, 0.001239, 0.000757, 0.000799, 0.001837, 0.001264, 0.00067, 0.001691, 0.001945, 0.001949, 0.001914, 0.002262, 0.002361, 0.00178, 0.000704, 0.000246, 0.000875, 0.000339, 0.000241, 0.000767, 0.002706, 0.002683, 0.001183};

    static Double[] channelA_fresh = {0.033319, 0.036676, 0.041985, 0.047963, 0.057679, 0.068562, 0.082388, 0.099224, 0.120284, 0.145115, 0.173053, 0.209988, 0.251141, 0.303996, 0.359537, 0.429583, 0.501304, 0.582895, 0.664957, 0.748769, 0.827454, 0.896456, 0.95046, 0.988228, 1.0, 0.995457, 0.963007, 0.921208, 0.851852, 0.778729, 0.697789, 0.617503, 0.540767, 0.468711, 0.408012, 0.35389, 0.30853, 0.271104, 0.239758, 0.213572, 0.189555, 0.170943, 0.153341, 0.139922, 0.127184, 0.118812, 0.111875, 0.108987, 0.107885, 0.109434, 0.113039, 0.118368, 0.12646, 0.135816, 0.148911, 0.162775, 0.180896, 0.199852, 0.224204, 0.250619, 0.282832, 0.318135, 0.35791, 0.402045, 0.44967, 0.500843, 0.553515, 0.606391, 0.659294, 0.703463, 0.745456, 0.775216, 0.799999, 0.813289, 0.817175, 0.809761, 0.791741, 0.766859, 0.731681, 0.693271, 0.648052, 0.602297, 0.557672, 0.514017, 0.473624, 0.435338, 0.400393, 0.368145, 0.338141, 0.311, 0.285052, 0.261625, 0.238661, 0.218186, 0.198207, 0.180305, 0.16318, 0.147455, 0.133151, 0.120147, 0.1091, 0.098935, 0.090796, 0.083023, 0.076334, 0.069865, 0.064291, 0.059241, 0.055091, 0.051035, 0.047061, 0.043719, 0.040632, 0.037626, 0.034672, 0.032299, 0.030019, 0.02803, 0.026488, 0.025557, 0.024329, 0.022905, 0.022019, 0.021281, 0.020381, 0.019458, 0.018435, 0.017587, 0.017082, 0.016598, 0.016133, 0.015655, 0.015174, 0.014918, 0.01471, 0.014737, 0.014496, 0.01359, 0.013169, 0.013272, 0.012959, 0.012448, 0.012117, 0.011834, 0.011817, 0.011766, 0.011624, 0.011621, 0.011772, 0.011513, 0.011058, 0.011336, 0.011677, 0.011641, 0.011578, 0.01145, 0.011423, 0.011496, 0.011248, 0.010873, 0.011262, 0.011616, 0.011385, 0.011407, 0.011889, 0.012131, 0.012193, 0.011872, 0.011458, 0.011667, 0.011704, 0.011178, 0.011082, 0.011499, 0.01156, 0.011454, 0.011184, 0.010928, 0.010859, 0.010887, 0.011086, 0.011259, 0.011416, 0.011341, 0.011247, 0.011554, 0.011742, 0.011658, 0.011613, 0.0116, 0.011785, 0.012016, 0.011975, 0.012039, 0.01239, 0.012878, 0.013495, 0.013728, 0.013848, 0.014613, 0.015252, 0.015535, 0.016085, 0.016889, 0.017675, 0.01846, 0.01943, 0.020583, 0.022202, 0.023672, 0.025016, 0.026903, 0.028964, 0.031599, 0.03436, 0.037371, 0.040137, 0.042739, 0.04672, 0.050996, 0.055826, 0.061076, 0.066922, 0.073536, 0.080495, 0.088879, 0.097847, 0.108468, 0.119991, 0.132332, 0.147371, 0.163284, 0.181484, 0.20107, 0.222937, 0.247411, 0.273191, 0.302711, 0.333195, 0.366076, 0.400042, 0.43492, 0.469596, 0.50365, 0.534038, 0.5608, 0.582898, 0.595004, 0.603016, 0.598407, 0.589042, 0.570837, 0.54682, 0.519785, 0.487754, 0.455463, 0.422922, 0.393609, 0.366492, 0.34629, 0.329268, 0.320651, 0.316729, 0.316641, 0.32259, 0.330872, 0.345056, 0.361788, 0.380831, 0.404565, 0.429582, 0.456706, 0.485692, 0.516411, 0.54746, 0.578416, 0.608733, 0.636474, 0.661932, 0.681057, 0.696789, 0.703577, 0.702636, 0.695672, 0.674708, 0.648906, 0.613296, 0.571794, 0.526584, 0.476456, 0.42613, 0.375724, 0.327576, 0.280844, 0.241811, 0.206088, 0.175111, 0.149729, 0.127041, 0.110754, 0.095361, 0.080805, 0.07072, 0.061711, 0.054145, 0.048281, 0.04339, 0.039611, 0.036083, 0.032927, 0.030842, 0.029037, 0.027206, 0.025545, 0.024015, 0.02313, 0.022102, 0.020755, 0.020857, 0.021269, 0.020666, 0.020176, 0.019777, 0.02004, 0.019824, 0.0187, 0.018767, 0.019031, 0.018553, 0.018157, 0.017828, 0.018794, 0.018957, 0.018044, 0.018576, 0.019035, 0.018613, 0.01898, 0.019547, 0.019175, 0.018759, 0.018308, 0.019021, 0.019181, 0.018599, 0.019149, 0.019546, 0.019233, 0.019425, 0.0196, 0.019066, 0.019545, 0.0205, 0.020904, 0.020985, 0.020822, 0.021112, 0.021514, 0.022038, 0.021931, 0.021591, 0.020968, 0.022306, 0.023309, 0.02317, 0.022871, 0.022863, 0.023612, 0.023898, 0.023836, 0.023161, 0.023522, 0.024283, 0.025211, 0.025778, 0.026192, 0.026537, 0.026321, 0.026128, 0.02678, 0.026924, 0.026767, 0.026251, 0.027763, 0.029028, 0.02745, 0.027847, 0.028559, 0.028563, 0.02871, 0.02894, 0.029269, 0.029417, 0.02955, 0.029708, 0.0308, 0.031487, 0.031618, 0.03318, 0.033529, 0.032942};

    static Double[] channelB_fresh = {0.014863, 0.014244, 0.014895, 0.015492, 0.015785, 0.015983, 0.015942, 0.015842, 0.015661, 0.015188, 0.014476, 0.013964, 0.013547, 0.013268, 0.01302, 0.012657, 0.012339, 0.012611, 0.012729, 0.012274, 0.012197, 0.012831, 0.012841, 0.012173, 0.012036, 0.012232, 0.012421, 0.012607, 0.012867, 0.013116, 0.012381, 0.011766, 0.011803, 0.011804, 0.011719, 0.011478, 0.01103, 0.010755, 0.010613, 0.010148, 0.009547, 0.009945, 0.01053, 0.010872, 0.011111, 0.010636, 0.010292, 0.010319, 0.010109, 0.009546, 0.009493, 0.009868, 0.009854, 0.009662, 0.010075, 0.010613, 0.010354, 0.010095, 0.009996, 0.009897, 0.009801, 0.009854, 0.010123, 0.010001, 0.009566, 0.009665, 0.009989, 0.010174, 0.010335, 0.009918, 0.009562, 0.009538, 0.009652, 0.010084, 0.010377, 0.010505, 0.010829, 0.01128, 0.012219, 0.013312, 0.014115, 0.014897, 0.015737, 0.016685, 0.017994, 0.01969, 0.022, 0.024485, 0.027118, 0.029662, 0.03217, 0.036662, 0.041458, 0.046233, 0.051558, 0.059192, 0.066971, 0.075011, 0.084649, 0.095751, 0.108927, 0.12304, 0.140795, 0.159234, 0.180513, 0.202754, 0.228942, 0.256539, 0.286559, 0.318343, 0.351629, 0.387291, 0.423902, 0.462176, 0.50062, 0.538546, 0.576417, 0.614112, 0.650573, 0.685347, 0.715806, 0.743422, 0.771955, 0.80074, 0.827286, 0.853327, 0.876966, 0.899777, 0.920965, 0.940479, 0.958475, 0.973021, 0.986179, 0.9935, 0.9996, 1.0, 0.998017, 0.990112, 0.97911, 0.964766, 0.945278, 0.923332, 0.89786, 0.871553, 0.841074, 0.81012, 0.777897, 0.744989, 0.711319, 0.67873, 0.646659, 0.615164, 0.583962, 0.55502, 0.526554, 0.499229, 0.472686, 0.446913, 0.422875, 0.399533, 0.37774, 0.356275, 0.336072, 0.31662, 0.298531, 0.280782, 0.263287, 0.247535, 0.232254, 0.217875, 0.203794, 0.190695, 0.178201, 0.166426, 0.156012, 0.146234, 0.136847, 0.127661, 0.119499, 0.111626, 0.104252, 0.09786, 0.092127, 0.086577, 0.081118, 0.076415, 0.071806, 0.06741, 0.063605, 0.060298, 0.056741, 0.053141, 0.050585, 0.047802, 0.044404, 0.041768, 0.039854, 0.037756, 0.035603, 0.033823, 0.032054, 0.030316, 0.028743, 0.027326, 0.02624, 0.025257, 0.024418, 0.023585, 0.022768, 0.021989, 0.021241, 0.020232, 0.019186, 0.018478, 0.017877, 0.01749, 0.016906, 0.016192, 0.015798, 0.015466, 0.015215, 0.014734, 0.013926, 0.013518, 0.01329, 0.012647, 0.012235, 0.012577, 0.012518, 0.012096, 0.011755, 0.011441, 0.011213, 0.011287, 0.011855, 0.01184, 0.011533, 0.011305, 0.010928, 0.01009, 0.009992, 0.010518, 0.010434, 0.01024, 0.010082, 0.010083, 0.010288, 0.009979, 0.009509, 0.009443, 0.009381, 0.009324, 0.009266, 0.009207, 0.00958, 0.009771, 0.009462, 0.009883, 0.010801, 0.010327, 0.009797, 0.009534, 0.009449, 0.009508, 0.009768, 0.009876, 0.009251, 0.00918, 0.009611, 0.009586, 0.009566, 0.00992, 0.010161, 0.010296, 0.009917, 0.009563, 0.009691, 0.009853, 0.010044, 0.010055, 0.010131, 0.01054, 0.010335, 0.009652, 0.009938, 0.010233, 0.010288, 0.010169, 0.00994, 0.010037, 0.010034, 0.009801, 0.010216, 0.010911, 0.011267, 0.011236, 0.010649, 0.010715, 0.010807, 0.009955, 0.009946, 0.010726, 0.011359, 0.011871, 0.012099, 0.012072, 0.011887, 0.010933, 0.010488, 0.010791, 0.011606, 0.012221, 0.011272, 0.011049, 0.011383, 0.010568, 0.010538, 0.012046, 0.012117, 0.011777, 0.011845, 0.012068, 0.012416, 0.011455, 0.011084, 0.01188, 0.012692, 0.013433, 0.01383, 0.013677, 0.013152, 0.013219, 0.012906, 0.012085, 0.012103, 0.012313, 0.01264, 0.012909, 0.013192, 0.013745, 0.013696, 0.013173, 0.013382, 0.013323, 0.012901, 0.013841, 0.014552, 0.014303, 0.014234, 0.014166, 0.013876, 0.014151, 0.01467, 0.014698, 0.01518, 0.015978, 0.014575, 0.014093, 0.014613, 0.015366, 0.015984, 0.016383, 0.016506, 0.01604, 0.014543, 0.014607, 0.015352, 0.016905, 0.017031, 0.016971, 0.017513, 0.016296, 0.015522, 0.018069, 0.018337, 0.01816, 0.019383, 0.018942, 0.018035, 0.017755, 0.018591, 0.019619, 0.019867, 0.020066, 0.020458, 0.021475, 0.020173, 0.018873, 0.019332, 0.020263, 0.021151, 0.021721, 0.02186, 0.021276, 0.019674, 0.021118, 0.022087, 0.022212, 0.022933, 0.023729, 0.024214};

    static Double[] channelC_fresh = {0.032141, 0.032816, 0.032147, 0.031546, 0.031324, 0.031198, 0.031317, 0.03116, 0.030617, 0.029538, 0.028018, 0.026692, 0.025456, 0.025708, 0.026302, 0.025767, 0.025206, 0.025076, 0.025118, 0.025798, 0.026195, 0.026057, 0.026104, 0.026353, 0.026902, 0.027639, 0.027981, 0.028192, 0.028999, 0.029897, 0.031396, 0.033034, 0.035419, 0.037811, 0.040221, 0.04294, 0.04607, 0.050295, 0.055357, 0.061552, 0.068224, 0.076092, 0.084185, 0.094102, 0.104282, 0.116116, 0.128595, 0.142893, 0.15846, 0.175912, 0.194715, 0.214651, 0.236704, 0.259726, 0.288059, 0.317486, 0.350359, 0.384042, 0.423094, 0.464115, 0.510664, 0.559857, 0.612877, 0.66775, 0.724106, 0.778838, 0.832884, 0.879772, 0.9254, 0.955648, 0.983014, 0.994183, 1.0, 0.993459, 0.979436, 0.956424, 0.926309, 0.891599, 0.853511, 0.814362, 0.775596, 0.73716, 0.702536, 0.669262, 0.640539, 0.613202, 0.588063, 0.564389, 0.541934, 0.520493, 0.499475, 0.48027, 0.461391, 0.444093, 0.427476, 0.413712, 0.401211, 0.390988, 0.382835, 0.376576, 0.372329, 0.36899, 0.368819, 0.36921, 0.370892, 0.372972, 0.376684, 0.380561, 0.384724, 0.389569, 0.394996, 0.400623, 0.40633, 0.412129, 0.417988, 0.424554, 0.431055, 0.437355, 0.443779, 0.45037, 0.454401, 0.456745, 0.461554, 0.46704, 0.471185, 0.475339, 0.479793, 0.483883, 0.48726, 0.490415, 0.493369, 0.495555, 0.497433, 0.499982, 0.502565, 0.504773, 0.506867, 0.508678, 0.510888, 0.513529, 0.515297, 0.51665, 0.518309, 0.520055, 0.522347, 0.524632, 0.526896, 0.528818, 0.53036, 0.532729, 0.535493, 0.537679, 0.539797, 0.542017, 0.543971, 0.545283, 0.54695, 0.548966, 0.550922, 0.552853, 0.554531, 0.556158, 0.557601, 0.559328, 0.561572, 0.563145, 0.564216, 0.564825, 0.565311, 0.565758, 0.565999, 0.565564, 0.565063, 0.564484, 0.563461, 0.562229, 0.561017, 0.559755, 0.558172, 0.556456, 0.55451, 0.552488, 0.550414, 0.548714, 0.547039, 0.544639, 0.542319, 0.540178, 0.538006, 0.535808, 0.533588, 0.531365, 0.529206, 0.526921, 0.524295, 0.521747, 0.519273, 0.51703, 0.514862, 0.513026, 0.511209, 0.509445, 0.50733, 0.504885, 0.503232, 0.501805, 0.499902, 0.498039, 0.49628, 0.49469, 0.493243, 0.491995, 0.490752, 0.488915, 0.487392, 0.486495, 0.485186, 0.483605, 0.482459, 0.481525, 0.48148, 0.481181, 0.480525, 0.480192, 0.480005, 0.479825, 0.479715, 0.479826, 0.479649, 0.479213, 0.479041, 0.47898, 0.479399, 0.479835, 0.480301, 0.47999, 0.47929, 0.478989, 0.478631, 0.478044, 0.477448, 0.476845, 0.476483, 0.476157, 0.475769, 0.474869, 0.473313, 0.471686, 0.470057, 0.468934, 0.467535, 0.465624, 0.463641, 0.461623, 0.460043, 0.45832, 0.456189, 0.453869, 0.451419, 0.448268, 0.44523, 0.442794, 0.439845, 0.436477, 0.432815, 0.429152, 0.425658, 0.422076, 0.418414, 0.414339, 0.410204, 0.406116, 0.402416, 0.399078, 0.393897, 0.388676, 0.38463, 0.380328, 0.3758, 0.370053, 0.364454, 0.36004, 0.355353, 0.350454, 0.345568, 0.340533, 0.335074, 0.330015, 0.325209, 0.319475, 0.313843, 0.308482, 0.303336, 0.298282, 0.293077, 0.287523, 0.281466, 0.276926, 0.272792, 0.268494, 0.263362, 0.25745, 0.252992, 0.248511, 0.243462, 0.238387, 0.233316, 0.228994, 0.224592, 0.220071, 0.215287, 0.210618, 0.206808, 0.202428, 0.197613, 0.193379, 0.18943, 0.185957, 0.182474, 0.178888, 0.17472, 0.170833, 0.167177, 0.164794, 0.161458, 0.156313, 0.152452, 0.149187, 0.14687, 0.143692, 0.139928, 0.136781, 0.133776, 0.13096, 0.127652, 0.124535, 0.12216, 0.120383, 0.11863, 0.115283, 0.112869, 0.111187, 0.109196, 0.106884, 0.10415, 0.1015, 0.099165, 0.097502, 0.095646, 0.093844, 0.092494, 0.091235, 0.089966, 0.088206, 0.086479, 0.084793, 0.083942, 0.083044, 0.082093, 0.079626, 0.077871, 0.077282, 0.076937, 0.076551, 0.07602, 0.073066, 0.070858, 0.071605, 0.070822, 0.069823, 0.069426, 0.069575, 0.06962, 0.068742, 0.068511, 0.068201, 0.066784, 0.066107, 0.065487, 0.064054, 0.063699, 0.063703, 0.063537, 0.064016, 0.064752, 0.065578, 0.063872, 0.061929, 0.061303, 0.060259, 0.060074, 0.061818, 0.060612, 0.060092, 0.061393, 0.061469, 0.061471, 0.061506, 0.062965, 0.06259, 0.060936};
}
