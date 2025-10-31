package dev.al3mid3x.punishments.utils;

public class TimeUtil {

    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    public static long parseTime(String timeString) {
        long totalMilliseconds = 0;
        long currentNum = 0;
        for (char c : timeString.toCharArray()) {
            if (Character.isDigit(c)) {
                currentNum = currentNum * 10 + (c - '0');
            } else {
                switch (c) {
                    case 's':
                        totalMilliseconds += currentNum * 1000;
                        break;
                    case 'm':
                        totalMilliseconds += currentNum * 1000 * 60;
                        break;
                    case 'h':
                        totalMilliseconds += currentNum * 1000 * 60 * 60;
                        break;
                    case 'd':
                        totalMilliseconds += currentNum * 1000 * 60 * 60 * 24;
                        break;
                    case 'w':
                        totalMilliseconds += currentNum * 1000 * 60 * 60 * 24 * 7;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid time unit: " + c);
                }
                currentNum = 0;
            }
        }
        if (currentNum != 0) {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }
        return totalMilliseconds;
    }
}
