package LottoStat;

import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SheetHandler {
    private XSSFWorkbook excel;
    private Sheet table;

    public SheetHandler(XSSFWorkbook excel) {
        this.excel = excel;
        this.table = excel.getSheetAt(0);
    }

    public Object[][] getAllRows() {
        ArrayList<String[]> values = new ArrayList<String[]>();

        for (int i = 0; i < table.getPhysicalNumberOfRows(); i++) {
            Row row = table.getRow(i);
            values.add(new String[] {String.valueOf((int)row.getCell(0).getNumericCellValue()), String.valueOf((int)row.getCell(1).getNumericCellValue())});
        }
        
        Collections.sort(values, new Comparator<String[]>() { //sorts the values descendingly
            @Override
            public int compare(String[] o1, String[] o2) {
                return Integer.valueOf(o2[1]).compareTo(Integer.valueOf(o1[1]));
            }
        });

        return values.toArray(new Object[][] {});
    }

    public P numberExists(int number) {
        for (int i = 0; i < table.getPhysicalNumberOfRows(); i++) {
            Row row = table.getRow(i);
            Cell titleCell = row.getCell(0);

            if ((int) titleCell.getNumericCellValue() == number)
                return new P(true, i);
        }
        return new P(false, 0);
    }

    public void incrementOrCreate(int value) throws IOException, URISyntaxException {
        P rowWithValue = numberExists(value);

        if (rowWithValue.exists()) {
            Cell foundCell = table.getRow(rowWithValue.rowNum()).getCell(1);
            foundCell.setCellValue(foundCell.getNumericCellValue() + 1);
            update();
        } else {
            table.createRow(table.getPhysicalNumberOfRows()); // counting starts from 1 unlike index, so no need to ++
            Row createdRow = table.getRow(table.getPhysicalNumberOfRows() - 1);

            createdRow.createCell(0);
            createdRow.createCell(1);
            createdRow.getCell(0).setCellValue((double) value);
            createdRow.getCell(1).setCellValue(0);

            incrementOrCreate(value); // re-runs the function and finds the now created column in the if-exists, incrementing it by one
        }
    }

    public void update() throws IOException, URISyntaxException {
        File abs = new File("stats.xlsx");
        FileOutputStream outFile = new FileOutputStream(abs);// null


        excel.write(outFile);
        outFile.close();
    }

    public void eraseData() throws IOException, URISyntaxException {
        if(table.getPhysicalNumberOfRows() > 0) {
            for(int i=0; i<= table.getLastRowNum(); i++){
                Row row = table.getRow(i);
                table.removeRow(row);
            }
        }
        update();
    }
}

class P {
    private boolean exists;
    private int rowNum;

    P(boolean exists, int rowNum) {
        this.exists = exists;
        this.rowNum = rowNum;
    }

    boolean exists() {
        return this.exists;
    };

    int rowNum() {
        return this.rowNum;
    };
}