package ngrams;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

/**
 * An object for mapping a year number (e.g. 1996) to numerical data. Provides
 * utility methods useful for data analysis.
 */
public class TimeSeries extends TreeMap<Integer, Double> {

    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    /**
     * Constructs a new empty TimeSeries.
     */
    public TimeSeries() {
        super();
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR,
     * inclusive of both end points.
     */
    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        for (Map.Entry<Integer, Double> entry : ts.entrySet()) {
            int year = entry.getKey();
            if (year >= startYear && year <= endYear) {
                this.put(year, entry.getValue());
            }
        }
    }

    /**
     * Returns all years for this time series in ascending order.
     */
    public List<Integer> years() {
        return new ArrayList<>(this.keySet());
    }

    /**
     * Returns all data for this time series. Must correspond to the order of years().
     */
    public List<Double> data() {
        return new ArrayList<>(this.values());
    }

    /**
     * Returns the year-wise sum of this TimeSeries with the given TS.
     */
    public TimeSeries plus(TimeSeries ts) {
        TimeSeries result = new TimeSeries();
        for (Integer year : this.keySet()) {
            result.put(year, this.get(year));
        }
        for (Map.Entry<Integer, Double> entry : ts.entrySet()) {
            int year = entry.getKey();
            double val = entry.getValue();
            result.put(year, result.getOrDefault(year, 0.0) + val);
        }
        return result;
    }

    /**
     * Returns the quotient of the value for each year in this TimeSeries divided by
     * the value for the same year in TS.
     */
    public TimeSeries dividedBy(TimeSeries ts) {
        TimeSeries result = new TimeSeries();
        for (Map.Entry<Integer, Double> entry : this.entrySet()) {
            int year = entry.getKey();
            if (!ts.containsKey(year)) {
                throw new IllegalArgumentException("Year " + year + " not found in divisor TimeSeries.");
            }
            result.put(year, this.get(year) / ts.get(year));
        }
        return result;
    }
}
