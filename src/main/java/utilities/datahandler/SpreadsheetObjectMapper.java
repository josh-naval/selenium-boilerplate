package utilities.datahandler;

import customexception.InvalidExcelFileException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpreadsheetObjectMapper {
    private final static String XLSX = "xlsx";

    private final static String XLS = "xls";

    private final Workbook workbook;

    private final int sheet;

    private SpreadsheetObjectMapper(final String filePath, final int sheet) throws IOException,
            InvalidExcelFileException {

        if (!isFileExtensionValid(filePath))
            throw new InvalidExcelFileException("Unsupported file type.");

        this.sheet = sheet;
        workbook = createWorkBook(filePath);
    }

    private boolean isFileExtensionValid(final String filePath) {
        String fileExtension = FilenameUtils.getExtension(filePath);

        if (fileExtension.equalsIgnoreCase(XLSX))
            return true;

        if (fileExtension.equalsIgnoreCase(XLS))
            return true;

        return false;
    }

    private <T> ArrayList<T> map(final Class<T> clazz) throws Exception {
        final ArrayList<T> list = new ArrayList<T>();

        final Sheet sheet = workbook.getSheetAt(this.sheet);
        final int lastRow = sheet.getLastRowNum();

        for (int i = 1; i <= lastRow; i++) {
            final T obj = clazz.newInstance();
            final Field[] fields = obj.getClass().getDeclaredFields();

            for (final Field field : fields) {
                final String fieldName = field.getName();
                final int index = getHeaderIndex(fieldName, this.workbook, sheet);

                if (index == -1)
                    continue;

                final Cell cell = sheet.getRow(i).getCell(index);
                final Field classField = obj.getClass().getDeclaredField(fieldName);
                setObjectFieldValueFromCell(obj, classField, cell);
            }

            list.add(obj);
        }

        return list;
    }

    private void setObjectFieldValueFromCell(final Object obj, final Field field, final Cell cell) {
        final Class<?> clazz = field.getType();
        field.setAccessible(true);

        if (clazz.equals(String.class))
            setStringValue(obj, field, cell);

        if (clazz.equals(Date.class))
            setDateValue(obj, field, cell);

        if (clazz.equals(int.class) || clazz.equals(long.class) || clazz.equals(float.class) || clazz.equals(double.class))
            setNumericValue(clazz, obj, field, cell);

        if (clazz.equals(boolean.class))
            setBooleanValue(obj, field, cell);

    }

    private void setBooleanValue(final Object obj, final Field field, final Cell cell) {
        final boolean value = cell.getBooleanCellValue();
        try {
            field.set(obj, value);
        } catch (final Exception ex) {
            try {
                field.set(obj, null);
            } catch (final IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private void setStringValue(final Object obj, final Field field, final Cell cell) {
        try {
            field.set(obj, cell.getStringCellValue());
        } catch (final Exception ex) {
            try {
                field.set(obj, null);
            } catch (final IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private void setDateValue(final Object obj, final Field field, final Cell cell) {
        try {
            final Date date = cell.getDateCellValue();
            field.set(obj, date);
        } catch (final Exception ex) {
            try {
                field.set(obj, null);
            } catch (final IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private void setNumericValue(final Class<?> cls, final Object obj, final Field field, final Cell cell) {
        final double value = cell.getNumericCellValue();
        try {
            if (cls == int.class)
                field.set(obj, (int) value);

            if (cls == long.class)
                field.set(obj, (long) value);

            if (cls == float.class)
                field.set(obj, (float) value);

            if (cls == double.class)
                field.set(obj, value);

        } catch (final Exception ex) {
            try {
                field.set(obj, null);
            } catch (final IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private Workbook createWorkBook(final String file) throws IOException {
        final InputStream inp = new FileInputStream(file);
        return WorkbookFactory.create(inp);
    }

    private int getHeaderIndex(String fieldName, final Workbook workbook, final Sheet sheet) throws Exception {
        final int totalColumns = sheet.getRow(0).getLastCellNum();
        int index = -1;
        boolean isColumHeaderExist = false;

        for (index = 0; index < totalColumns; index++) {
            //first row is the column headers
            final Cell cell = sheet.getRow(0).getCell(index);
            final String columnHeader = cell.getStringCellValue().toLowerCase();

            isColumHeaderExist = columnHeader.equalsIgnoreCase(fieldName.toLowerCase());

            if (isColumHeaderExist)
                break;
        }

        if (!isColumHeaderExist) {
            System.out.println("\"" + fieldName + "\" not found in the spreadsheet");
            return -1;
        }

        return index;
    }

    public static class Map {

        private String filePath;

        private int sheet;

        private Class clazz;

        public Map from(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Map worksheet(int sheet) {
            this.sheet = sheet;
            return this;
        }

        public Map to(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public <T> List<T> perform() throws Exception {
            return new SpreadsheetObjectMapper(this.filePath, this.sheet).map(clazz);
        }
    }

    public static void main(String[] args) throws Exception {
    /*  Sample Usage
        String root = System.getProperty("user.dir");
        String filePath = "/src/main/resources/Book1.xlsx";
        String fullPath = root + filePath;

        List<Account> accounts = new Map().from(fullPath).worksheet(0).to(Account.class).perform();

        for (Account account : accounts)
        {
            System.out.println(account.getAccountNumber());
            System.out.println(account.getUsername());
            System.out.println(account.getPassword());
        }
     */
    }
}
