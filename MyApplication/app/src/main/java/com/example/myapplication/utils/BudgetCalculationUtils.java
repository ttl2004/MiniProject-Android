package com.example.myapplication.utils;

public final class BudgetCalculationUtils {
    public static final int MAX_DISPLAY_PERCENT = 999999;

    private BudgetCalculationUtils() {
    }

    public static int calculateDisplayPercent(long spentAmount, long limitAmount) {
        if (spentAmount <= 0 || limitAmount <= 0) {
            return 0;
        }

        double percent = (spentAmount / (double) limitAmount) * 100;
        if (Double.isNaN(percent) || percent <= 0) {
            return 0;
        }
        if (Double.isInfinite(percent) || percent >= MAX_DISPLAY_PERCENT) {
            return MAX_DISPLAY_PERCENT;
        }

        return (int) percent;
    }

    public static long safeAdd(long left, long right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException e) {
            return right >= 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
    }

    public static long safeSubtract(long left, long right) {
        try {
            return Math.subtractExact(left, right);
        } catch (ArithmeticException e) {
            return right >= 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }
}
