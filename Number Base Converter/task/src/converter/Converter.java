package converter;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Scanner;

public class Converter {

    private static final Scanner scanner = new Scanner(System.in);

    public static void menu1() {
        String command = "command"; // user's command
        while (!"/exit".matches(command)) {
            // Instruction: choose one from-> /from /to /exit
            System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
            command = scanner.nextLine();
            switch (command) {
                case "/exit":
                    break;
                default:
                    String[] bases = command.split("\\s+");
                    menu2(bases);
                    break;
            }

        }
    }

    private static void menu2(String[] bases) {
        String number = "number";
        while (!"/back".matches(number)) {
            System.out.printf("Enter number in base %s to convert to base %s (To go back type /back) ", bases[0], bases[1]);
            number = scanner.nextLine();
            if (!"/back".matches(number)) {
                String result;
                if (number.split("\\.").length == 1) {
                    result = fromSourceToTargetInteger(Integer.parseInt(bases[0]), Integer.parseInt(bases[1]), number);
                } else {
                    result = fromSourceToTargetFraction(Integer.parseInt(bases[0]), Integer.parseInt(bases[1]), number);
                }
                System.out.println("Conversion result: " + result);
            }
        }
    }

    private static String fromSourceToTargetInteger(int source, int target, String number) {
        // convert from 'source base' to 'decimal base'
        BigInteger decimal;
        if (source != 10) {
            decimal = toDecimalInteger(number, BigInteger.valueOf(source));
        } else {
            decimal = new BigInteger(number);
        }

        // convert from 'decimal base' to 'target base'
        return fromDecimalInteger(decimal, BigInteger.valueOf(target));
    }

    private static String fromSourceToTargetFraction(int source, int target, String number) {
        // convert from 'source base' to 'decimal base'
        BigDecimal decimal;
        if (source != 10) {
            decimal = toDecimalFraction(number, BigDecimal.valueOf(source));
        } else {
            decimal = new BigDecimal(number);
        }

        // convert from 'decimal base' to 'target base'
        return fromDecimalFraction(decimal, BigDecimal.valueOf(target));
    }

    private static BigInteger toDecimalInteger(String nonDecimal, BigInteger source) {
        int len = nonDecimal.length();
        BigInteger currentBase = BigInteger.ONE;
        BigInteger decimal = BigInteger.ZERO;
        for (int i = 0; i < len; i++) {
            int currentDigit = Integer.parseInt("" + charToInt(nonDecimal.charAt(len - i - 1)));
            BigInteger addThis = currentBase.multiply(BigInteger.valueOf(currentDigit));
            decimal = decimal.add(addThis);
            currentBase = currentBase.multiply(source);
        }
        return decimal;
    }

    private static String fromDecimalInteger(BigInteger decimal, BigInteger target) {
        if (target.intValue() == 10) {
            return target.toString();
        }
        String nonDecimal = "";
        BigInteger quotient = decimal;
        int remainder = 0;
        while (!quotient.equals(BigInteger.ZERO)) {
            remainder = quotient.remainder(target).intValue();
            quotient = quotient.divide(target);
            nonDecimal = "" + intToChar(remainder) + nonDecimal;
        }
        return nonDecimal;
    }

    private static BigDecimal toDecimalFraction(String nonDecimal, BigDecimal source) {
        BigDecimal integer = toDecimalFractionInt(nonDecimal.split("\\.")[0], source);
        BigDecimal fraction = toDecimalFractionFrc(nonDecimal.split("\\.")[1], source);
        return integer.add(fraction).setScale(5, RoundingMode.HALF_DOWN);
    }

    private static String fromDecimalFraction(BigDecimal decimal, BigDecimal target) {
        BigDecimal integer = decimal.setScale(0, RoundingMode.DOWN);
        BigDecimal fraction = decimal.subtract(integer);
        return fromDecimalFractionInt(integer, target) + fromDecimalFractionFrc(fraction, target);
    }

    private static BigDecimal toDecimalFractionInt(String integer, BigDecimal source) {
        int len = integer.length();
        BigDecimal currentBase = BigDecimal.ONE;
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < len; i++) {
            BigDecimal digit = new BigDecimal(charToInt(integer.charAt(len - i - 1)));
            result = result.add(currentBase.multiply(digit));
            currentBase = currentBase.multiply(source);
        }
        return result;
    }

    private static BigDecimal toDecimalFractionFrc(String fraction, BigDecimal source) {
        int len = fraction.length();
        BigDecimal currentBase = BigDecimal.ONE;
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < len; i++) {
            currentBase = currentBase.divide(source, 5, RoundingMode.HALF_DOWN);
            BigDecimal digit = new BigDecimal(charToInt(fraction.charAt(i)));
            result = result.add(currentBase.multiply(digit));
        }
        return result;
    }

    private static String fromDecimalFractionInt(BigDecimal integer, BigDecimal target) {
        BigDecimal quotient = integer;
        if (quotient.equals(BigDecimal.ZERO)) {
            return "0.";
        }

        String result = ".";
        while (!quotient.equals(BigDecimal.ZERO)) {
            BigDecimal remainder = quotient.remainder(target);
            quotient = quotient.divideToIntegralValue(target);
            result = intToChar(remainder.intValue()) + result;
        }
        return result;
    }

    private static String fromDecimalFractionFrc(BigDecimal fraction, BigDecimal target) {
        String result = "";
        BigDecimal remainder = fraction;
        BigDecimal two = new BigDecimal(2);
        for (int i = 0; i < 5; i++) {
            remainder = remainder.multiply(target);
            BigDecimal integer = remainder.setScale(0, RoundingMode.DOWN);
            if (!integer.equals(BigDecimal.ZERO)) {
                remainder = remainder.subtract(integer);
            }
            if (i == 4) {
                remainder = remainder.multiply(target);
                BigDecimal additionalInteger = remainder.setScale(0, RoundingMode.DOWN);
                if (additionalInteger.multiply(two).compareTo(target) > 0) {
                    integer = integer.add(BigDecimal.ONE);
                }
            }
            result += intToChar(integer.intValue());
        }
        return result;
    }

    private static int charToInt(char c) {
        if (c < 58) { // digit
            return c - 48;
        } else if (c < 71) { // upper case
            return c - 55;
        } else { // lower case
            return c - 87;
        }
    }

    private static char intToChar(int remainder) {
        if (remainder < 10) {
            return (char) (remainder + 48);
        } else {
            return (char) (remainder + 87);
        }
    }

    private static String decimalToNonDecimal(int decimal, int base) {
        String nonDecimal = "";
        int quotient = decimal;
        int remainder = 0;
        while (quotient != 0) {
            remainder = quotient % base;
            quotient = quotient / base;
            nonDecimal = String.format("%x", remainder) + nonDecimal;
        }
        return nonDecimal;
    }

    private static int nonDecimalToDecimal(String nonDecimal, int base) {
        int decimal = 0;
        int baseNumber = 1;
        for (int i = 0; i < nonDecimal.length(); i++) {
            char current = nonDecimal.charAt(nonDecimal.length() - i - 1);
            int digit;
            if (current < 58) { // digit
                digit = current - 48;
            } else if (current < 71) { // upper case
                digit = current - 55;
            } else { // lower case
                digit = current - 87;
            }
            decimal += digit * baseNumber;
            baseNumber *= base;
        }
        return decimal;
    }

}

