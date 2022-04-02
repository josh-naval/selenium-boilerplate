package utilities.datahandler;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class SpreadsheetObjectWriter {
    private Workbook workbook;

    private Sheet worksheet;

    public static void main(String[] args) throws Exception {
    /*  Sample Code/Usage
        Account account1 = new Account();
        account1.setAccountNumber(123);
        account1.setUsername("sampleUsername");
        //account1.setPassword("password");

        Account account2 = new Account();
        account2.setAccountNumber(1234);
        account2.setUsername("sampleUsername");
        account2.setPassword("password");

        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);

        SpreadsheetObjectWriter writer = new SpreadsheetObjectWriter.Write()
                .thisObject(account1)
                .toSpreadsheet()
                .perform();

        SpreadsheetObjectWriter writer2 = new SpreadsheetObjectWriter.Write()
                .thisObjects(accounts)
                .toSpreadsheet()
                .perform();

        SpreadsheetObjectWriter writer3 = new Write()
                .thisObjects(accounts)
                .toSpreadsheet()
                .saveTo("C:\\Users\\joshuan\\Downloads")
                .perform();*/
    }

    public void initializeWriter() {
        workbook = createWorkbook();
        worksheet = workbook.createSheet();
    }

    public <T> void writeObjects(@NotNull final List<T> objects) {
        if (objects.size() == 0)
            return;

        if (workbook == null || worksheet == null)
            initializeWriter();

        int rowCounter = 1;

        for (T object : objects) {
            writeObject(object, rowCounter);
            rowCounter++;
        }

    }

    public <T> void writeObject(@NotNull final Object object, int rowNumber) {

        if (workbook == null || worksheet == null)
            initializeWriter();

        Field[] fields = object.getClass().getDeclaredFields();

        if (worksheet.getRow(0) == null)
            createColumnHeaders(fields);

        final Row row = worksheet.createRow(rowNumber);

        try {
            int cellCounter = 0;

            for (Field field : fields) {
                field.setAccessible(true);
                final Class<?> clazz = field.getType();

                Cell cell = row.createCell(cellCounter);

                if (clazz.equals(String.class))
                    cell = setStringCellValue(object, field, cell);

                if (clazz.equals(Date.class))
                    cell = setDateCellValue(object, field, cell);

                if (clazz.equals(int.class) || clazz.equals(long.class) || clazz.equals(float.class) || clazz.equals(double.class))
                    cell = setNumericCellValue(clazz, object, field, cell);

                if (clazz.equals(boolean.class))
                    cell = setBooleanCellValue(object, field, cell);

                cellCounter++;
            }

        } catch (final IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

    public void save() {
        String directory = System.getProperty("user.dir");
        save(directory);
    }

    public void save(String filePath) {
        if (workbook == null)
            return;

        Path savePath = Paths.get(filePath, "output.xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(savePath.toAbsolutePath().toString())) {
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    private void createColumnHeaders(Field[] fields) {
        CellStyle headerCellStyle = setHeaderCellStyle();
        Row columnHeader = worksheet.createRow(0);

        for (int i = 0; i < fields.length; i++) {
            Cell cell = columnHeader.createCell(i);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(fields[i].getName());
        }
    }

    private CellStyle setHeaderCellStyle() {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(headerFont);

        return cellStyle;
    }

    private <T> Cell setStringCellValue(final T obj, Field field, Cell cell) throws IllegalAccessException {
        if (field.get(obj) == null)
            return cell;

        String value = field.get(obj).toString();
        cell.setCellValue(value);
        return cell;
    }

    private <T> Cell setDateCellValue(final T obj, Field field, Cell cell) throws IllegalAccessException {
        if (field.get(obj) == null)
            return cell;

        Date date = (Date) field.get(obj);
        cell.setCellValue(date);
        return cell;
    }

    private <T> Cell setBooleanCellValue(final T obj, Field field, Cell cell) throws IllegalAccessException {
        boolean booleanValue = (boolean) field.get(obj);
        cell.setCellValue(booleanValue);
        return cell;
    }

    private <T> Cell setNumericCellValue(final Class<?> clazz, final T obj, Field field, Cell cell) throws IllegalAccessException {
        if (clazz == int.class)
            cell.setCellValue((int) field.get(obj));

        if (clazz == long.class)
            cell.setCellValue((long) field.get(obj));

        if (clazz == float.class)
            cell.setCellValue((float) field.get(obj));

        if (clazz == double.class)
            cell.setCellValue((double) field.get(obj));

        return cell;
    }

    public static class Write {

        private String filePath;

        private List<?> objects;

        private Object object;

        private int rowNumber;

        private SpreadsheetObjectWriter writer;

        private String path;

        public <T> Write thisObjects(List<T> objects) {
            this.objects = objects;
            return this;
        }

        public <T> Write thisObject(Object object) {
            this.object = object;
            return this;
        }

        public Write inRow(int rowNumber) {
            this.rowNumber = rowNumber;
            return this;
        }

        public Write saveTo(String path) {
            this.path = path;
            return this;
        }

        public Write toSpreadsheet() {
            if (object != null) {
                writer = new SpreadsheetObjectWriter();

                if (rowNumber == 0)
                    rowNumber += 1;

                writer.initializeWriter();
                writer.writeObject(object, rowNumber);
                return this;
            }

            if (objects != null) {
                if (objects.size() == 0)
                    return this;

                writer = new SpreadsheetObjectWriter();
                writer.writeObjects(objects);
                return this;
            }

            return this;
        }

        public SpreadsheetObjectWriter perform() {
            if (writer == null) {
                System.out.println("No objects found to write in spreadsheet!");
                return null;
            }

            if (path != null) {
                writer.save(path);
                return writer;
            }

            writer.save();
            return writer;
        }
    }
}