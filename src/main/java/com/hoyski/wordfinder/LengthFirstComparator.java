package com.hoyski.wordfinder;


import java.util.Comparator;

class LengthFirstComparator implements Comparator<String> {
    /**
     * Compares two Strings ordering shorter strings before longer. If 'o1' and 'o2' are the same length
     * then a standard String compare is performed
     *
     * @param o1 The "left" String
     * @param o2 The "right" String
     * @return See above
     */
    public int compare(String o1, String o2) {
        if (o1.length() != o2.length()) {
            // Sort shorter Strings before longer
            return o1.length() - o2.length();
        } else {
            // Sort equal length Strings alphabetically
            return o1.compareTo(o2);
        }
    }
}
