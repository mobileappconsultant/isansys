package com.isansys.pse_isansysportal;

import com.isansys.common.measurements.VitalSignType;

import java.util.ArrayList;

public class GraphConfigs
{
    static class GraphConfig
    {
        // Block Graph is setup to True if the Graph is only showing values between 0 and 1. E.g. the Manual Vitals selected via Button Selections
        private final boolean block_graph;

        // do_not_round_to_axis_most_significant_digit is set to True to prevent "bunching" if GridLabelRenderer decides to insert so many divisions that they become unreadable on smaller graphs
        // GridLabelRenderer mHumanRoundingY true produces axes for the most significant digit, e.g. 40,80,120... will be overridden to additionally include 100s, so instead of steps of 40
        // we have steps of 20. See IIT-2740
        // Currently used only for weight scales.
        public final boolean do_not_round_to_axis_most_significant_digit;

        GraphConfig(boolean block_graph, boolean fewer_y_axis_labels)
        {
            this.block_graph = block_graph;
            this.do_not_round_to_axis_most_significant_digit = fewer_y_axis_labels;
        }

        static class GraphYAxisMaxMin
        {
            int max;
            int min;

            GraphYAxisMaxMin()
            {
                this.max = 1;
                this.min = 0;
            }
        }

        public final ArrayList<GraphColourBand> graph_colour_bands = new ArrayList<>();
        public final GraphYAxisMaxMin graph_y_axis_max_min = new GraphYAxisMaxMin();

        void clear()
        {
            graph_colour_bands.clear();
        }


        void add(GraphColourBand graph_colour_band)
        {
            graph_colour_bands.add(graph_colour_band);

            if (block_graph)
            {
                graph_y_axis_max_min.max = 1;
                graph_y_axis_max_min.min = 0;
            }
            else
            {
                double largest_number = graph_colour_bands.get(0).greater_than_or_equal_value;
                double smallest_number = graph_colour_bands.get(graph_colour_bands.size() - 1).less_than_value;

                graph_y_axis_max_min.max = (int)(smallest_number);
                graph_y_axis_max_min.min = (int)(largest_number);
            }
        }
    }


    private final GraphConfig graph_config_heart_rate = new GraphConfig(false, false);
    private final GraphConfig graph_config_respiration_rate = new GraphConfig(false, false);
    private final GraphConfig graph_config_temperature = new GraphConfig(false, false);
    private final GraphConfig graph_config_spo2 = new GraphConfig(false, false);
    private final GraphConfig graph_config_blood_pressure = new GraphConfig(false, false);
    private final GraphConfig graph_config_weight = new GraphConfig(false, true);
    private final GraphConfig graph_config_early_warning_score = new GraphConfig(false, false);
    private final GraphConfig graph_config_supplemental_oxygen = new GraphConfig(true, false);
    private final GraphConfig graph_config_consciousness_levels = new GraphConfig(true, false);
    private final GraphConfig graph_config_capillary_refill_time = new GraphConfig(true, false);
    private final GraphConfig graph_config_respiration_distress = new GraphConfig(true, false);
    private final GraphConfig graph_config_family_or_nurse_concern = new GraphConfig(true, false);
    private final GraphConfig graph_config_urine_output = new GraphConfig(false, false);


    void clear()
    {
        graph_config_heart_rate.clear();
        graph_config_respiration_rate.clear();
        graph_config_temperature.clear();
        graph_config_spo2.clear();
        graph_config_blood_pressure.clear();
        graph_config_weight.clear();
        graph_config_early_warning_score.clear();
        graph_config_supplemental_oxygen.clear();
        graph_config_consciousness_levels.clear();
        graph_config_capillary_refill_time.clear();
        graph_config_respiration_distress.clear();
        graph_config_family_or_nurse_concern.clear();
        graph_config_urine_output.clear();
    }


    GraphConfig getGraphConfig(VitalSignType vital_sign_type)
    {
        switch(vital_sign_type)
        {
            case HEART_RATE:
            case MANUALLY_ENTERED_HEART_RATE:
                return graph_config_heart_rate;

            case RESPIRATION_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
                return graph_config_respiration_rate;

            case TEMPERATURE:
            case MANUALLY_ENTERED_TEMPERATURE:
                return graph_config_temperature;

            case SPO2:
            case MANUALLY_ENTERED_SPO2:
                return graph_config_spo2;

            case BLOOD_PRESSURE:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                return graph_config_blood_pressure;

            case WEIGHT:
            case MANUALLY_ENTERED_WEIGHT:
                return graph_config_weight;

            case EARLY_WARNING_SCORE:
                return graph_config_early_warning_score;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                return graph_config_supplemental_oxygen;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                return graph_config_consciousness_levels;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                return graph_config_capillary_refill_time;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return graph_config_respiration_distress;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return graph_config_family_or_nurse_concern;

            case MANUALLY_ENTERED_URINE_OUTPUT:
                return graph_config_urine_output;

            default:
                return null;
        }
    }
}
